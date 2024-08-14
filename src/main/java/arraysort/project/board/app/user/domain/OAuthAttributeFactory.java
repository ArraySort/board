package arraysort.project.board.app.user.domain;

import java.util.Map;

public class OAuthAttributeFactory {

	private OAuthAttributeFactory() {
	}

	public static OAuthAttributes getAttributeByProvider(Map<String, Object> attributes, String nameAttributeKey, String registrationId) {
		return switch (registrationId.toLowerCase()) {
			case "kakao" -> new KakaoAttributes(attributes, nameAttributeKey);
			case "google" -> new GoogleAttributes(attributes, nameAttributeKey);
			case "naver" -> new NaverAttributes((Map) attributes.get("response"), nameAttributeKey);
			default -> throw new IllegalStateException("Unexpected value: " + registrationId.toLowerCase());
		};
	}
}
