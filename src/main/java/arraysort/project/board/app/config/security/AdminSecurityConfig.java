package arraysort.project.board.app.config.security;

import arraysort.project.board.app.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
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

	private final AdminLoginFailureHandler adminLoginFailureHandler;

	private final AdminService adminService;

	private final PasswordEncoder passwordEncoder;

	private final SessionRegistry redisSessionRegistry;

	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	private final CustomAccessDeniedHandler customAccessDeniedHandler;

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
						.failureHandler(adminLoginFailureHandler)
						.permitAll()
				)

				.logout(logout -> logout
						.logoutUrl("/admin/process-logout")
						.logoutSuccessUrl("/admin/login")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll()
				)

				// 중복 로그인 방지
				.sessionManagement(session -> session
						.maximumSessions(1)
						.sessionRegistry(redisSessionRegistry)
						.expiredSessionStrategy(customSessionExpiredStrategy)
						.maxSessionsPreventsLogin(false)  // 중복 로그인 방지
				)

				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler)
				)

				.authenticationProvider(adminAuthenticationProvider());

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
