package arraysort.project.board.app.admin.service;

import arraysort.project.board.app.admin.domain.AdminAddReqDTO;
import arraysort.project.board.app.admin.domain.AdminLoginReqDTO;
import arraysort.project.board.app.admin.domain.AdminVO;
import arraysort.project.board.app.admin.mapper.AdminMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static arraysort.project.board.app.common.Constants.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminMapper adminMapper;

	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	// 관리자 추가
	@Transactional
	public void addAdmin(AdminAddReqDTO dto) {
		dto.encodePassword(passwordEncoder.encode(dto.getAdminPassword()));
		AdminVO vo = AdminVO.of(dto);
		adminMapper.insertAdmin(vo);
	}

	// 로그인
	@Transactional(readOnly = true)
	public void login(AdminLoginReqDTO dto, HttpServletRequest request) {

		// 인증 토큰 생성 : 관리자
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(dto.getAdminId(), dto.getAdminPassword(), Collections.singleton(new SimpleGrantedAuthority(ROLE_ADMIN)));

		// 인증
		Authentication authentication = authenticationManager.authenticate(authenticationToken);

		// 인증 객체 설정
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// 세션 설정
		request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
	}
}
