package arraysort.project.board.app.config.security;

import arraysort.project.board.app.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		// 로그인 성공 시 처리(로그인 잠금, 시도횟수 초기화, 일일 최초 로그인 포인트 지급, 사용자 최근 접속 시간 업데이트)
		userService.handleSuccessLoginAttempts(authentication.getName());
		response.sendRedirect("/home");
	}
}
