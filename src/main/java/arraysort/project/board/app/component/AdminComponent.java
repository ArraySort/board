package arraysort.project.board.app.component;

import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.utils.AdminUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminComponent {

	/**
	 * 게시판 관리 기능 중 사용자가 관리자인지 검증
	 */
	public void validateAdmin() {
		if (!AdminUtil.isAdmin()) {
			throw new InvalidPrincipalException("게시판 추가는 관리자만 가능합니다.");
		}
	}
}
