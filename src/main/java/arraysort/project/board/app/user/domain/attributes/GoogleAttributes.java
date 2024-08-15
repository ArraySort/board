package arraysort.project.board.app.user.domain.attributes;

import arraysort.project.board.app.common.Constants;

import java.util.Map;

public class GoogleAttributes extends OAuthAttributes {
	public GoogleAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, Constants.OAUTH_PROVIDER_GOOGLE);
	}

	@Override
	public String getUserIdentify() {
		return String.valueOf(super.getAttributes().get(getNameAttributeKey()));
	}

	@Override
	public String getUserName() {
		return String.valueOf(super.getAttributes().get("name"));
	}
}
