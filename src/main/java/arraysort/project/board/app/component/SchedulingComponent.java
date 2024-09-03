package arraysort.project.board.app.component;

import arraysort.project.board.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingComponent {

	private final UserMapper userMapper;

	/**
	 * 매일 자정에 실행되는 스케줄러
	 * 사용자 일일 댓글 개수를 초기화
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void resetDailyCommentCount() {
		userMapper.resetAllDailyCommentCounts();
	}
}
