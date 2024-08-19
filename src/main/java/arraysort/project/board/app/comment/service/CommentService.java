package arraysort.project.board.app.comment.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.comment.domain.CommentAddReqDTO;
import arraysort.project.board.app.comment.domain.CommentListResDTO;
import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.comment.mapper.CommentMapper;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.post.domain.PageDTO;
import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.post.domain.PageResDTO;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentMapper commentMapper;

	private final PostComponent postComponent;

	// 댓글 추가
	@Transactional
	public void addComment(CommentAddReqDTO dto, long boardId, long postId) {
		// 추가 전 검증
		validateAdd(boardId, postId);

		CommentVO vo = CommentVO.insertOf(dto, postId);
		commentMapper.insertComment(vo);
	}

	// 댓글 리스트 조회(페이징)
	@Transactional(readOnly = true)
	public PageResDTO<CommentListResDTO> findCommentListWithPaging(PageReqDTO dto, long boardId, long postId) {
		// 게시판 검증(존재, 상태 검증)
		postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);

		int totalCommentCount = commentMapper.selectTotalCommentCount(dto, postId);
		PageDTO pageDTO = new PageDTO(totalCommentCount, dto, boardId, postId);

		List<CommentListResDTO> commentList = commentMapper.selectCommentListWithPaging(pageDTO)
				.stream()
				.map(CommentListResDTO::of)
				.toList();

		return new PageResDTO<>(totalCommentCount, dto.getCommentPage(), commentList);
	}


	/**
	 * 댓글 추가 전 검증
	 * 1. 게시판 존재, 상태 검증
	 * 2. 게시판 댓글 허용 여부 검증
	 * 3. 로그인하지 않은 사용자에 대한 검증
	 * (댓글은 UserAccessLevel = 1 부터 가능, 비로그인은 0)
	 *
	 * @param boardId 현재 댓글을 작성하려는 게시판 ID
	 * @param postId  현재 댓글을 작성하려는 게시글 ID
	 */
	private void validateAdd(long boardId, long postId) {
		// 1. 게시판 검증(존재, 상태 검증)
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);

		// 2. 게시판 댓글 허용 여부 검증
		if (boardDetail.getCommentFlag() == Flag.N) {
			throw new InvalidPrincipalException("현재 게시판은 댓글을 허용하지 않습니다. ");
		}

		// 3. 로그인하지 않은 사용자에 대한 검증
		if (!UserUtil.isAuthenticatedUser()) {
			throw new InvalidPrincipalException("로그인이 필요합니다.");
		}

		// 4. 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);
	}
}
