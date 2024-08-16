package arraysort.project.board.app.config.security;

import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final UserMapper userMapper;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String userId = request.getParameter("userId");
		UserVO vo = userMapper.selectUserByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException(userId));

		// 로그인 시도 초기화
		resetLoginAttempts(vo);

		response.sendRedirect("/home");
	}

	/**
	 * 로그인 성공 시 로그인 시도 초기화
	 *
	 * @param vo 로그인에 성공한 사용자 정보
	 */
	private void resetLoginAttempts(UserVO vo) {
		if (vo.getLoginTryCount() > 0 || vo.getLoginLock() != null) {
			vo.updateLoginTryCount(0);
			vo.updateLoginLock(null);
			userMapper.updateLoginAttempts(vo);
		}
	}
}
