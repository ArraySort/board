package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.board.domain.BoardAddReqDTO;
import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static arraysort.project.board.app.common.Constants.*;

@Controller
@RequestMapping("/admin/board")
@RequiredArgsConstructor
public class AdminBoardController {

	private final BoardService boardService;

	// 게시판 관리 페이지(게시판 목록)
	@GetMapping
	public String showBoardManagementPage(@ModelAttribute("page") PageReqDTO dto, Model model) {
		model.addAttribute("boardPagination", boardService.findBoardListWithPaging(dto));
		return MAV_ADMIN_BOARD_MANAGEMENT;
	}

	// 게시판 추가 페이지
	@GetMapping("/add")
	public String showAddBoardPage() {
		return "admin/adminAddBoard";
	}

	// 게시판 추가 요청
	@PostMapping("/process-add-board")
	public String processAddBoard(@Valid @ModelAttribute BoardAddReqDTO dto, Model model) {
		boardService.addBoard(dto);

		ControllerUtil.addMessageAndRequest(model, "게시판 추가가 완료되었습니다.", MAV_REQUEST_ADD_BOARD);
		return MAV_ALERT;
	}
}
