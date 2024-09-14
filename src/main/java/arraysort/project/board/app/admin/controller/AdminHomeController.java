package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.comment.service.CommentService;
import arraysort.project.board.app.like.service.CommentLikeService;
import arraysort.project.board.app.like.service.PostLikeService;
import arraysort.project.board.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static arraysort.project.board.app.common.Constants.MAV_ADMIN;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHomeController {

	private final PostService postService;

	private final CommentService commentService;

	private final PostLikeService postLikeService;

	private final CommentLikeService commentLikeService;

	// 관리자 메인 페이지
	@GetMapping
	public String showMainPage(Model model) {
		model.addAttribute("allViews", postService.findAllViews());
		model.addAttribute("allPostsCount", postService.findAllPostsCount());
		model.addAttribute("allCommentsCount", commentService.findAllCommentsCount());
		model.addAttribute("allLikesCount",
				postLikeService.findAllLikesCount() + commentLikeService.findAllLikesCount());
		model.addAttribute("recentPosts", postService.findRecentPosts(10));
		return MAV_ADMIN;
	}
}
