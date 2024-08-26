package arraysort.project.board.app.component;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.comment.domain.CommentAdoptReqDTO;
import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.comment.mapper.CommentMapper;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.exception.CommentNotFoundException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.post.domain.PostDetailResDTO;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentComponent {

	private final PostComponent postComponent;

	private final CommentMapper commentMapper;


	/**
	 * 게시판, 게시글 검증(상태, 존재, 게시판 접근등급)
	 *
	 * @param boardId 검증하려는 게시판 ID
	 * @param postId  검증하려는 게시글 ID
	 */
	public void validateBoardAndPost(long boardId, long postId) {
		// 게시판 상태 검증, 게시판 접근 등급 검증
		postComponent.getValidatedBoard(boardId);

		// 게시글 상태 검증, 게시글 비활성화 여부
		postComponent.getValidatedPost(postId, boardId);
	}

	/**
	 * 댓글 추가 전 검증
	 * 1. 게시판 댓글 허용 여부 검증
	 * 2. 로그인하지 않은 사용자에 대한 검증
	 * 3. 게시글 검증(존재, 상태)
	 * (댓글은 UserAccessLevel = 1 부터 가능, 비로그인은 0)
	 *
	 * @param boardId 현재 댓글을 작성하려는 게시판 ID
	 * @param postId  현재 댓글을 작성하려는 게시글 ID
	 */
	public void validateAdd(BoardVO boardDetail, long boardId, long postId) {
		// 1. 게시판 댓글 허용 여부 검증
		if (boardDetail.getCommentFlag() == Flag.N) {
			throw new InvalidPrincipalException("현재 게시판은 댓글을 허용하지 않습니다. ");
		}

		// 2. 로그인하지 않은 사용자에 대한 검증
		if (!UserUtil.isAuthenticatedUser()) {
			throw new InvalidPrincipalException("로그인이 필요합니다.");
		}

		// 3. 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);
	}

	/**
	 * 댓글 검증(수정, 삭제)
	 * 1. 댓글 검증(존재, 상태)
	 * 2. 댓글 소유자 검증
	 *
	 * @param commentId 수정하려는 댓글 ID
	 */
	public void validateComment(long commentId) {
		// 1. 댓글 검증(존재, 상태)
		CommentVO commentDetail = getValidatedComment(commentId);

		// 2. 댓글 소유자 검증
		validateCommentOwnership(commentDetail);
	}

	/**
	 * 댓글 채택 전 검증
	 * 1. 댓글 검증(존재, 상태)
	 * 2. 채택 댓글 사용자 검증
	 * (채택당하는 댓글의 소유자 == 게시글 소유자, 채택하려는 사용자 != 게시글 소유자, 비로그인 경우 제한)
	 *
	 * @param dto        채택당하는 댓글의 정보
	 * @param postDetail 검증된 게시글 세부정보
	 */
	public void validateAdoptComment(CommentAdoptReqDTO dto, PostDetailResDTO postDetail) {
		// 1. 댓글 검증(존재, 상태)
		CommentVO commentDetail = getValidatedComment(dto.getCommentId());

		// 3. 채택하려는 댓글이 게시글 소유자거나 인증되지 않은 사용자 인 경우, 채택하려는 사용자가 게시글 소유자가 아닌 경우
		if (UserUtil.isCurrentUserOwner(commentDetail.getUserId()) || UserUtil.isNotAuthenticatedUser() || UserUtil.isNotCurrentUserOwner(postDetail.getUserId())) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}
	}

	/**
	 * 댓글 검증
	 * 1. 댓글 존재 검증
	 * 2. 댓글 상태 검증
	 *
	 * @param commentId 검증하려는 댓글 ID
	 * @return 검증된 댓글 세부정보
	 */
	private CommentVO getValidatedComment(long commentId) {
		// 1. 댓글 존재 검증
		CommentVO commentDetail = commentMapper.selectCommentById(commentId)
				.orElseThrow(CommentNotFoundException::new);

		// 2. 댓글 상태 검증
		if (commentDetail.getActivateFlag() == Flag.N || commentDetail.getDeleteFlag() == Flag.Y) {
			throw new CommentNotFoundException();
		}
		return commentDetail;
	}

	/**
	 * 댓글 소유자 검증
	 *
	 * @param commentDetail 현재 수정/삭제 하려는 댓글의 세부 정보
	 */
	private void validateCommentOwnership(CommentVO commentDetail) {
		if (UserUtil.isNotCurrentUserOwner(commentDetail.getUserId())) {
			throw new InvalidPrincipalException("본인이 작성한 댓글만 수정/삭제가 가능합니다.");
		}
	}
}
