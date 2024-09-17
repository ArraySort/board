package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.UserNotFoundException;
import arraysort.project.board.app.user.domain.UserRankingResDTO;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static arraysort.project.board.app.common.Constants.*;

@Service
@RequiredArgsConstructor
public class UserPointService {

	private final UserMapper userMapper;

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

	// 일일 댓글 수 초기화
	@Transactional
	public void resetDailyCommentCount() {
		userMapper.resetAllDailyCommentCounts();
	}

	// 일일 획득 포인트 초기화
	@Transactional
	public void resetDailyPoint() {
		userMapper.resetAllDailyPoints();
	}

	// 관리자, 사용자 메인페이지 : 유저 전체 랭킹 조회
	@Transactional(readOnly = true)
	public List<UserRankingResDTO> findUserRanking() {
		return userMapper.selectUsersForRanking(10).stream()
				.map(UserRankingResDTO::of)
				.toList();
	}

	// 사용자 메인페이지 : 오늘의 유저 랭킹 조회
	@Transactional(readOnly = true)
	public List<UserRankingResDTO> findUserDailyRanking() {
		return userMapper.selectUsersForDailyRanking(5).stream()
				.map(UserRankingResDTO::of)
				.toList();
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
