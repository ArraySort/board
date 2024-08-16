package arraysort.project.board.app.exception;

import org.springframework.security.core.AuthenticationException;

public class NotActivatedUserException extends AuthenticationException {
	public NotActivatedUserException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
