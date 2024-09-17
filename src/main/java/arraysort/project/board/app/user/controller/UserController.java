package arraysort.project.board.app.user.controller;

import arraysort.project.board.app.user.domain.UserSignupReqDTO;
import arraysort.project.board.app.user.service.UserService;
import arraysort.project.board.app.utils.ControllerUtil;
import arraysort.project.board.app.utils.UserUtil;
import jakarta.servlet.http.HttpSession;
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
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	// 로그인 페이지로 이동
	@GetMapping("/login")
	public String showLoginPage() {
		if (UserUtil.isAuthenticatedUser()) {
			return REDIRECT_HOME;
		}
		return MAV_USER_LOGIN;
	}

	// 회원가입 페이지로 이동
	@GetMapping("/signup")
	public String showSignupPage() {
		if (UserUtil.isAuthenticatedUser()) {
			return REDIRECT_HOME;
		}
		return MAV_USER_SIGNUP;
	}

	// 회원가입 요청
	@PostMapping("/process-signup")
	public String processSignup(@Valid @ModelAttribute UserSignupReqDTO dto, HttpSession session, Model model) {
		userService.addUser(dto, session);

		ControllerUtil.addMessageAndRequest(model, "회원가입이 완료되었습니다.", MAV_REQUEST_SIGNUP);
		return MAV_ALERT;
	}
}
