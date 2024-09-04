package arraysort.project.board.app.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Order(1)
@Configuration
@RequiredArgsConstructor
public class AdminSecurityConfig {

	private final String[] permittedAdminUrls = {
			"/admin/login", "/admin/process-add-admin", "/admin/process-login-admin",
			"/WEB-INF/views/**", "/resources/**"};

	private final AdminAuthenticationProvider adminAuthenticationProvider;

	private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

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

				.logout(logout -> logout
						.logoutUrl("/admin/process-logout")
						.logoutSuccessUrl("/admin/login")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID", "remember-me")
						.permitAll()
				)

				.authenticationManager(adminAuthenticationManager())

				.sessionManagement(session -> session
						.maximumSessions(1)
						.expiredSessionStrategy(customSessionExpiredStrategy)
						.maxSessionsPreventsLogin(true)  // 중복 로그인 방지
				);

		return http.build();
	}

	@Bean
	public AuthenticationManager adminAuthenticationManager() {
		ProviderManager providerManager = new ProviderManager(adminAuthenticationProvider);
		providerManager.setEraseCredentialsAfterAuthentication(false);

		return providerManager;
	}
}
