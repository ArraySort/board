package arraysort.project.board.app.user.service;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.exception.*;
import arraysort.project.board.app.user.domain.UserSignupReqDTO;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	// 회원가입
	@Transactional
	public void addUser(UserSignupReqDTO dto) {
		// 회원가입 검증
		validateAdd(dto);

		dto.encodePassword(passwordEncoder.encode(dto.getUserPassword()));

		UserVO vo = UserVO.of(dto);

		userMapper.insertUser(vo);
	}

	// 로그인 : Spring Security 적용
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserVO vo = userMapper.selectUserByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException(userId));

		// 유저 검증
		validateUser(vo);

		// 로그인 시도 검증, 잠금 시간 체크
		validateLoginAttempts(vo);

		return createUserDetails(vo);
	}

	/**
	 * 로그인 시도 검증
	 * 로그인 잠금 시간 검증
	 *
	 * @param vo 로그인 하려는 사용자 정보
	 */
	private void validateLoginAttempts(UserVO vo) {
		if (vo.getLoginTryCount() >= Constants.MAX_ATTEMPTS_COUNT &&
				vo.getLoginLock() != null && vo.getLoginLock().toInstant().isAfter(Instant.now())) {
			throw new LoginLockException("로그인 시도가 지정된 횟수를 초과하여 계정이 잠금 처리되었습니다. 잠시후에 다시 시도하세요.",
					new InvalidPrincipalException("정책위반"));
		}
	}

	/**
	 * 회원가입 시 입력한 아이디, 이름 중복검사
	 *
	 * @param dto 회원가입 폼에서 사용자가 입력한 값
	 */
	private void validateAdd(UserSignupReqDTO dto) {
		if (userMapper.selectUserCountByUserId(dto.getUserId()) != 0) {
			throw new DuplicatedUserException("이미 가입된 아이디입니다.");
		}

		if (userMapper.selectUserCountByUserName(dto.getUserName()) != 0) {
			throw new DuplicatedUserException("중복된 이름입니다.");
		}

		if (!Objects.equals(dto.getUserPassword(), dto.getUserPasswordCheck())) {
			throw new PasswordCheckException();
		}
	}

	/**
	 * 로그인 하려는 유저가 비활성화 상태인지, 삭제된 상태인지 검증
	 *
	 * @param vo 로그인 유저의 정보
	 */
	private void validateUser(UserVO vo) {
		if (vo.getActivateFlag() == Flag.N) {
			throw new NotActivatedUserException("관리자에 의해 비활성화 된 계정입니다.",
					new InvalidPrincipalException("정책 위반"));
		}

		if (vo.getDeleteFlag() == Flag.Y) {
			throw new UsernameNotFoundException(vo.getUserId());
		}
	}

	/**
	 * Spring Security 로그인
	 * UserDetails 를 만들어주는 메서드
	 */
	private UserDetails createUserDetails(UserVO vo) {
		return new User(
				vo.getUserId(),
				vo.getUserPassword(),
				Collections.singleton(new SimpleGrantedAuthority("USER"))
		);
	}
}
