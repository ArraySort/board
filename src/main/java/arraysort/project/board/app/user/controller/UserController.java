package arraysort.project.board.app.user.controller;

import arraysort.project.board.app.user.domain.UserSignupReqDTO;
import arraysort.project.board.app.user.service.UserService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String showLoginPage() {
        return "user/login";
    }

    // 회원가입 페이지로 이동
    @GetMapping("/signup")
    public String showSignupPage() {
        return "user/signup";
    }

    // 회원가입 요청
    @PostMapping("/process-signup")
    public String processSignup(@Valid @ModelAttribute UserSignupReqDTO dto, Model model) {
        userService.addUser(dto);

        ControllerUtil.addMessageAndRequest(model, "회원가입이 완료되었습니다.", "SIGNUP");
        return "common/alert";
    }
}
