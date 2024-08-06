package arraysort.project.board.app.utils;

import arraysort.project.board.app.exception.InvalidPrincipalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

	private UserUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 로그인 한 유저의 Username(userId) 를 가져옴
	 *
	 * @return 로그인 한 유저의 userId
	 */
	public static String getCurrentLoginUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!isValidAuthentication(authentication)) {
			throw new InvalidPrincipalException("올바르지 않은 유저입니다.");
		}
		return authentication.getName();
	}

	/**
	 * 로그인(인가) 된 사용자의 접근인지 아닌지 검증
	 *
	 * @return 인가 여부
	 */
	public static boolean isAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return isValidAuthentication(authentication) && !authentication.getPrincipal().equals("anonymousUser");
	}

	/**
	 * authentication null 체크 및 검증
	 */
	private static boolean isValidAuthentication(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated();
	}
}
