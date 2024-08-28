package arraysort.project.board.app.home.controller;

import arraysort.project.board.app.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static arraysort.project.board.app.common.Constants.MAV_HOME;
import static arraysort.project.board.app.common.Constants.REDIRECT_HOME;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final BoardService boardService;

	// 기본 경로 홈페이지로 리다이렉트
	@GetMapping("/")
	public String index() {
		return REDIRECT_HOME;
	}

	// 홈 페이지로 이동
	@GetMapping("/home")
	public String showHomePage(Model model) {
		model.addAttribute("message", "게시판 홈페이지");
		model.addAttribute("boards", boardService.findAllBoards());
		return MAV_HOME;
	}
}
