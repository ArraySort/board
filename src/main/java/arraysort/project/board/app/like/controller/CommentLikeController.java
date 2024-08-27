package arraysort.project.board.app.like.controller;

import arraysort.project.board.app.like.domain.CommentLikeDislikeResDTO;
import arraysort.project.board.app.like.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{boardId}/post/detail/{postId}/{commentId}")
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	// 좋아요 요청
	@PostMapping("/comment-like")
	public ResponseEntity<CommentLikeDislikeResDTO> processAddPostLike(@PathVariable long boardId, @PathVariable long postId, @PathVariable long commentId) {
		try {
			return ResponseEntity.ok(commentLikeService.handleCommentLikeDislike(boardId, postId, commentId, true));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// 싫어요 요청
	@PostMapping("/comment-dislike")
	public ResponseEntity<CommentLikeDislikeResDTO> processAddDisLike(@PathVariable long boardId, @PathVariable long postId, @PathVariable long commentId) {
		try {
			return ResponseEntity.ok(commentLikeService.handleCommentLikeDislike(boardId, postId, commentId, false));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
