package arraysort.project.board.app.config.security.config;

import arraysort.project.board.app.config.security.component.XSSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	@Bean
	public FilterRegistrationBean<XSSFilter> xssFilterFilterRegistration() {
		FilterRegistrationBean<XSSFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new XSSFilter());
		registrationBean.addUrlPatterns("/*");
		registrationBean.setName("XSS Filter");
		return registrationBean;
	}
}
