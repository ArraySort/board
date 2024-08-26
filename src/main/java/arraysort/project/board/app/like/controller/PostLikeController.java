package arraysort.project.board.app.like.controller;

import arraysort.project.board.app.like.domain.PostLikeDislikeResDTO;
import arraysort.project.board.app.like.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{boardId}/post/detail/{postId}")
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	// 좋아요 요청
	@PostMapping("/like")
	public ResponseEntity<PostLikeDislikeResDTO> processAddPostLike(@PathVariable long boardId, @PathVariable long postId) {
		return ResponseEntity.ok(postLikeService.handlePostLikeDislike(boardId, postId, true));
	}

	// 싫어요 요청
	@PostMapping("/dislike")
	public ResponseEntity<PostLikeDislikeResDTO> processAddDisLike(@PathVariable long boardId, @PathVariable long postId) {
		return ResponseEntity.ok(postLikeService.handlePostLikeDislike(boardId, postId, false));
	}
}
