package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.board.domain.BoardAddReqDTO;
import arraysort.project.board.app.board.domain.BoardEditReqDTO;
import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.category.service.CategoryService;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static arraysort.project.board.app.common.Constants.*;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

	private final BoardService boardService;

	private final CategoryService categoryService;

	// 게시판 관리 페이지(게시판 목록)
	@GetMapping
	public String showBoardManagementPage(@ModelAttribute("page") PageReqDTO dto, Model model) {
		model.addAttribute("boardPagination", boardService.findBoardListWithPaging(dto));
		return MAV_ADMIN_BOARD_MANAGEMENT;
	}

	// 게시판 추가 페이지
	@GetMapping("/add")
	public String showAddBoardPage(Model model) {
		model.addAttribute("boardList", boardService.findAllBoards());
		return MAV_ADMIN_BOARD_ADD;
	}

	// 게시판 추가 요청
	@PostMapping("/process-add-board")
	public String processAddBoard(@Valid @ModelAttribute BoardAddReqDTO dto, Model model) {
		boardService.addBoard(dto);

		ControllerUtil.addMessageAndRequest(model, "게시판 추가가 완료되었습니다.", MAV_REQUEST_ADD_BOARD);
		return MAV_ALERT;
	}

	// 게시판 수정 페이지
	@GetMapping("/{boardId}/edit")
	public String showEditBoardPage(@PathVariable long boardId, Model model) {
		model.addAttribute("boardList", boardService.findAllBoards());
		model.addAttribute("boardDetail", boardService.findBoardDetailById(boardId));
		model.addAttribute("categoryList", categoryService.findCategoryList(boardId));
		return "admin/board/adminEditBoard";
	}

	// 게시판 수정 요청
	@PostMapping("/{boardId}/process-edit-board")
	public String processEditBoard(@PathVariable long boardId, @Valid @ModelAttribute BoardEditReqDTO dto, Model model) {
		boardService.modifyBoard(boardId, dto);

		ControllerUtil.addMessageAndRequest(model, "게시판 수정이 완료되었습니다.", MAV_REQUEST_MODIFY_BOARD);
		return MAV_ALERT;
	}

	// 게시판 삭제 요청
	@PostMapping("/{boardId}/process-delete-board")
	public String processDeleteBoard(@PathVariable long boardId, Model model) {
		boardService.removeBoard(boardId);

		ControllerUtil.addMessageAndRequest(model, "게시판 삭제 완료되었습니다.", MAV_REQUEST_DELETE_BOARD);
		return MAV_ALERT;
	}
}
