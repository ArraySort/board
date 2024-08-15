package arraysort.project.board.app.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {
	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
		HttpServletRequest request = event.getRequest();
		HttpServletResponse response = event.getResponse();
		HttpSession session = request.getSession();

		session.setAttribute("message", "다른 기기에서 로그인 되었습니다. 현재 기기에서 로그아웃 됩니다.");
		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}
}
