package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.category.service.CategoryService;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.post.domain.PostAddAdminReqDTO;
import arraysort.project.board.app.post.service.PostService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static arraysort.project.board.app.common.Constants.*;

@Controller
@RequestMapping("/admin/post/{boardId}")
@RequiredArgsConstructor
public class AdminPostController {

	private final PostService postService;

	private final BoardService boardService;

	private final CategoryService categoryService;

	// 게시글 관리 페이지 접속
	@GetMapping
	public String showPostManagementPage(@ModelAttribute PageReqDTO dto, @PathVariable long boardId, Model model) {
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("currentBoard", boardService.findBoardDetailById(boardId));
		model.addAttribute("postPagination", postService.findPostListWithPaging(dto, boardId));
		return MAV_ADMIN_POST_MANAGEMENT;
	}

	// 게시글 활성화 상태 변경
	@PostMapping("/{postId}/edit-activate-flag")
	public String processEditActivateFlagPost(@PathVariable long boardId, @PathVariable long postId, Model model) {
		postService.modifyActivateFlag(boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "게시글 활성화 상태가 변경되었습니다. ", MAV_REQUEST_EDIT_ACTIVATE_FLAG);
		return MAV_ALERT;
	}

	// 비활성화 게시글 관리 페이지 접속
	@GetMapping("/deactivated")
	public String showDeactivatedPostPage(@ModelAttribute PageReqDTO dto, @PathVariable long boardId, Model model) {
		// 비활성화 목록 조회를 위한 조회 값 설정
		dto.setActivateFlag(Flag.N);
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("currentBoard", boardService.findBoardDetailById(boardId));
		model.addAttribute("deactivatePostPagination", postService.findPostListWithPaging(dto, boardId));
		return MAV_ADMIN_POST_DEACTIVATED;
	}

	// 비활성화 게시글 관리 페이지 접속
	@GetMapping("/add")
	public String showAddPostPage(@PathVariable long boardId, Model model) {
		model.addAttribute("boardDetail", boardService.findBoardDetailById(boardId));
		model.addAttribute("categories", categoryService.findCategoryList(boardId));
		return MAV_ADMIN_POST_ADD;
	}

	// 게시글 추가 요청
	@PostMapping("/add-admin-post")
	public String processAddPost(@PathVariable long boardId, @Valid @ModelAttribute PostAddAdminReqDTO dto, Model model) {
		postService.addAdminPost(dto, boardId);

		ControllerUtil.addMessageAndRequest(model, "게시글이 추가되었습니다.", MAV_REQUEST_ADMIN_ADD_POST);
		return MAV_ALERT;
	}
}
