package arraysort.project.board.app.utils;

import arraysort.project.board.app.exception.InvalidPrincipalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

import static arraysort.project.board.app.common.Constants.ANONYMOUS_USER;

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
	 * 현재 로그인 한 유저의 소유인지 검사
	 *
	 * @param userId 소유자의 유저 ID
	 * @return 로그인한 유저의 소유인지 여부 / 소유일 경우 true
	 */
	public static boolean isCurrentUserOwner(String userId) {
		return Objects.equals(userId, getCurrentLoginUserId());
	}

	/**
	 * 현재 로그인 한 유저의 소유인지 검사
	 *
	 * @param userId 소유자의 유저 ID
	 * @return 로그인한 유저의 소유인지 여부 / 소유가 아닐 경우 true
	 */
	public static boolean isNotCurrentUserOwner(String userId) {
		return !isCurrentUserOwner(userId);
	}

	/**
	 * 로그인(인증) 된 사용자의 접근인 경우
	 *
	 * @return 인증된 유저인 경우 true
	 */
	public static boolean isAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return isValidAuthentication(authentication) && !authentication.getPrincipal().equals(ANONYMOUS_USER);
	}

	/**
	 * 로그인(인증) 된 사용자의 접근이 아닌 경우
	 *
	 * @return 인증되지 않은 유저인 경우 true
	 */
	public static boolean isNotAuthenticatedUser() {
		return !isAuthenticatedUser();
	}

	/**
	 * authentication null 체크 및 검증
	 */
	private static boolean isValidAuthentication(Authentication authentication) {
		return authentication != null && authentication.isAuthenticated();
	}
}
