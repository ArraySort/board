package arraysort.project.board.app.config.security;

import arraysort.project.board.app.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final String[] permittedUrls = {
			"/", "/home",
			"/user/login", "/user/process-login", "/user/signup", "/user/process-signup",
			"/user/send-email-verification", "/user/verify-email-code",
			"/WEB-INF/views/**", "/resources/**",
			"/admin/login", "/admin/process-add-admin", "/admin/process-login-admin"};

	private final LoginSuccessHandler loginSuccessHandler;

	private final LoginFailureHandler loginFailureHandler;

	private final CustomOAuth2UserService customOAuth2UserService;

	private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(permittedUrls).permitAll()
						.requestMatchers("/{boardId}/post/**").permitAll()
						.requestMatchers("/image/{imageId}").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.anyRequest()
						.authenticated()
				)

				.formLogin(form -> form
						.loginPage("/user/login")
						.loginProcessingUrl("/user/process-login")
						.usernameParameter("userId")
						.passwordParameter("userPassword")
						.failureHandler(loginFailureHandler)
						.successHandler(loginSuccessHandler)
						.permitAll()
				)

				.oauth2Login(oauth2 -> oauth2
						.loginPage("/user/login")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)
						)
						.failureHandler(loginFailureHandler)
						.defaultSuccessUrl("/home")
				)

				.logout(logout -> logout
						.logoutUrl("/process-logout")
						.logoutSuccessUrl("/home")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID", "remember-me")
						.permitAll()
				)

				.sessionManagement(session -> session
						.maximumSessions(1)
						.expiredSessionStrategy(customSessionExpiredStrategy)
						.maxSessionsPreventsLogin(false)
				)

				.rememberMe(rememberMe -> rememberMe
						.rememberMeParameter("remember-me")
						.tokenValiditySeconds(30 * 24 * 60 * 60)    // 유효기간 : 30일
						.alwaysRemember(false)
				);

		return http.build();
	}
}

