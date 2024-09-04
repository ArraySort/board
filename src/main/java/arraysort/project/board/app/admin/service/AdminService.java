package arraysort.project.board.app.admin.service;

import arraysort.project.board.app.admin.domain.AdminAddReqDTO;
import arraysort.project.board.app.admin.domain.AdminVO;
import arraysort.project.board.app.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static arraysort.project.board.app.common.Constants.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

	private final AdminMapper adminMapper;

	private final PasswordEncoder passwordEncoder;

	// 관리자 추가
	@Transactional
	public void addAdmin(AdminAddReqDTO dto) {
		dto.encodePassword(passwordEncoder.encode(dto.getAdminPassword()));
		AdminVO vo = AdminVO.of(dto);
		adminMapper.insertAdmin(vo);
	}

	// 관리자 로그인
	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		AdminVO vo = adminMapper.selectAdminByAdminId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("관리자를 찾을 수 없습니다."));

		return crateAdminDetails(vo);
	}
	
	/**
	 * Spring Security 로그인
	 * UserDetails 를 만들어주는 메서드
	 */
	private UserDetails crateAdminDetails(AdminVO vo) {
		return new User(
				vo.getAdminId(),
				vo.getAdminPassword(),
				Collections.singleton(new SimpleGrantedAuthority(ROLE_ADMIN))
		);
	}
}
