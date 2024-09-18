package arraysort.project.board.app.config.security.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class XSSRequestWrapper extends HttpServletRequestWrapper {

	public XSSRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		return sanitize(value);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null)
			for (int i = 0; i < values.length; i++) {
				values[i] = sanitize(values[i]);
			}
		return values;
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		return sanitize(value);
	}

	private String sanitize(String value) {
		if (value != null) {
			value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
			value = value.replaceAll("'", "&#39;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("eval\\((.*)\\)", "");
			value = value.replaceAll("(?i)javascript", "");
			value = value.replaceAll("(?i)script", "");
		}
		return value;
	}
}
