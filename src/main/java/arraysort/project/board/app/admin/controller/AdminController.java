package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.admin.domain.AdminAddDTO;
import arraysort.project.board.app.admin.domain.AdminLoginDTO;
import arraysort.project.board.app.admin.service.AdminService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	// 관리자 메인 페이지
	@GetMapping
	public String showMainPage() {
		return "admin/admin";
	}

	// 관리자 로그인 페이지
	@GetMapping("/login")
	public String showLoginPage() {
		return "admin/adminLogin";
	}

	// 관리자 로그인 요청
	@PostMapping("/process-login-admin")
	public String processLoginAdmin(@ModelAttribute AdminLoginDTO dto, Model model, HttpServletRequest request) {
		adminService.login(dto, request);

		ControllerUtil.addMessageAndRequest(model, "로그인 성공", "LOGIN_ADMIN");
		return "common/alert";
	}

	// 관리자 추가 요청
	@PostMapping("/process-add-admin")
	public String processAddAdmin(@ModelAttribute AdminAddDTO dto, Model model) {
		adminService.addAdmin(dto);

		ControllerUtil.addMessageAndRequest(model, "관리자 추가 완료", "ADD_ADMIN");
		return "common/alert";
	}

	// 유저 관리 페이지
	@GetMapping("/user")
	public String showUserManagementPage() {
		return "admin/adminUserManagement";
	}

	// 게시판 관리 페이지
	@GetMapping("/board")
	public String showBoardManagementPage() {
		return "admin/adminBoardManagement";
	}

	// 게시판 관리 페이지
	@GetMapping("/post")
	public String showPostManagementPage() {
		return "admin/adminPostManagement";
	}

	// 댓글 관리 페이지
	@GetMapping("/comment")
	public String showCommentManagementPage() {
		return "admin/adminCommentManagement";
	}

	// 신고 관리 페이지
	@GetMapping("/report")
	public String showReportManagementPage() {
		return "admin/adminReportManagement";
	}
}
