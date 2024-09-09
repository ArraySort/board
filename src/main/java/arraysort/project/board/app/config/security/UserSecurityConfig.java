package arraysort.project.board.app.config.security;

import arraysort.project.board.app.user.service.CustomOAuth2UserService;
import arraysort.project.board.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Order(2)
@Configuration
@RequiredArgsConstructor
public class UserSecurityConfig {

	private final String[] permittedUrls = {
			"/", "/home",
			"/user/login", "/user/process-login", "/user/signup", "/user/process-signup",
			"/user/send-email-verification", "/user/verify-email-code",
			"/{boardId}/post/**", "/image/{imageId}",
			"/WEB-INF/views/**", "/resources/**"};

	private final LoginSuccessHandler loginSuccessHandler;

	private final LoginFailureHandler loginFailureHandler;

	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	private final CustomOAuth2UserService customOAuth2UserService;

	private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

	private final UserService userService;

	private final PasswordEncoder passwordEncoder;

	private final SessionRegistry redisSessionRegistry;

	@Bean
	public SecurityFilterChain userFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(permittedUrls).permitAll()
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
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
						.successHandler(loginSuccessHandler)
						.failureHandler(loginFailureHandler)
				)

				.logout(logout -> logout
						.logoutUrl("/process-logout")
						.logoutSuccessUrl("/home")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID", "remember-me")
						.permitAll()
				)

				// 중복 로그인 방지
				.sessionManagement(session -> session
						.maximumSessions(1)
						.sessionRegistry(redisSessionRegistry)
						.expiredSessionStrategy(customSessionExpiredStrategy)
						.maxSessionsPreventsLogin(false)
				)

				.rememberMe(rememberMe -> rememberMe
						.rememberMeParameter("remember-me")
						.tokenValiditySeconds(30 * 24 * 60 * 60)    // 유효기간 : 30일
						.alwaysRemember(false)
						.userDetailsService(userService)
				)

				.exceptionHandling(exceptionHandling -> exceptionHandling
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler)
				)

				.authenticationProvider(userAuthenticationProvider());

		return http.build();
	}

	// 유저 로그인 Provider 설정
	@Bean
	public DaoAuthenticationProvider userAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userService);
		provider.setPasswordEncoder(passwordEncoder);

		return provider;
	}
}

