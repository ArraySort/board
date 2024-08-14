package arraysort.project.board.app.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuthAttributes {

	private Map<String, Object> attributes;

	private String nameAttributeKey;

	private String oAuthProvider;

	private String name;

	private String userIdentify;

	protected OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String oAuthProvider) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.oAuthProvider = oAuthProvider;
	}
}
