package arraysort.project.board.app.temp.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.common.page.PageResDTO;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.DetailNotFoundException;
import arraysort.project.board.app.history.service.PostHistoryService;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.PostVO;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.temp.domain.*;
import arraysort.project.board.app.temp.mapper.TempPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TempPostService {

	private final ImageService imageService;

	private final PostHistoryService postHistoryService;

	private final TempPostMapper tempPostMapper;

	private final PostMapper postMapper;

	private final PostComponent postComponent;

	// 임시저장 게시글 추가
	@Transactional
	public void addTempPost(TempPostAddReqDTO dto, long boardId) {
		TempPostVO vo = TempPostVO.insertOf(dto, boardId);
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		// 임시저장 게시글 추가
		tempPostMapper.insertTempPost(vo);

		// 임시저장 게시글 이미지 업로드
		handleTempPostImages(dto, boardDetail, vo.getTempPostId());
	}

	// 임시 저장 게시글 목록 조회
	@Transactional(readOnly = true)
	public PageResDTO<TempPostListResDTO> findTempPostListWithPaging(PageReqDTO dto, long boardId) {
		postComponent.getValidatedBoard(boardId);

		int totalTempPostCount = tempPostMapper.selectTotalTempPostCount(dto, boardId);
		PageDTO pageDTO = new PageDTO(dto, boardId);

		List<TempPostListResDTO> tempPostList = tempPostMapper.selectTempPostListWithPaging(pageDTO)
				.stream()
				.map(TempPostListResDTO::of)
				.toList();

		return new PageResDTO<>(totalTempPostCount, dto.getPage(), tempPostList);
	}

	// 임시저장 게시글 수정 시 저장된 값 조회
	@Transactional
	public TempPostDetailResDTO findTempPostDetailByPostId(long boardId, long tempPostId) {
		postComponent.getValidatedBoard(boardId);

		// 임시저장 게시글 존재 검증
		return TempPostDetailResDTO.of(tempPostMapper.selectTempPostDetailByPostId(tempPostId, boardId)
				.orElseThrow(DetailNotFoundException::new));
	}

	// 임시저장 게시글에서 일반 게시글로 게시
	@Transactional
	public void publishTempPost(TempPostPublishReqDTO dto, long boardId, long tempPostId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		CategoryVO categoryDetail = postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);
		TempPostVO tempPostDetail = tempPostMapper.selectTempPostDetailByPostId(tempPostId, boardId)
				.orElseThrow(DetailNotFoundException::new);

		// 임시저장 게시글 소유자 검증
		postComponent.validatePostOwnership(tempPostDetail.getUserId());

		PostVO vo = PostVO.insertOf(dto, boardId);

		// 임시저장 게시글 썸네일 이미지 처리 -> 변경 여부 반환
		boolean isThumbnailChanged = handleThumbnailImage(dto, vo, tempPostDetail, boardDetail);

		// 임시저장 -> 게시글 게시
		postMapper.insertPost(vo);

		// 임시저장 -> 게시글 이미지 업로드
		imageService.publishTempImages(dto, tempPostId, vo.getPostId());

		// 임시저장 게시글 삭제
		tempPostMapper.deleteTempPost(tempPostId);

		// 썸네일이 변경된 경우 기존 임시저장 썸네일 이미지 삭제
		if (isThumbnailChanged) {
			imageService.removeTempThumbnailImage(tempPostDetail.getImageId());
		}

		// 임시저장 -> 게시글 게시 완료 시 기록 추가
		postHistoryService.addPostHistory(vo, categoryDetail.getCategoryName());
	}

	// 임시저장 게시글 수정
	@Transactional
	public void modifyTempPost(TempPostEditReqDTO dto, long boardId, long tempPostId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		TempPostVO tempPostDetail = tempPostMapper.selectTempPostDetailByPostId(tempPostId, boardId)
				.orElseThrow(DetailNotFoundException::new);

		// 임시저장 게시글 소유자 검증
		postComponent.validatePostOwnership(tempPostDetail.getUserId());

		// 임시저장 게시글 이미지 처리
		handleTempPostImages(dto, boardDetail, tempPostId);

		TempPostVO vo = TempPostVO.updateOf(dto, tempPostId);

		// 임시저장 게시글 썸네일 이미지 처리 -> 변경 여부 반환
		boolean isThumbnailChanged = handleThumbnailImage(dto, vo, tempPostDetail, boardDetail, tempPostId);

		// 임시저장 게시물 수정
		tempPostMapper.updateTempPost(vo, tempPostId);

		// 썸네일이 변경된 경우 기존 임시저장 썸네일 이미지 삭제
		if (isThumbnailChanged) {
			imageService.removeTempThumbnailImage(tempPostDetail.getImageId());
		}

	}

	// 임시저장 게시글 삭제
	@Transactional
	public void removeTempPost(long boardId, long tempPostId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		TempPostVO tempPostDetail = tempPostMapper.selectTempPostDetailByPostId(tempPostId, boardId)
				.orElseThrow(DetailNotFoundException::new);

		// 임시저장 게시글 소유자 검증
		postComponent.validatePostOwnership(tempPostDetail.getUserId());

		// 임시저장 게시글 내부 이미지 삭제 처리
		handleTempPostImageRemove(tempPostId, boardDetail);

		// 갤러리 게시판일 때 썸네일 이미지 삭제
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			imageService.removeTempThumbnailImage(tempPostDetail.getImageId());
		}

		// 임시저장 게시글 삭제
		tempPostMapper.deleteTempPost(tempPostId);
	}


	/**
	 * [임시저장 게시글 이미지 처리(추가)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param dto         추가되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handleTempPostImages(TempPostAddReqDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		if (dto.getImages().size() > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		imageService.addTempImages(dto.getImages(), postId);
	}

	/**
	 * [게시글 이미지 처리(수정)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param dto         수정되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param tempPostId  게시글 ID
	 */
	private void handleTempPostImages(TempPostEditReqDTO dto, BoardVO boardDetail, long tempPostId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		// 이미지 추가, 삭제에 대한 총 이미지 업로드 수 검증
		validateImageCount(dto, boardDetail, tempPostId);

		if (!dto.getRemovedImageIds().isEmpty()) {
			imageService.removeTempImages(dto.getRemovedImageIds(), tempPostId);
		}

		imageService.addTempImages(dto.getAddedImages(), tempPostId);
	}

	/**
	 * [임시저장 게시글 삭제 시 이미지 삭제 처리]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 *
	 * @param tempPostId  삭제하려는 임시저장 게시글 ID
	 * @param boardDetail 검증된 게시판 세부정보
	 */
	private void handleTempPostImageRemove(long tempPostId, BoardVO boardDetail) {
		// 이미지 삭제, 게시글에서 이미지 업로드 허용한 경우
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		List<Long> deletePostImageIds = imageService.findImagesByTempPostId(tempPostId)
				.stream()
				.map(ImageVO::getImageId)
				.toList();

		if (!deletePostImageIds.isEmpty()) {
			imageService.removeTempImages(deletePostImageIds, tempPostId);
		}
	}

	/**
	 * 썸네일 이미지 처리
	 * 기존 썸네일 이미지 저장
	 * 게시판 타입에 대한 검증 후 썸네일 이미지가 변경된 경우 변경 여부 반환
	 *
	 * @param dto            임시저장 -> 게시글 변환 DTO
	 * @param vo             게시되는 게시글 VO
	 * @param tempPostDetail 검증된 임시저장 게시글 세부정보
	 * @param boardDetail    검증된 게시판 세부정보
	 * @return 썸네일 이미지 변경 여부
	 */
	private boolean handleThumbnailImage(TempPostPublishReqDTO dto, PostVO vo, TempPostVO tempPostDetail, BoardVO boardDetail) {
		boolean isThumbnailChanged = false;

		// 기존 임시저장 썸네일 이미지
		vo.updateThumbnailImageId(tempPostDetail.getImageId());

		// 갤러리 게시판 검증, 썸네일 이미지 수정 시 실행
		if (boardDetail.getBoardType() == BoardType.GALLERY && !dto.getThumbnailImage().isEmpty()) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
			isThumbnailChanged = true;
		}
		return isThumbnailChanged;
	}

	/**
	 * 썸네일 이미지 처리
	 * 기존 썸네일 이미지 저장
	 * 게시판 타입에 대한 검증 후 썸네일 이미지가 변경된 경우 변경 여부 반환
	 *
	 * @param dto            임시저장 게시글 수정 DTO
	 * @param tempPostId     임시저장 게시글 ID
	 * @param vo             임시저장 게시글 VO
	 * @param tempPostDetail 검증된 임시저장 게시글 세부정보
	 * @param boardDetail    검증된 게시판 세부정보
	 * @return 썸네일 이미지 변경 여부
	 */
	private boolean handleThumbnailImage(TempPostEditReqDTO dto, TempPostVO vo, TempPostVO tempPostDetail, BoardVO boardDetail, long tempPostId) {
		boolean isThumbnailChanged = false;
		// 기존 썸네일 이미지
		vo.updateThumbnailImageId(tempPostDetail.getImageId());

		// 썸네일 이미지 업로드 검증 : 갤러리 게시판인지, 썸네일 이미지가 비어있는지
		if (boardDetail.getBoardType() == BoardType.GALLERY && !dto.getThumbnailImage().isEmpty()) {
			vo.updateThumbnailImageId(imageService.modifyThumbnailImage(dto.getThumbnailImage(), tempPostId));
			isThumbnailChanged = true;
		}
		return isThumbnailChanged;
	}

	/**
	 * 이미지 총 개수 검증
	 * 추가된 이미지, 기존이미지, 삭제한 이미지에 대한 업로드 수 검증
	 *
	 * @param dto         Edit 시 받아오는 DTO
	 * @param boardDetail 검증된 게시판 정보
	 * @param tempPostId  수정하는 임시저장 게시글 ID
	 */
	private void validateImageCount(TempPostEditReqDTO dto, BoardVO boardDetail, long tempPostId) {
		boolean addedImageCheck = dto.getAddedImages().stream()
				.anyMatch(MultipartFile::isEmpty);

		int addedImageCount = addedImageCheck ? 0 : dto.getAddedImages().size();

		int imageCount = imageService.findTempImageCountByTempPostId(tempPostId) + addedImageCount - dto.getRemovedImageIds().size();

		if (imageCount > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}
	}
}
