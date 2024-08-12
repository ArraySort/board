package arraysort.project.board.app.temp.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.DetailNotFoundException;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.post.domain.PageResDTO;
import arraysort.project.board.app.post.domain.PostVO;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.temp.domain.*;
import arraysort.project.board.app.temp.mapper.TempPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TempPostService {

	private final ImageService imageService;

	private final TempPostMapper tempPostMapper;

	private final PostMapper postMapper;

	private final PostComponent postComponent;

	// 임시저장 게시글 추가
	@Transactional
	public void addTempPost(TempPostAddDTO dto, long boardId) {
		TempPostVO vo = TempPostVO.insertOf(dto, boardId);
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType().equals("GALLERY")) {
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
		int offset = (dto.getPage() - 1) * Constants.PAGE_ROW_COUNT;

		List<TempPostListResDTO> tempPostList = tempPostMapper.selectTempPostListWithPaging(
						Constants.PAGE_ROW_COUNT,
						offset,
						dto,
						boardId
				)
				.stream()
				.map(TempPostListResDTO::of)
				.toList();

		return new PageResDTO<>(totalTempPostCount, dto.getPage(), tempPostList);
	}

	// 임시저장 게시글 수정 시 저장된 값 조회
	@Transactional
	public TempPostDetailResDTO findTempPostDetailByPostId(long tempPostId, long boardId) {
		postComponent.getValidatedBoard(boardId);
		return TempPostDetailResDTO.of(tempPostMapper.selectTempPostDetailByPostId(tempPostId, boardId)
				.orElseThrow(DetailNotFoundException::new));
	}

	// 임시저장 게시글에서 일반 게시글로 게시
	@Transactional
	public void publishTempPost(TempPostEditReqDTO dto, long boardId, long tempPostId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		PostVO vo = PostVO.insertOf(dto, boardId);

		// 기존 임시저장 썸네일 이미지
		vo.updateThumbnailImageId(tempPostId);

		// 갤러리 게시판 이고, 썸네일 이미지 수정 시 실행
		if (boardDetail.getBoardType().equals("GALLERY") && !dto.getThumbnailImage().isEmpty()) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		// 임시저장 -> 게시글 게시
		postMapper.insertPost(vo);

		// 임시저장 -> 게시글 이미지 업로드
		imageService.publishTempImages(dto, tempPostId, vo.getPostId());
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
	private void handleTempPostImages(TempPostAddDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag().equals("N")) {
			return;
		}

		if (dto.getImages().size() > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		imageService.addTempImages(dto.getImages(), postId);
	}
}
