package arraysort.project.board.app.config.security.component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class XSSFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Filter.super.init(filterConfig);
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

		XSSRequestWrapper wrappedRequest = new XSSRequestWrapper(httpServletRequest);

		filterChain.doFilter(wrappedRequest, servletResponse);
	}

	@Override
	public void destroy() {
		Filter.super.destroy();
	}
}
