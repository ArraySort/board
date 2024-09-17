package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.UserNotFoundException;
import arraysort.project.board.app.user.domain.UserRankingResDTO;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static arraysort.project.board.app.common.Constants.*;

@Service
@RequiredArgsConstructor
public class UserPointService {

	private final UserMapper userMapper;

	private final RedisTemplate<String, Object> redisTemplate;

	// 게시글 작성에 따른 사용자 포인트 지급, Level 2 해당
	@Transactional
	public void giveUserPointForPost() {
		// 게시글 작성 시 포인트 지급(20)
		userMapper.updateUserPointForPost(UserUtil.getCurrentLoginUserId(), POST_POINT);
	}

	// 댓글 작성에 따른 사용자 포인트 지급
	@Transactional
	public void giveUserPointForComment() {
		UserVO user = getValidatedUser();

		// 레벨 2 보다 낮은 사용자인 경우
		if (user.isBelowAccessLevel2()) {
			// 일일 댓글 제한에 도달하지 못했을 때(20)
			if (user.isDailyCommentLimitNotReached()) {
				userMapper.updateUserPointForComment(user.getUserId(), COMMENT_POINT_FOR_LEVEL1);
				checkAndUpgradeUserLevel(user, COMMENT_POINT_FOR_LEVEL1);
			}
		} else {
			// 레벨 2인 유저는 제한 없이 포인트 지급(10)
			userMapper.updateUserPointForComment(user.getUserId(), COMMENT_POINT);
		}
	}

	// 일일 최초 로그인 시 사용자 포인트 지급
	@Transactional
	public void giveUserPointForAttendance(String userId) {
		UserVO user = getValidatedUser();

		// 일일 최초 로그인 일 때
		if (user.isNotAccessedToday()) {
			userMapper.updateUserPointForAttendance(userId, ATTENDANCE_POINT);
			checkAndUpgradeUserLevel(user, ATTENDANCE_POINT);
		}
	}

	// 일일 정보 초기화 : 일일 댓글 개수, 일일 포인트, 랭킹 캐싱 데이터
	@Transactional
	public void resetDailyInfo() {
		userMapper.resetAllDailyCommentCounts();
		userMapper.resetAllDailyPoints();
		redisTemplate.delete("user:ranking");
		redisTemplate.delete("user:daily_ranking");
	}

	// 관리자, 사용자 메인페이지 : 유저 전체 랭킹 조회
	@Transactional(readOnly = true)
	public List<UserRankingResDTO> findUserRanking() {
		return getCachedRanking("user:ranking",
				() -> userMapper.selectUsersForRanking(10).stream()
						.map(UserRankingResDTO::of)
						.toList(), 1, TimeUnit.DAYS);
	}

	// 사용자 메인페이지 : 오늘의 유저 랭킹 조회
	@Transactional(readOnly = true)
	public List<UserRankingResDTO> findUserDailyRanking() {
		return getCachedRanking("user:daily_ranking",
				() -> userMapper.selectUsersForDailyRanking(5).stream()
						.map(UserRankingResDTO::of)
						.toList(), 10, TimeUnit.MINUTES);
	}

	/**
	 * Redis 유저 랭킹 조회 캐싱 공통 메서드
	 *
	 * @param key             캐싱되는 key 값
	 * @param dbQuerySupplier 랭킹 조회 쿼리
	 * @param timeout         만료시간
	 * @param timeUnit        만료 시간 단위
	 * @return 유저 랭킹 리스트
	 */
	private List<UserRankingResDTO> getCachedRanking(String key, Supplier<List<UserRankingResDTO>> dbQuerySupplier, long timeout, TimeUnit timeUnit) {
		// 캐시에서 데이터 조회
		List<UserRankingResDTO> cachedRanking = (List<UserRankingResDTO>) redisTemplate.opsForValue().get(key);

		if (cachedRanking == null || cachedRanking.isEmpty()) {
			// 캐시에 데이터가 없으면 DB 에서 조회
			cachedRanking = dbQuerySupplier.get();
			redisTemplate.opsForValue().set(key, cachedRanking, timeout, timeUnit);
		}

		return cachedRanking;
	}

	/**
	 * 유저 등업
	 * 레벨 1 사용자에 대한 유저 등업 조건 확인
	 * 조건 만족 시 레벨 2 로 등업
	 *
	 * @param user 유저 정보
	 */
	private void checkAndUpgradeUserLevel(UserVO user, int currentAddPoint) {
		if (user.canUpgradeLevel(currentAddPoint)) {
			userMapper.updateUserLevelUp(user.getUserId());
		}
	}

	/**
	 * 유저 조회
	 *
	 * @return 검증된 유저 반환
	 */
	private UserVO getValidatedUser() {
		return userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
				.orElseThrow(UserNotFoundException::new);
	}
}
