package arraysort.project.board.app.config.security.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AdminLoginFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.error("[관리자 로그인 에러 : ]", exception);

		if (exception instanceof BadCredentialsException) {
			request.setAttribute("message", "아이디와 비밀번호를 올바르게 입력하세요");
		} else {
			request.setAttribute("message", "로그인 처리중 오류가 발생했습니다.");
		}

		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}
}
