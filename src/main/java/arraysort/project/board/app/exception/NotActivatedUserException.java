package arraysort.project.board.app.exception;

import org.springframework.security.core.AuthenticationException;

public class NotActivatedUserException extends AuthenticationException {
	public NotActivatedUserException(String msg) {
		super(msg);
	}
}
