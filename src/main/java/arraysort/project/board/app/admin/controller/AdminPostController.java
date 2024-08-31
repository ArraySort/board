package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static arraysort.project.board.app.common.Constants.MAV_ADMIN_POST_MANAGEMENT;

@Controller
@RequestMapping("/admin/post")
@RequiredArgsConstructor
public class AdminPostController {

	private final PostService postService;

	private final BoardService boardService;

	// 게시글 관리 페이지 접속
	@GetMapping("/{boardId}")
	public String showPostManagementPage(@ModelAttribute("page") PageReqDTO dto, @PathVariable long boardId, Model model) {
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("currentBoard", boardService.findBoardDetailById(boardId));
		model.addAttribute("postPagination", postService.findPostListWithPaging(dto, boardId));
		return MAV_ADMIN_POST_MANAGEMENT;
	}
}
