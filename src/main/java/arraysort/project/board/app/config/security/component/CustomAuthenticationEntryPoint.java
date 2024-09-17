package arraysort.project.board.app.config.security.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private static final Pattern ADMIN_PATH_PATTERN = Pattern.compile("^/admin.*");

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		String requestURI = request.getRequestURI();

		if (ADMIN_PATH_PATTERN.matcher(requestURI).matches()) {
			request.setAttribute("request", "ADMIN_LOGIN");
		} else {
			request.setAttribute("request", "LOGIN");
		}

		request.setAttribute("message", "로그인이 필요합니다.");
		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}
}