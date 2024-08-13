package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class UserVO {

	private String userId;  // 유저 ID

	private String userPassword;    // 유저 비밀번호

	private String userName;    // 유저 이름

	private Integer accessLevel;    // 유저 접근 등급

	private Date accessTime;    // 최근 접속 시간

	private Flag activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String createdAt;    // 최초 생성 시간

	private String updatedAt;    // 최종 수정 시간

	private Flag deleteFlag;  // 삭제 여부

	public static UserVO of(UserSignupReqDTO dto) {
		return UserVO.builder()
				.userId(dto.getUserId())
				.userPassword(dto.getUserPassword())
				.userName(dto.getUserName())
				.accessLevel(2) // TODO : access_level 수정
				.createdBy(dto.getUserId())
				.build();
	}
}
