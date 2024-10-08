package arraysort.project.board.app.post.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.comment.service.CommentService;
import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.common.page.PageResDTO;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.DetailNotFoundException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.history.service.PostHistoryService;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.*;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.user.service.UserPointService;
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

	private final ImageService imageService;

	private final PostHistoryService postHistoryService;

	private final CommentService commentService;

	private final UserPointService userPointService;

	private final PostComponent postComponent;

	private final PostMapper postMapper;

	private final UserMapper userMapper;

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

		// 게시글 추가 시 유저 포인트 지급
		userPointService.giveUserPointForPost();

		// 게시글 이미지 업로드
		handlePostImages(dto.getImages(), boardDetail, vo.getPostId());

		// 게시글 기록 추가(이미지 포함)
		postHistoryService.addPostHistory(vo, categoryDetail.getCategoryName());
	}

	// 게시글 리스트 조회(페이징 적용)
	@Transactional(readOnly = true)
	public PageResDTO<PostListResDTO> findPostListWithPaging(PageReqDTO dto, long boardId) {
		postComponent.getValidatedBoard(boardId);

		int totalPostCount = postMapper.selectTotalPostCount(dto, boardId);
		PageDTO pageDTO = new PageDTO(dto, boardId);

		List<PostListResDTO> postList = postMapper.selectPostListWithPaging(pageDTO)
				.stream()
				.map(PostListResDTO::of)
				.toList();

		return new PageResDTO<>(totalPostCount, dto.getPage(), postList);
	}

	// 게시글 세부내용 조회
	@Transactional
	public PostDetailResDTO findPostDetailByPostId(long postId, long boardId) {
		postComponent.getValidatedBoard(boardId);
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
		handlePostImages(dto.getAddedImages(), dto.getRemovedImageIds(), boardDetail, postId);

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

		// 댓글 삭제(이미지 포함)
		commentService.removeCommentByPostRemove(boardId, postId);

		// 게시글 삭제
		postMapper.deletePost(postId);
	}

	// 사용자 홈페이지 : 전체 게시글 중 일부분 조회
	@Transactional(readOnly = true)
	public List<PostListResDTO> findRecentPostsByBoardId(long boardId, int postCount) {
		return postMapper.selectRecentPostsByBoardId(boardId, postCount)
				.stream()
				.map(PostListResDTO::of)
				.toList();
	}

	// 관리자 : 관리자 홈페이지 최근 게시글 조회
	@Transactional(readOnly = true)
	public List<PostListResDTO> findRecentPosts(int postCount) {
		return postMapper.selectRecentPosts(postCount)
				.stream()
				.map(PostListResDTO::of)
				.toList();
	}

	// 관리자 : 게시글 활성화 상태 변경
	@Transactional
	public void modifyActivateFlag(long boardId, long postId) {
		// 게시판 검증
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);

		// 게시글 검증
		PostVO vo = postMapper.selectPostDetailByPostId(postId, boardId, UserUtil.getCurrentLoginUserId())
				.orElseThrow(DetailNotFoundException::new);

		if (vo.getActivateFlag() == Flag.Y) {
			// 비활성화 상태로 변경
			postMapper.updateActivateFlag(boardId, postId, Flag.N);
		} else {
			// 활성화 상태로 변경
			validateBoardNoticeCount(boardId, vo.getNoticeFlag(), boardDetail.getNoticeCount());
			postMapper.updateActivateFlag(boardId, postId, Flag.Y);
		}
	}

	// 관리자 : 게시글 추가
	@Transactional
	public void addAdminPost(PostAddAdminReqDTO dto, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		CategoryVO categoryDetail = postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);
		PostVO vo = PostVO.insertAdminOf(dto, boardId);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		// 게시판에 설정된 공지사항 개수 검증
		validateBoardNoticeCount(boardId, dto.getNoticeFlag(), boardDetail.getNoticeCount());

		// 게시글 추가
		postMapper.insertPost(vo);

		// 게시글 이미지 업로드
		handlePostImages(dto.getImages(), boardDetail, vo.getPostId());

		// 게시글 기록 추가(이미지 포함)
		postHistoryService.addPostHistory(vo, categoryDetail.getCategoryName());
	}

	// 관리자 : 게시글 수정
	@Transactional
	public void modifyAdminPost(PostEditAdminReqDTO dto, long postId, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		CategoryVO categoryDetail = postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);
		PostDetailResDTO postDetail = postComponent.getValidatedPost(postId, boardId);

		// 게시글 소유자 검증
		postComponent.validatePostOwnership(postDetail.getAdminId());

		// 게시판에 설정된 공지사항 개수 검증
		validateBoardNoticeCount(boardId, dto.getNoticeFlag(), boardDetail.getNoticeCount());

		// 이미지 수정
		handlePostImages(dto.getAddedImages(), dto.getRemovedImageIds(), boardDetail, postId);

		PostVO vo = PostVO.updateAdminOf(dto, postId);
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

	// 관리자 : 게시글 삭제
	@Transactional
	public void removeAdminPost(long postId, long boardId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		PostDetailResDTO postDetail = postComponent.getValidatedPost(postId, boardId);

		// 게시글 소유자 검증
		postComponent.validatePostOwnership(postDetail.getAdminId());

		// 게시글 내부 이미지 삭제 처리
		handlePostImageRemove(postId, boardDetail);

		// 갤러리 게시판에서 게시글을 삭제했을 때 썸네일 이미지 삭제
		if (boardDetail.getBoardType() == BoardType.GALLERY) {
			imageService.removeThumbnailImage(postId);
		}

		// 댓글 삭제(이미지 포함)
		commentService.removeCommentByPostRemove(boardId, postId);

		// 게시글 삭제
		postMapper.deletePost(postId);
	}

	// 관리자 : 총 게시글 조회수 조회
	@Transactional(readOnly = true)
	public long findAllViews() {
		return postMapper.selectAllViews();
	}

	// 관리자 : 총 게시글 수 조회
	@Transactional(readOnly = true)
	public long findAllPostsCount() {
		return postMapper.selectAllPostsCount();
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
	 * [게시글 이미지 처리(추가)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param images      추가되는 게시글 이미지 리스트
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(List<MultipartFile> images, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		if (images.size() > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		imageService.addImages(images, postId);
	}

	/**
	 * [게시글 이미지 처리(수정)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param addedImages 추가된 이미지 리스트
	 * @param removedIds  삭제된 이미지 리스트
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(List<MultipartFile> addedImages, List<Long> removedIds, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		boolean addedImageCheck = addedImages.stream()
				.anyMatch(MultipartFile::isEmpty);

		int addedImageCount = addedImageCheck ? 0 : addedImages.size();

		int imageCount = imageService.findImageCountByPostId(postId) + addedImageCount - removedIds.size();

		if (imageCount > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		if (!removedIds.isEmpty()) {
			imageService.removeImages(removedIds);
		}

		imageService.addImages(addedImages, postId);
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

	/**
	 * 관리자 : 공지사항 게시글 추가, 활성화 변경 시 게시판에 설정된 공지사항 수 검증
	 *
	 * @param boardId     게시글을 추가하려는 게시판 ID
	 * @param noticeFlag  공지사항 여부
	 * @param noticeCount 게시판에 설정된 공지사항 수
	 */
	private void validateBoardNoticeCount(long boardId, Flag noticeFlag, int noticeCount) {
		if (noticeFlag == Flag.Y &&
				noticeCount <= postMapper.selectNoticePostCount(boardId)) {
			throw new InvalidPrincipalException("게시판의 최대 공지사항 개수를 초과합니다.");
		}
	}
}
