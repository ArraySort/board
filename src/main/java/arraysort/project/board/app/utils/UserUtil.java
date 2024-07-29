package arraysort.project.board.app.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    private UserUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 로그인(인가) 된 사용자의 접근인지 아닌지 검증
     *
     * @return 인가 여부
     */
    public static boolean isAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !authentication.getPrincipal().equals("anonymousUser");
    }
}
