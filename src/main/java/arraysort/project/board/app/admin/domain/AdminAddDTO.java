package arraysort.project.board.app.admin.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminAddDTO {

	@NotBlank(message = "아이디는 필수로 입력되어야 합니다.")
	@Size(min = 4, max = 20, message = "아이디는 최소 4글자 최대 20글자여야 합니다.")
	private String adminId;

	@NotBlank(message = "비밀번호는 필수로 입력되어야 합니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 최소 4글자 최대 20글자여야 합니다.")
	private String adminPassword;

	// 관리자 비밀번호 암호화
	public void encodePassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
}
