package arraysort.project.board.app.user.domain;

import arraysort.project.board.app.common.Constants;

import java.util.Map;

public class OAuthAttributeFactory {

	private OAuthAttributeFactory() {
	}

	public static OAuthAttributes getAttributeByProvider(Map<String, Object> attributes, String registrationId, String nameAttributeKey) {

		return switch (registrationId) {
			case Constants.REGISTRATION_ID_GOOGLE -> new GoogleAttributes(attributes, nameAttributeKey);
			case Constants.REGISTRATION_ID_KAKAO -> new KakaoAttributes(attributes, nameAttributeKey);
			case Constants.REGISTRATION_ID_NAVER ->
					new NaverAttributes((Map) attributes.get("response"), nameAttributeKey);
			default -> throw new IllegalStateException("Unexpected value: " + registrationId.toLowerCase());
		};
	}
}
