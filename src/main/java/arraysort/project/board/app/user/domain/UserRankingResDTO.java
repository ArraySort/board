package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRankingResDTO {

	private String userId;

	private String userName;

	private String provider;

	private Integer accessLevel;

	private int point;

	private Flag activateFlag;

	private Flag deleteFlag;

	private int dailyPoint;
	
	// 유저 랭킹 조회
	public static UserRankingResDTO of(UserVO vo) {
		return UserRankingResDTO.builder()
				.userId(vo.getUserId())
				.userName(vo.getUserName())
				.provider(vo.getProvider())
				.point(vo.getPoint())
				.activateFlag(vo.getActivateFlag())
				.deleteFlag(vo.getDeleteFlag())
				.dailyPoint(vo.getDailyPoint())
				.build();
	}
}
