package arraysort.project.board.app.component;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.mapper.BoardMapper;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.exception.*;
import arraysort.project.board.app.post.domain.PostDetailResDTO;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PostComponent {

	private final BoardMapper boardMapper;

	private final CategoryMapper categoryMapper;

	private final PostMapper postMapper;

	private final UserMapper userMapper;

	/**
	 * [게시판 검증]
	 * 1. 게시판 존재 검증
	 * 2. 게시판 상태 검증(비활성화, 삭제 상태)
	 *
	 * @param boardId 현재 접속한 페이지의 게시판 ID
	 * @return 1, 2, 3 번이 검증된 게시판의 세부정보
	 */
	@Transactional(readOnly = true)
	public BoardVO getValidatedBoard(long boardId) {
		// 1. 게시간 존재 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 게시판 상태 검증(비활성화, 삭제)
		if (boardDetail.getActivateFlag() == Flag.N || boardDetail.getDeleteFlag() == Flag.Y) {
			throw new BoardNotFoundException();
		}

		validatePermissionForBoard(boardDetail);

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
	@Transactional(readOnly = true)
	public CategoryVO getValidatedCategory(Long categoryId, BoardVO boardDetail) {
		// 1. 게시글 추가 시 선택한 카테고리가 존재하는지 검증
		CategoryVO categoryDetail = categoryMapper.selectCategoryDetailById(categoryId)
				.orElseThrow(CategoryNotFoundException::new);

		// 2. 게시글에서 고른 카테고리가 게시판에서 설정한 값과 일치하지 않을 때
		if (!Objects.equals(categoryDetail.getBoardId(), boardDetail.getBoardId())) {
			throw new InvalidPrincipalException("올바르지 않은 카테고리입니다.");
		}

		return categoryDetail;
	}

	/**
	 * [사용자 검증]
	 * 1. 로그인 한 사용자 존재 검증
	 * 2. 사용자 상태 검증(비활성화, 삭제 상태)
	 * 3. 게시판 설정(접근 가능 등급) <-> 사용자 접근 가능 등급 검증
	 * 4. 로그인하지 않은 사용자
	 * (로그인하지 않은 사용자는 게시판 설정(접근 가능 등급) 에 따라서 접근 가능)
	 *
	 * @param boardDetail 검증된 게시판 세부정보
	 */
	private void validatePermissionForBoard(BoardVO boardDetail) {
		if (UserUtil.isUser()) {
			// 1. 로그인 한 사용자 존재 검증
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			// 2. 사용자 상태 검증(비활성화, 삭제 상태)
			if (userDetail.getActivateFlag() == Flag.N || userDetail.getDeleteFlag() == Flag.Y) {
				throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
			}

			// 3. 게시판 설정(접근 가능 등급) <-> 사용자 접근 가능 등급 검증
			if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel())) {
				throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
			}
			// 4. 로그인 하지 않은 사용자
		} else if (!UserUtil.isAdmin() && boardDetail.getAccessLevel() != 0) {
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
	@Transactional(readOnly = true)
	public PostDetailResDTO getValidatedPost(long postId, long boardId) {
		// 1. 게시글이 존재 검증
		PostDetailResDTO postDetail = PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId, boardId, UserUtil.getCurrentLoginUserId())
				.orElseThrow(DetailNotFoundException::new));

		// 2. 게시글 상태 검증(삭제, 비활성화 상태)
		if (!UserUtil.isAdmin()) {
			if (postDetail.getActivateFlag() == Flag.N || postDetail.getDeleteFlag() == Flag.Y) {
				throw new DetailNotFoundException();
			}
		}

		validatePrivatePost(postDetail);

		return postDetail;
	}

	/**
	 * [게시글 수정, 삭제 검증]
	 * 1. 게시글을 수정, 삭제하려는 사용자가 게시글 소유자인지 검증
	 *
	 * @param userId 게시글 소유자의 ID
	 */
	public void validatePostOwnership(String userId) {
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
		if (postDetail.getPrivateFlag() == Flag.Y) {
			String currentUserId = UserUtil.getCurrentLoginUserId();
			String ownerId = UserUtil.isUser() ? postDetail.getUserId() : postDetail.getAdminId();

			if (!Objects.equals(ownerId, currentUserId)) {
				throw new InvalidPrincipalException("비공개 게시글은 작성자만 볼 수 있습니다.");
			}
		}
	}
}
