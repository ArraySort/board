package arraysort.project.board.app.utils;

import arraysort.project.board.app.exception.InvalidPrincipalException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal == null) throw new InvalidPrincipalException();

        return ((UserDetails) principal).getUsername();
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
