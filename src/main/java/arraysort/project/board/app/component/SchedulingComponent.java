package arraysort.project.board.app.component;

import arraysort.project.board.app.user.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingComponent {

	private final UserPointService userPointService;

	/**
	 * 매일 자정에 실행되는 스케줄러
	 * 사용자 일일 댓글 개수, 일일 획득 포인트, redis 캐싱 랭킹 데이터 초기화
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void resetDailyCount() {
		userPointService.resetDailyInfo();
	}
}
