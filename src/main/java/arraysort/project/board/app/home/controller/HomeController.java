package arraysort.project.board.app.home.controller;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.post.domain.PostListResDTO;
import arraysort.project.board.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static arraysort.project.board.app.common.Constants.MAV_HOME;
import static arraysort.project.board.app.common.Constants.REDIRECT_HOME;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final BoardService boardService;

	private final PostService postService;

	// 기본 경로 홈페이지로 리다이렉트
	@GetMapping("/")
	public String index() {
		return REDIRECT_HOME;
	}

	// 홈 페이지로 이동
	@GetMapping("/home")
	public String showHomePage(Model model) {
		model.addAttribute("boards", boardService.findAllBoards());

		Map<String, List<PostListResDTO>> topPostsMap = new HashMap<>();

		for (BoardVO board : boardService.findAllBoards()) {
			topPostsMap.put(board.getBoardName(),
					postService.findTopPostsByBoardId(board.getBoardId(), 5));
		}

		model.addAttribute("topPostsMap", topPostsMap);
		return MAV_HOME;
	}
}
