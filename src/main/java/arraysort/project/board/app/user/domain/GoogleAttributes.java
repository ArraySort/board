package arraysort.project.board.app.user.domain;

import java.util.Map;

public class GoogleAttributes extends OAuthAttributes {

	public GoogleAttributes(Map<String, Object> attributes, String nameAttributeKey) {
		super(attributes, nameAttributeKey, "GOOGLE");
	}

	@Override
	public String getName() {
		return (String) super.getAttributes().get("name");
	}

	@Override
	public String getUserIdentify() {
		return (String) super.getAttributes().get(getNameAttributeKey());
	}
}
