package arraysort.project.board.app.post.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.IdNotFoundException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.history.service.PostHistoryService;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.*;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;

	private final UserMapper userMapper;

	private final ImageService imageService;

	private final PostHistoryService postHistoryService;

	private final PostComponent postComponent;

	// 게시글 추가
	@Transactional
	public void addPost(PostAddReqDTO dto, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		CategoryVO categoryDetail = postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);
		PostVO vo = PostVO.insertOf(dto, boardId);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		// 게시글 추가
		postMapper.insertPost(vo);

		// 게시글 이미지 업로드
		handlePostImages(dto, boardDetail, vo.getPostId());

		// 게시글 기록 추가(이미지 포함)
		postHistoryService.addPostHistory(vo, categoryDetail.getCategoryName());
	}

	// 게시글 리스트 조회(페이징 적용)
	@Transactional(readOnly = true)
	public PageResDTO<PostListResDTO> findPostListWithPaging(PageReqDTO dto, long boardId) {
		postComponent.getValidatedBoard(boardId);

		int totalPostCount = postMapper.selectTotalPostCount(dto, boardId);
		PageDTO pageDTO = new PageDTO(totalPostCount, dto, boardId);

		List<PostListResDTO> postList = postMapper.selectPostListWithPaging(pageDTO)
				.stream()
				.map(PostListResDTO::of)
				.toList();

		return new PageResDTO<>(totalPostCount, dto.getPage(), postList);
	}

	// 게시글 세부내용 조회, 게시글 조회수 증가
	@Transactional
	public PostDetailResDTO findPostDetailByPostId(long postId, long boardId) {
		postComponent.getValidatedBoard(boardId);

		// 게시글 조회수 증가
		increaseViews(postId);

		return postComponent.getValidatedPost(postId, boardId);
	}

	// 게시글 수정
	@Transactional
	public void modifyPost(PostEditReqDTO dto, long postId, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		CategoryVO categoryDetail = postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);
		PostDetailResDTO postDetail = postComponent.getValidatedPost(postId, boardId);

		// 게시글 소유자 검증
		postComponent.validatePostOwnership(postDetail.getUserId());

		// 이미지 수정
		handlePostImages(dto, boardDetail, postId);

		PostVO vo = PostVO.updateOf(dto, postId);
		// 썸네일 이미지 수정
		vo.updateThumbnailImageId(postDetail.getImageId());

		// 썸네일 이미지 업로드 검증 : 갤러리 게시판인지, 썸네일 이미지가 비어있는지
		if (boardDetail.getBoardType() == BoardType.GALLERY && !dto.getThumbnailImage().isEmpty()) {
			vo.updateThumbnailImageId(imageService.modifyThumbnailImage(dto.getThumbnailImage(), postId));
		}

		// 게시물 수정
		postMapper.updatePost(vo, postId);

		// 게시물 기록 추가(수정)
		postHistoryService.addPostHistory(vo, categoryDetail.getCategoryName());
	}

	// 게시글 삭제
	@Transactional
	public void removePost(long postId, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		PostDetailResDTO postDetail = postComponent.getValidatedPost(postId, boardId);

		// 게시글 소유자 검증
		postComponent.validatePostOwnership(postDetail.getUserId());

		// 게시글 내부 이미지 삭제 처리
		handlePostImageRemove(postId, boardDetail);

		// 갤러리 게시판에서 게시글을 삭제했을 때 썸네일 이미지 삭제
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			imageService.removeThumbnailImage(postId);
		}

		// 게시물 삭제
		postMapper.deletePost(postId);
	}

	// 게시글 작성, 수정 페이지 요청에 대한 사용자 검증
	@Transactional(readOnly = true)
	public void validateAddByUserLevel() {
		if (UserUtil.isAuthenticatedUser()) {
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			if (userDetail.getAccessLevel() < 2) {
				throw new InvalidPrincipalException("게시글을 작성하려면 등급 2 이상이어야 합니다.");
			}
		} else {
			throw new InvalidPrincipalException("게시글을 작성하려면 로그인이 필요합니다.");
		}
	}

	/**
	 * 게시글 리스트에서 게시글 세부내용 조회 시 조회수 증가
	 *
	 * @param postId 세부내용 조회 한 게시글 ID
	 */
	private void increaseViews(long postId) {
		if (postMapper.selectExistPostId(postId).isEmpty()) {
			throw new IdNotFoundException();
		}
		postMapper.updateViews(postId);
	}

	/**
	 * [게시글 이미지 처리(추가)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param dto         추가되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(PostAddReqDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		if (dto.getImages().size() > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		imageService.addImages(dto.getImages(), postId);
	}

	/**
	 * [게시글 이미지 처리(수정)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param dto         수정되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(PostEditReqDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		boolean addedImageCheck = dto.getAddedImages().stream()
				.anyMatch(MultipartFile::isEmpty);

		int addedImageCount = addedImageCheck ? 0 : dto.getAddedImages().size();

		int imageCount = imageService.findImageCountByPostId(postId) + addedImageCount - dto.getRemovedImageIds().size();

		if (imageCount > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		if (!dto.getRemovedImageIds().isEmpty()) {
			imageService.removeImages(dto.getRemovedImageIds());
		}

		imageService.addImages(dto.getAddedImages(), postId);
	}

	/**
	 * [게시글 삭제 시 이미지 삭제 처리]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 *
	 * @param postId      삭제하려는 게시글 ID
	 * @param boardDetail 검증된 게시판 세부정보
	 */
	private void handlePostImageRemove(long postId, BoardVO boardDetail) {
		// 이미지 삭제, 게시글에서 이미지 업로드 허용한 경우
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		List<Long> deletePostImageIds = imageService.findImagesByPostId(postId)
				.stream()
				.map(ImageVO::getImageId)
				.toList();
		if (!deletePostImageIds.isEmpty()) {
			imageService.removeImages(deletePostImageIds);
		}
	}
}
