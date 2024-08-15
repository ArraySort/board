package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.Constants;

import java.util.Map;

public class KakaoAttributes extends OAuthAttributes {
	public KakaoAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, Constants.REGISTRATION_ID_KAKAO);
	}

	@Override
	public String getUserIdentify() {
		return String.valueOf(String.valueOf(super.getAttributes().get(getNameAttributeKey())));
	}

	@Override
	public String getUserName() {
		return String.valueOf(((Map) super.getAttributes().get("properties")).get("nickname"));
	}
}
