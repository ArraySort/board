package arraysort.project.board.app.config.security;

import arraysort.project.board.app.exception.NotActivatedUserException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.error("[폼 로그인 에러 : ]", exception);

		if (exception instanceof NotActivatedUserException || exception.getCause() instanceof NotActivatedUserException) {
			request.setAttribute("message", "관리자에 의해 비활성화 된 계정입니다.");
		} else {
			request.setAttribute("message", "아이디와 비밀번호를 다시 확인해주세요.");
		}

		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}
}
