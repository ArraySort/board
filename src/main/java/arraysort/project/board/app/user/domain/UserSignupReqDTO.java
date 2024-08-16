package arraysort.project.board.app.user.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupReqDTO {

	@NotBlank(message = "아이디는 필수로 입력되어야 합니다.")
	@Size(min = 4, max = 20, message = "아이디는 최소 4글자 최대 20글자여야 합니다.")
	private String userId;

	@NotBlank(message = "비밀번호는 필수로 입력되어야 합니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 최소 4글자 최대 20글자여야 합니다.")
	private String userPassword;

	@NotBlank(message = "비밀번호 확인은 필수로 입력되어야 합니다.")
	private String userPasswordCheck;

	@NotBlank(message = "이름은 필수로 입력되어야 합니다.")
	@Size(min = 2, max = 10, message = "이름은 최소 2글자 최대 10글자여야 합니다")
	private String userName;

	private String zipcode;

	private String address;

	private String addressDetail;

	public void encodePassword(String userPassword) {
		this.userPassword = userPassword;
	}
}
