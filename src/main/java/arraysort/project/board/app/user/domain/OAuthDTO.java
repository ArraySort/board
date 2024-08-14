package arraysort.project.board.app.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OAuthDTO {

	private String userId;

	private String userName;

	private String oAuthProvider;

}
