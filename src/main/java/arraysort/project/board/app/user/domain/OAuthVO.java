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
public class OAuthVO {

	private String userId;        // 유저 아이디

	private String userName;    // 유저 이름

	private String oAuthProvider;    // OAuth 제공자

	private Integer accessLevel;    // 유저 접근 등급

	private Date accessTime;    // 최근 접속 시간

	private Flag activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String createdAt;    // 최초 생성 시간

	private String updatedAt;    // 최종 수정 시간

	private Flag deleteFlag;  // 삭제 여부

	// OAuth 회원가입
	public static OAuthVO of(OAuthDTO dto) {
		return OAuthVO.builder()
				.userId(dto.getUserId())
				.userName(dto.getUserName())
				.oAuthProvider(dto.getOAuthProvider())
				.accessLevel(2)
				.createdBy(dto.getUserId())
				.build();
	}
}

