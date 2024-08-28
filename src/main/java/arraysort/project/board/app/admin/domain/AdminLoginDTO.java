package arraysort.project.board.app.admin.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminLoginDTO {

	@NotBlank(message = "아이디 입력은 필수입니다.")
	private String adminId;

	@NotBlank(message = "비밀번호 입력은 필수입니다.")
	private String adminPassword;
}
