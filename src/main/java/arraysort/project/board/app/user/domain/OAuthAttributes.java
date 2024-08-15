package arraysort.project.board.app.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuthAttributes {

	private Map<String, Object> attributes;        // 요청에 따라 지정된 사용자 정보 속성

	private String nameAttributeKey;        // 사용자 정보 속성의 Key 이름

	private String oAuthProvider;        // OAuth2 제공 플랫폼

	private String userIdentify;        // OAuth2 제공 사용자 ID -> User(userId)

	private String userName;        // OAuth2 제공 사용자 이름 -> User(userName)

	protected OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String oAuthProvider) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.oAuthProvider = oAuthProvider;
	}
}
