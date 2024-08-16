package arraysort.project.board.app.config.security;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.exception.LoginLockException;
import arraysort.project.board.app.exception.NotActivatedUserException;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

	private final UserMapper userMapper;

	@Transactional
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		log.error("[폼 로그인 에러 : ]", exception);

		if (exception instanceof NotActivatedUserException || exception.getCause() instanceof NotActivatedUserException) {
			request.setAttribute("message", exception.getMessage());
		} else if (exception instanceof BadCredentialsException) {
			String userId = request.getParameter("userId");
			userMapper.selectUserByUserId(userId).ifPresent(this::handleFailedLoginAttempts);

			request.setAttribute("message", "아이디와 비밀번호를 다시 확인해주세요.");
		} else if (exception.getCause() instanceof LoginLockException) {
			request.setAttribute("message", exception.getMessage());
		} else {
			request.setAttribute("message", "로그인 처리중 오류가 발생했습니다.");
		}

		request.getRequestDispatcher("/WEB-INF/views/common/alert.jsp").forward(request, response);
	}

	/**
	 * 비밀번호가 일치하지 않을 때 로그인 시도 횟수 증가
	 * 지정된 횟수를 초과하면 지정된 시간만큼 LoginLock 시간 설정
	 *
	 * @param vo 로그인을 시도하는 회원 정보
	 */
	private void handleFailedLoginAttempts(UserVO vo) {
		vo.updateLoginTryCount(vo.getLoginTryCount() + 1);

		if (vo.getLoginTryCount() >= Constants.MAX_ATTEMPTS_COUNT && vo.getLoginTryCount() % Constants.MAX_ATTEMPTS_COUNT == 0) {
			vo.updateLoginLock(Timestamp.from(Instant.now().plusSeconds(Constants.LOGIN_LOCK_SEC)));
		}

		userMapper.updateLoginAttempts(vo);
	}
}
