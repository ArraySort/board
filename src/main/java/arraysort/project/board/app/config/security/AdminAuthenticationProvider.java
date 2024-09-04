package arraysort.project.board.app.config.security;

import arraysort.project.board.app.admin.domain.AdminVO;
import arraysort.project.board.app.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AdminAuthenticationProvider implements AuthenticationProvider {
	private final AdminMapper adminMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!(authentication instanceof UsernamePasswordAuthenticationToken)) {
			return null;
		}

		String adminId = (String) authentication.getPrincipal();
		String password = (String) authentication.getCredentials();

		AdminVO admin = adminMapper.selectAdminByAdminId(adminId)
				.orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

		if (!passwordEncoder.matches(password, admin.getAdminPassword())) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}

		return new UsernamePasswordAuthenticationToken(adminId, password, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
