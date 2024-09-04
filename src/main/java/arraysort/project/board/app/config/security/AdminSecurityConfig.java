package arraysort.project.board.app.config.security;

import arraysort.project.board.app.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class AdminSecurityConfig {

	private final String[] permittedAdminUrls = {
			"/admin/login", "/admin/process-add-admin", "/admin/process-login-admin",
			"/WEB-INF/views/**", "/resources/**"};

	private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

	private final AdminLoginSuccessHandler adminLoginSuccessHandler;

	private final AdminLoginFailureHandler loginFailureHandler;

	private final AdminService adminService;

	private final PasswordEncoder passwordEncoder;

	@Bean
	public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(AntPathRequestMatcher.antMatcher("/admin/**"))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(permittedAdminUrls).permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest()
						.authenticated()
				)

				.formLogin(form -> form
						.loginPage("/admin/login")
						.loginProcessingUrl("/admin/process-login-admin")
						.usernameParameter("adminId")
						.passwordParameter("adminPassword")
						.successHandler(adminLoginSuccessHandler)
						.failureHandler(loginFailureHandler)
						.permitAll()
				)

				.logout(logout -> logout
						.logoutUrl("/admin/process-logout")
						.logoutSuccessUrl("/admin/login")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll()
				)

				.authenticationProvider(adminAuthenticationProvider())

				.sessionManagement(session -> session
						.maximumSessions(1)
						.expiredSessionStrategy(customSessionExpiredStrategy)
						.maxSessionsPreventsLogin(false)  // 중복 로그인 방지
				);

		return http.build();
	}

	// 관리자 로그인 Provider 설정
	@Bean
	public DaoAuthenticationProvider adminAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(adminService);
		provider.setPasswordEncoder(passwordEncoder);

		return provider;
	}
}
