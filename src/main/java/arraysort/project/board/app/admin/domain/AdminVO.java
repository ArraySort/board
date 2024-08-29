package arraysort.project.board.app.admin.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminVO {

	private String adminId;    // 관리자 ID

	private String adminPassword;    // 관리자 비밀번호

	// 관리자 추가
	public static AdminVO of(AdminAddReqDTO dto) {
		return AdminVO.builder()
				.adminId(dto.getAdminId())
				.adminPassword(dto.getAdminPassword())
				.build();
	}
}
