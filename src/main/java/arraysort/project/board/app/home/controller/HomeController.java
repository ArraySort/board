package arraysort.project.board.app.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 홈 페이지로 이동
    @GetMapping(value = {"/", "home"})
    public String showHomePage(Model model) {
        model.addAttribute("message", "게시판 홈페이지");
        return "home";
    }

    // 로그인 페이지로 이동
    @GetMapping("/loginPage")
    public String showLoginPage() {
        return "user/login";
    }

    // 회원가입 페이지로 이동
    @GetMapping("/signupPage")
    public String showSignupPage() {
        return "user/signup";
    }
}
