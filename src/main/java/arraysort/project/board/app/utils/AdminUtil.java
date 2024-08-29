package arraysort.project.board.app.utils;

import arraysort.project.board.app.common.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AdminUtil {
	private AdminUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 현재 로그인 한 사용자가 관리자인지 검증
	 * 로그인 한 사용자의 ROLE 비교
	 *
	 * @return 관리자 여부
	 */
	public static boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(grantedAuthority -> Constants.ROLE_ADMIN.equals(grantedAuthority.getAuthority()));
	}
}
