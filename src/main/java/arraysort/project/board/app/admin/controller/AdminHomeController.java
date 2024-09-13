package arraysort.project.board.app.admin.controller;

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

	// 관리자 메인 페이지
	@GetMapping
	public String showMainPage(Model model) {
		model.addAttribute("allViews", postService.findAllViews());
		return MAV_ADMIN;
	}
}
