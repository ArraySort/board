package arraysort.project.board.app.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginLockException extends AuthenticationException {
	public LoginLockException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
