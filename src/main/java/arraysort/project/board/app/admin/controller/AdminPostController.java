package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.post.service.PostService;
import arraysort.project.board.app.utils.ControllerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static arraysort.project.board.app.common.Constants.*;

@Controller
@RequestMapping("/admin/post")
@RequiredArgsConstructor
public class AdminPostController {

	private final PostService postService;

	private final BoardService boardService;

	// 게시글 관리 페이지 접속
	@GetMapping("/{boardId}")
	public String showPostManagementPage(@ModelAttribute PageReqDTO dto, @PathVariable long boardId, Model model) {
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("currentBoard", boardService.findBoardDetailById(boardId));
		model.addAttribute("postPagination", postService.findPostListWithPaging(dto, boardId));
		return MAV_ADMIN_POST_MANAGEMENT;
	}

	// 게시글 활성화 상태 변경
	@PostMapping("/{boardId}/{postId}/process-edit-activate-flag-post")
	public String processEditActivateFlagPost(@PathVariable long boardId, @PathVariable long postId, Model model) {
		postService.modifyActivateFlag(boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "게시글 활성화 상태가 변경되었습니다. ", MAV_REQUEST_EDIT_ACTIVATE_FLAG);
		return MAV_ALERT;
	}

	// 비활성화 게시글 관리 페이지 접속
	@GetMapping("/{boardId}/deactivated")
	public String showDeactivatedPostPage(@ModelAttribute PageReqDTO dto, @PathVariable long boardId, Model model) {
		// 비활성화 목록 조회를 위한 조회 값 설정
		dto.setActivateFlag(Flag.N);
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("currentBoard", boardService.findBoardDetailById(boardId));
		model.addAttribute("deactivatePostPagination", postService.findPostListWithPaging(dto, boardId));
		return MAV_ADMIN_DEACTIVATED_POST;
	}
}
