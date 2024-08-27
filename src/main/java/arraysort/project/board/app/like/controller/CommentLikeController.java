package arraysort.project.board.app.like.controller;

import arraysort.project.board.app.like.domain.CommentLikeDislikeResDTO;
import arraysort.project.board.app.like.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{boardId}/post/detail/{postId}/{commentId}")
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	// 좋아요 요청
	@PostMapping("/comment-like")
	public ResponseEntity<CommentLikeDislikeResDTO> processAddPostLike(@PathVariable long boardId, @PathVariable long postId, @PathVariable long commentId) {
		return ResponseEntity.ok(commentLikeService.handleCommentLikeDislike(boardId, postId, commentId, true));
	}

	// 싫어요 요청
	@PostMapping("/comment-dislike")
	public ResponseEntity<CommentLikeDislikeResDTO> processAddDisLike(@PathVariable long boardId, @PathVariable long postId, @PathVariable long commentId) {
		return ResponseEntity.ok(commentLikeService.handleCommentLikeDislike(boardId, postId, commentId, false));
	}
}
