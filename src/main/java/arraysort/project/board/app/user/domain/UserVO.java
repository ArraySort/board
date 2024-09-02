package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.common.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

	private String userId;  // 유저 ID

	private String userPassword;    // 유저 비밀번호

	private String userName;    // 유저 이름

	private String zipcode;    // 유저 우편번호

	private String address;    // 유저 기본 주소

	private String addressDetail;    // 유저 상세 주소

	private String provider;    // Oath 제공자

	private Integer accessLevel;    // 유저 접근 등급

	private int point;    // 유저 포인트

	private Date accessTime;    // 최근 접속 시간

	private Integer loginTryCount;    // 로그인 시도 횟수

	private Date loginLock;    // 로그인 잠금 시간

	private Flag activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String createdAt;    // 최초 생성 시간

	private String updatedAt;    // 최종 수정 시간

	private Flag deleteFlag;  // 삭제 여부

	private int dailyCommentCount;    // 일일 댓글 개수

	// 로그인
	public static UserVO of(UserSignupReqDTO dto) {
		return UserVO.builder()
				.userId(dto.getUserId())
				.userPassword(dto.getUserPassword())
				.userName(dto.getUserName())
				.zipcode(dto.getZipcode())
				.address(dto.getAddress())
				.addressDetail(dto.getAddressDetail())
				.accessLevel(2)
				.createdBy(dto.getUserId())
				.build();
	}

	// 로그인 시도 횟수 증가
	public void incrementLoginTryCount() {
		this.loginTryCount = loginTryCount + 1;
	}

	// 로그인 잠금 활성화
	public void activateLoginLock() {
		this.loginLock = Timestamp.from(Instant.now().plusSeconds(Constants.LOGIN_LOCK_SEC));
	}

	// 로그인 상태 초기화
	public void resetLoginStatus() {
		this.loginLock = null;
		this.loginTryCount = 0;
	}

	// 레벨 2 보다 낮은지 여부
	public boolean isBelowAccessLevel2() {
		return this.accessLevel < 2;
	}

	// 일일 댓글 개수에 도달했는지 여부
	public boolean isDailyCommentLimitNotReached() {
		return this.dailyCommentCount < 3;
	}
}
