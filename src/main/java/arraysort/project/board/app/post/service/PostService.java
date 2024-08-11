package arraysort.project.board.app.post.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.mapper.BoardMapper;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.exception.*;
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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;

	private final BoardMapper boardMapper;

	private final CategoryMapper categoryMapper;

	private final UserMapper userMapper;

	private final ImageService imageService;

	// 게시글 추가
	@Transactional
	public void addPost(PostAddReqDTO dto, long boardId, String boardType) {
		PostVO vo = PostVO.insertOf(dto, boardId);
		BoardVO boardDetail = validateBoard(boardId, boardType);

		validateCategory(dto.getCategoryId(), boardDetail);
		validateUserPermissionForBoard(boardDetail);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType().equals("GALLERY")) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		postMapper.insertPost(vo);
		handlePostImages(dto, boardDetail, vo.getPostId());
	}

	// 게시글 리스트 조회(페이징 적용)
	@Transactional(readOnly = true)
	public PageResDTO findPostListWithPaging(PageReqDTO dto, long boardId, String boardType) {
		BoardVO boardDetail = validateBoard(boardId, boardType);
		validateUserPermissionForBoard(boardDetail);

		int totalPostCount = postMapper.selectTotalPostCount(dto, boardId);
		int offset = (dto.getPage() - 1) * Constants.PAGE_ROW_COUNT;

		List<PostListResDTO> postList = postMapper.selectPostListWithPaging(
						Constants.PAGE_ROW_COUNT,
						offset,
						dto,
						boardId
				)
				.stream()
				.map(PostListResDTO::of)
				.toList();

		return new PageResDTO(totalPostCount, dto.getPage(), postList);
	}

	// 게시글 세부내용 조회, 게시글 조회수 증가
	@Transactional
	public PostDetailResDTO findPostDetailByPostId(long postId, long boardId, String boardType) {
		increaseViews(postId);

		BoardVO boardDetail = validateBoard(boardId, boardType);
		validateUserPermissionForBoard(boardDetail);

		PostDetailResDTO postDetail = validatePost(postId, boardId);
		validatePrivatePost(postDetail);

		return postDetail;
	}

	// 게시글 수정
	@Transactional
	public void modifyPost(PostEditReqDTO dto, long postId, long boardId, String boardType) {
		BoardVO boardDetail = validateBoard(boardId, boardType);
		validateCategory(dto.getCategoryId(), boardDetail);
		validateUserPermissionForBoard(boardDetail);

		PostDetailResDTO postDetail = validatePost(postId, boardId);
		validatePostOwnership(postDetail.getUserId());

		// 이미지 수정 처리
		handlePostImages(dto, boardDetail, postId);

		PostVO vo = PostVO.updateOf(dto);
		vo.updateThumbnailImageId(postDetail.getImageId());

		// 썸네일 이미지 업로드 검증 : 갤러리 게시판인지, 썸네일 이미지가 비어있는지
		if (boardDetail.getBoardType().equals("GALLERY") && !dto.getThumbnailImage().isEmpty()) {
			vo.updateThumbnailImageId(imageService.modifyThumbnailImage(dto.getThumbnailImage(), postId));
		}

		postMapper.updatePost(vo, postId);
	}

	// 게시글 삭제
	@Transactional
	public void removePost(long postId, long boardId, String boardType) {
		BoardVO boardDetail = validateBoard(boardId, boardType);
		validateUserPermissionForBoard(boardDetail);

		PostDetailResDTO postDetail = validatePost(postId, boardId);
		validatePostOwnership(postDetail.getUserId());

		// 게시글 내부 이미지 삭제 처리
		handlePostImageRemove(postId, boardDetail);

		// 갤러리 게시판에서 게시글을 삭제했을 때 썸네일 이미지 삭제
		if (Objects.equals(boardDetail.getBoardType(), "GALLERY")) {
			imageService.removeThumbnailImage(postId);
		}

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

	// 게시글 작성 페이지에 대한 게시판 타입 검증
	@Transactional(readOnly = true)
	public void validateAddByBoardType(long boardId, String boardType) {
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId).orElseThrow(BoardNotFoundException::new);

		if (!Objects.equals(boardDetail.getBoardType(), boardType.toUpperCase())) {
			throw new BoardNotFoundException();
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
	 * [게시판 검증]
	 * 1. 게시판 존재 검증
	 * 2. 게시판 상태 검증(비활성화, 삭제 상태)
	 * 3. 접속한 경로의 게시판 타입 검증
	 *
	 * @param boardId   현재 접속한 페이지의 게시판 ID
	 * @param boardType 현재 접속한 페이지의 게시판 Type
	 * @return 1, 2, 3 번이 검증된 게시판의 세부정보
	 */
	private BoardVO validateBoard(long boardId, String boardType) {
		// 1. 게시글을 추가하려는 게시판 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 게시글을 추가하려는 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// 3. 현재 접속한 경로의 게시판의 타입 검증
		if (!boardDetail.getBoardType().equals(boardType.toUpperCase())) {
			throw new BoardNotFoundException();
		}
		return boardDetail;
	}

	/**
	 * [카테고리 검증]
	 * 1. 카테고리 존재 검증
	 * 2. 게시판 설정(게시판에 지정된 카테고리) <-> 선택한 카테고리 검증
	 *
	 * @param categoryId  카테고리 ID
	 * @param boardDetail 게시판 세부정보
	 */
	private void validateCategory(Long categoryId, BoardVO boardDetail) {
		// 1. 게시글 추가 시 선택한 카테고리가 존재하는지 검증
		CategoryVO categoryDetail = categoryMapper.selectCategoryDetailById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

		// 2. 게시글에서 고른 카테고리가 게시판에서 설정한 값과 일치하지 않을 때
		if (!Objects.equals(categoryDetail.getBoardId(), boardDetail.getBoardId())) {
			throw new InvalidPrincipalException("올바르지 않은 카테고리입니다.");
		}
	}

	/**
	 * [사용자 검증]
	 * 1. 로그인 한 사용자인지 검증
	 * 2. 사용자 상태 검증(비활성화, 삭제 상태)
	 * 3. 게시판 설정(접근 가능 등급) <-> 사용자 접근 가능 등급 검증
	 * (로그인하지 않은 사용자는 게시판 설정(접근 가능 등급) 에 따라서 접근 가능)
	 *
	 * @param boardDetail 검증된 게시판 세부정보
	 */

	private void validateUserPermissionForBoard(BoardVO boardDetail) {
		if (UserUtil.isAuthenticatedUser()) {
			// 1. 게시글을 추가할 때 작성자가 현재 로그인 한 사용자인지 검증
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			// 2. 게시글을 추가할 때 작성자가 비활성화 상태, 삭제된 상태인지 검증
			if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
				throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
			}

			// 3. 게시글을 추가하려는 게시판의 접근 가능한 사용자 등급이 현재 작성자의 등급보다 높은 경우, 작성자의 등급이 작성 가능 등급보다 낮은 경우 검증
			if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel()) || userDetail.getAccessLevel() < 2) {
				throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
			}
		} else if (boardDetail.getAccessLevel() != 0) {
			throw new InvalidPrincipalException("로그인이 필요합니다");
		}
	}

	/**
	 * [게시글 검증]
	 * 1. 게시글 존재 검증
	 * 2. 게시글 상태 검증
	 *
	 * @param postId  현재 게시글 ID
	 * @param boardId 현재 게시판 ID
	 * @return 검증된 게시글 세부정보
	 */
	private PostDetailResDTO validatePost(long postId, long boardId) {
		// 1. 조회 한 게시글이 존재하는지 검증
		PostDetailResDTO postDetail = PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId, boardId)
				.orElseThrow(DetailNotFoundException::new));

		// 2. 조회 한 게시글의 삭제, 비활성화 여부 검증
		if (postDetail.getActivateFlag().equals("N") || postDetail.getDeleteFlag().equals("Y")) {
			throw new DetailNotFoundException();
		}
		return postDetail;
	}

	/**
	 * [게시글 수정, 삭제 검증]
	 * 1. 게시글을 수정, 삭제하려는 사용자가 게시글 소유자인지 검증
	 *
	 * @param userId 게시글 소유자의 ID
	 */
	private void validatePostOwnership(String userId) {
		// 1. 게시글을 수정, 삭제하려는 사용자가 게시글 소유자인지 검증
		if (!Objects.equals(userId, UserUtil.getCurrentLoginUserId())) {
			throw new IdNotFoundException();
		}
	}

	/**
	 * [게시글 비공개 검증]
	 * 1. 현재 조회하는 게시글이 비공개 게시글인지 검증
	 *
	 * @param postDetail 검증된 게시글 세부정보
	 */
	private void validatePrivatePost(PostDetailResDTO postDetail) {
		// 1. 현재 조회하는 게시글이 비공개 게시글인지 검증
		if (postDetail.getPrivateFlag().equals("Y") &&
				!Objects.equals(postDetail.getUserId(), UserUtil.getCurrentLoginUserId())) {
			throw new InvalidPrincipalException("비공개 게시글은 작성자만 볼 수 있습니다.");
		}
	}

	/**
	 * [게시판 이미지 처리(추가)]
	 *
	 * @param dto         추가되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(PostAddReqDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag().equals("Y")) {
			if (dto.getImages().size() > boardDetail.getImageLimit()) {
				throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
			}
			imageService.addImages(dto.getImages(), postId);
		}
	}

	/**
	 * [게시판 이미지 처리(수정)]
	 *
	 * @param dto         수정되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handlePostImages(PostEditReqDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag().equals("Y")) {
			int imageCount = imageService.findImageCountByPostId(postId) + dto.getAddedImages().size() - dto.getRemovedImageIds().size();

			if (imageCount > boardDetail.getImageLimit()) {
				throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
			}

			if (!dto.getRemovedImageIds().isEmpty()) {
				imageService.removeImages(dto.getRemovedImageIds());
			}

			imageService.addImages(dto.getAddedImages(), postId);
		}
	}

	/**
	 * [게시글 삭제 시 이미지 삭제 처리]
	 *
	 * @param postId      삭제하려는 게시글 ID
	 * @param boardDetail 검증된 게시판 세부정보
	 */
	private void handlePostImageRemove(long postId, BoardVO boardDetail) {
		// 이미지 삭제, 게시글에서 이미지 업로드 허용한 경우
		if (Objects.equals(boardDetail.getImageFlag(), "Y")) {
			List<Long> deletePostImageIds = imageService.findImagesByPostId(postId)
					.stream()
					.map(ImageVO::getImageId)
					.toList();
			if (!deletePostImageIds.isEmpty()) {
				imageService.removeImages(deletePostImageIds);
			}
		}
	}
}
