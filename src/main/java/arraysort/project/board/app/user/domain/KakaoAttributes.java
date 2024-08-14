package arraysort.project.board.app.user.domain;

import java.util.Map;

public class KakaoAttributes extends OAuthAttributes {
	protected KakaoAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, "KAKAO");
	}

	@Override
	public String getUserIdentify() {
		return String.valueOf(((Map) super.getAttributes().get("properties")).get("nickname"));
	}

	@Override
	public String getName() {
		return String.valueOf(super.getAttributes().get(getNameAttributeKey()));
	}
}
