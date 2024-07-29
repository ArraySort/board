package arraysort.project.board.app.security;

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

    private final String[] permittedUrls = {"/", "/home", "/loginPage", "/process-login", "/signupPage", "/process-signup", "/WEB-INF/views/**"};

    private final LoginFailureHandler loginFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permittedUrls).permitAll()
                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/loginPage")                // 로그인하지 않은 사용자의 허용되지 않은 페이지 요청 시 이동
                        .loginProcessingUrl("/process-login")
                        .usernameParameter("userId")
                        .passwordParameter("userPassword")
                        .failureHandler(loginFailureHandler)
                        .defaultSuccessUrl("/home")
                        .permitAll()
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

