package arraysort.project.board.app.user.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupDto {

    @NotBlank(message = "아이디는 필수로 입력되어야 합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수로 입력되어야 합니다.")
    private String userPassword;

    @NotBlank(message = "이름은 필수로 입력되어야 합니다.")
    private String userName;

    public void encodePassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
