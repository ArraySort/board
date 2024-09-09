package arraysort.project.board.app.config.security.component;

import arraysort.project.board.app.exception.LoginLockException;
import arraysort.project.board.app.exception.NotActivatedUserException;
import arraysort.project.board.app.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLoginFailureHandler implements AuthenticationFailureHandler {

	private final UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.error("[폼 로그인 에러 : ]", exception);

		if (exception.getCause() instanceof NotActivatedUserException) {
			request.setAttribute("message", exception.getMessage());
		} else if (exception instanceof BadCredentialsException) {
			userService.handleFailedLoginAttempts(request.getParameter("userId"));
			request.setAttribute("message", "아이디와 비밀번호를 다시 확인해주세요.");
		} else if (exception.getCause() instanceof LoginLockException) {
			request.setAttribute("message", exception.getMessage());
		} else {
			request.setAttribute("message", "로그인 처리중 오류가 발생했습니다.");
		}

		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}
}
