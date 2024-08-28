package arraysort.project.board.app.admin.service;

import arraysort.project.board.app.admin.domain.AdminAddDTO;
import arraysort.project.board.app.admin.domain.AdminLoginDTO;
import arraysort.project.board.app.admin.domain.AdminVO;
import arraysort.project.board.app.admin.mapper.AdminMapper;
import arraysort.project.board.app.exception.AdminIdNotFoundException;
import arraysort.project.board.app.exception.AdminPasswordNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final AdminMapper adminMapper;

	private final PasswordEncoder passwordEncoder;

	// 관리자 추가
	@Transactional
	public void addAdmin(AdminAddDTO dto) {
		dto.encodePassword(passwordEncoder.encode(dto.getAdminPassword()));
		AdminVO vo = AdminVO.of(dto);
		adminMapper.insertAdmin(vo);
	}

	// 로그인
	@Transactional(readOnly = true)
	public void login(AdminLoginDTO dto, HttpServletRequest request) {
		// ID 조회
		AdminVO vo = adminMapper.selectAdminByAdminId(dto.getAdminId())
				.orElseThrow(AdminIdNotFoundException::new);

		// 비밀번호 일치 여부 확인
		if (!passwordEncoder.matches(dto.getAdminPassword(), vo.getAdminPassword())) {
			throw new AdminPasswordNotFoundException();
		}

		// 인증 토큰 생성 : 관리자
		UsernamePasswordAuthenticationToken authenticationToken =
				new UsernamePasswordAuthenticationToken(dto.getAdminId(), dto.getAdminPassword(), Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

		// 인증 객체 설정
		SecurityContextHolder.getContext().setAuthentication(authenticationToken);

		// 세션 설정
		request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
	}
}
