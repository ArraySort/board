package arraysort.project.board.app.admin.controller;

import arraysort.project.board.app.admin.domain.AdminAddReqDTO;
import arraysort.project.board.app.admin.service.AdminService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminLoginController {

	private final AdminService adminService;

	// 관리자 메인 페이지
	@GetMapping
	public String showMainPage() {
		return MAV_ADMIN;
	}

	// 관리자 로그인 페이지
	@GetMapping("/login")
	public String showLoginPage() {
		return MAV_ADMIN_LOGIN;
	}

	// 관리자 추가 요청
	@PostMapping("/process-add-admin")
	public String processAddAdmin(@Valid @ModelAttribute AdminAddReqDTO dto, Model model) {
		adminService.addAdmin(dto);

		ControllerUtil.addMessageAndRequest(model, "관리자 추가 완료", MAV_REQUEST_ADD_ADMIN);
		return MAV_ALERT;
	}

	// 유저 관리 페이지
	@GetMapping("/user")
	public String showUserManagementPage() {
		return MAV_ADMIN_USER_MANAGEMENT;
	}

	// 게시판 관리 페이지
	@GetMapping("/post")
	public String showPostManagementPage() {
		return MAV_ADMIN_POST_MANAGEMENT;
	}

	// 댓글 관리 페이지
	@GetMapping("/comment")
	public String showCommentManagementPage() {
		return MAV_ADMIN_COMMENT_MANAGEMENT;
	}

	// 신고 관리 페이지
	@GetMapping("/report")
	public String showReportManagementPage() {
		return MAV_ADMIN_REPORT_MANAGEMENT;
	}
}
