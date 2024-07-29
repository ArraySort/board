package arraysort.project.board.app.user.controller;

import arraysort.project.board.app.user.domain.UserSignupDto;
import arraysort.project.board.app.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입 요청
    @PostMapping("/process-signup")
    public String processSignup(@Valid @ModelAttribute UserSignupDto dto) {
        userService.addUser(dto);

        return "redirect:/home";
    }
}
