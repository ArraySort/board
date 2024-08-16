package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	private Date accessTime;    // 최근 접속 시간

	private Integer loginTryCount;    // 로그인 시도 횟수

	private Date loginLock;    // 로그인 잠금 시간

	private Flag activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String createdAt;    // 최초 생성 시간

	private String updatedAt;    // 최종 수정 시간

	private Flag deleteFlag;  // 삭제 여부

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

	// 로그인 시도 횟수 업데이트
	public void updateLoginTryCount(int loginTryCount) {
		this.loginTryCount = loginTryCount;
	}

	// 로그인 잠금 업데이트
	public void updateLoginLock(Date loginLock) {
		this.loginLock = loginLock;
	}
}
