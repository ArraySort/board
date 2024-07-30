package arraysort.project.board.app.user.controller;

import arraysort.project.board.app.user.domain.UserSignupDTO;
import arraysort.project.board.app.user.service.UserService;
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
    public String processSignup(@Valid @ModelAttribute UserSignupDTO dto, Model model) {
        userService.addUser(dto);

        model.addAttribute("message", "회원가입이 완료되었습니다.");
        model.addAttribute("request", "SIGNUP");
        return "common/alert";
    }
}
