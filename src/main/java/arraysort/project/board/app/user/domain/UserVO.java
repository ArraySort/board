package arraysort.project.board.app.user.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserVO {

    private String userId;

    private String userPassword;

    private String userName;

    public static UserVO of(UserSignupDto dto) {
        return UserVO.builder()
                .userId(dto.getUserId())
                .userPassword(dto.getUserPassword())
                .userName(dto.getUserName())
                .build();
    }
}
