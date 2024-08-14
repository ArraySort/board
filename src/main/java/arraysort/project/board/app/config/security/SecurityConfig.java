package arraysort.project.board.app.config.security;

import arraysort.project.board.app.user.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final String[] permittedUrls = {"/", "/home", "/user/login", "/user/process-login", "/user/signup", "/user/process-signup", "/WEB-INF/views/**", "/resources/**"};

	private final LoginFailureHandler loginFailureHandler;

	private final OAuth2UserService oAuth2UserService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(permittedUrls).permitAll()
						.requestMatchers("/{boardId}/post/**").permitAll()
						.requestMatchers("/image/{imageId}").permitAll()
						.anyRequest()
						.authenticated()
				)

				.formLogin(form -> form
						.loginPage("/user/login")
						.loginProcessingUrl("/user/process-login")
						.usernameParameter("userId")
						.passwordParameter("userPassword")
						.failureHandler(loginFailureHandler)
						.defaultSuccessUrl("/home")
						.permitAll()
				)

				.oauth2Login(oauth2 -> oauth2
						.loginPage("/user/login")
						.userInfoEndpoint(userInfo -> userInfo
								.userService(oAuth2UserService)
						)
						.failureHandler(loginFailureHandler)
						.defaultSuccessUrl("/home")
				)

				.logout(logout -> logout
						.logoutUrl("/process-logout")
						.logoutSuccessUrl("/home")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll()
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}

