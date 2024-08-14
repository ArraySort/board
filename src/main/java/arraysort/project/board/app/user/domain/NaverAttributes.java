package arraysort.project.board.app.user.domain;


import java.util.Map;

public class NaverAttributes extends OAuthAttributes {
	protected NaverAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, "NAVER");
	}

	@Override
	public String getName() {
		return String.valueOf(super.getAttributes().get("name"));
	}

	@Override
	public String getUserIdentify() {
		return String.valueOf(super.getAttributes().get("id"));
	}
}
