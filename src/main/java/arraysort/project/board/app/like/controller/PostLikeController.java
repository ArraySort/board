package arraysort.project.board.app.like.controller;

import arraysort.project.board.app.like.domain.PostLikeDislikeResDTO;
import arraysort.project.board.app.like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/{boardId}/post/detail/{postId}")
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	// 좋아요 요청
	@PostMapping("/like")
	public ResponseEntity<PostLikeDislikeResDTO> processAddPostLike(@PathVariable long boardId, @PathVariable long postId) {
		try {
			return ResponseEntity.ok(postLikeService.handlePostLikeDislike(boardId, postId, true));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// 싫어요 요청
	@PostMapping("/dislike")
	public ResponseEntity<PostLikeDislikeResDTO> processAddDisLike(@PathVariable long boardId, @PathVariable long postId) {
		try {
			return ResponseEntity.ok(postLikeService.handlePostLikeDislike(boardId, postId, false));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}
}
