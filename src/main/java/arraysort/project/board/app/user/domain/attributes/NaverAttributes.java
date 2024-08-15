package arraysort.project.board.app.user.domain.attributes;


import arraysort.project.board.app.common.Constants;

import java.util.Map;

public class NaverAttributes extends OAuthAttributes {
	public NaverAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, Constants.OAUTH_PROVIDER_NAVER);
	}

	@Override
	public String getUserIdentify() {
		return String.valueOf(super.getAttributes().get("id"));
	}

	@Override
	public String getUserName() {
		return String.valueOf(super.getAttributes().get("name"));
	}
}
