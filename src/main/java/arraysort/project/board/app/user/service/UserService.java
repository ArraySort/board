package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.*;
import arraysort.project.board.app.user.domain.UserSignupReqDTO;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import jakarta.servlet.http.HttpSession;
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
import java.util.Objects;

import static arraysort.project.board.app.common.Constants.ROLE_USER;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

	private final UserPointService userPointService;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	// 회원가입
	@Transactional
	public void addUser(UserSignupReqDTO dto, HttpSession session) {
		// 회원가입 검증
		validateAdd(dto);

		// 이메일 인증 검증
		validateEmailVerified(session);

		dto.encodePassword(passwordEncoder.encode(dto.getUserPassword()));

		UserVO vo = UserVO.of(dto);
		userMapper.insertUser(vo);

		session.removeAttribute("isEmailVerified");
	}

	// 로그인 : 성공 시 LoginSuccessHandler 처리 / 실패 시 LoginFailureHandler 처리
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		UserVO vo = userMapper.selectUserByUserId(userId)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		// 유저 검증
		validateUser(vo);

		// 로그인 시도 검증, 잠금 시간 체크
		validateLoginAttempts(vo);

		return createUserDetails(vo);
	}

	/**
	 * 로그인 실패 시 처리
	 * 1. 로그인 시도 횟수 증가
	 * 2. 시도 횟수가 설정된 횟수보다 많으면 로그인 잠금 활성화
	 *
	 * @param userId 로그인을 시도하는 유저 ID
	 */
	@Transactional
	public void handleFailedLoginAttempts(String userId) {
		userMapper.selectUserByUserId(userId).ifPresent(vo -> {
			// 1. 로그인 시도 횟수 증가
			vo.incrementLoginTryCount();

			// 2. 로그인 잠금 활성화
			if (vo.shouldActivateLoginLock()) {
				vo.activateLoginLock();
			}

			userMapper.updateLoginAttempts(vo);
		});
	}

	/**
	 * 로그인 성공 시 처리
	 * 1. 로그인 시도 횟수, 로그인 잠금에 대한 초기화
	 * 2. 일일 최초 로그인 일 때 사용자 포인트 지급
	 * 3. 사용자 접근 시간 업데이트
	 *
	 * @param userId 로그인을 시도하는 유저 ID
	 */
	@Transactional
	public void handleSuccessLoginAttempts(String userId) {
		userMapper.selectUserByUserId(userId).ifPresent(vo -> {
			if (vo.hasLoginLockInfo()) {
				// 1. 로그인 시도 횟수 초기화
				vo.resetLoginStatus();

				userMapper.updateLoginAttempts(vo);
			}
		});
		// 2. 일일 최초 로그인 시 사용자 포인트 지급
		userPointService.giveUserPointForAttendance(userId);

		// 3. 사용자 접근 시간 업데이트
		userMapper.updateAccessTime(userId);
	}

	/**
	 * 회원가입 시 입력한 아이디, 이름 중복검사
	 *
	 * @param dto 회원가입 폼에서 사용자가 입력한 값
	 */
	private void validateAdd(UserSignupReqDTO dto) {
		if (userMapper.selectIsExistsByUserId(dto.getUserId())) {
			throw new DuplicatedUserException("이미 가입된 아이디입니다.");
		}

		if (userMapper.selectIsExistsByUserName(dto.getUserName())) {
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
		if (vo.isNotActivated()) {
			throw new NotActivatedUserException("관리자에 의해 비활성화 된 계정입니다.");
		}

		if (vo.isDeleted()) {
			throw new UsernameNotFoundException(vo.getUserId());
		}
	}

	/**
	 * 로그인 시도 검증
	 * 로그인 잠금 시간 검증
	 *
	 * @param vo 로그인 하려는 사용자 정보
	 */
	private void validateLoginAttempts(UserVO vo) {
		if (vo.isActivatedLoginLock()) {
			throw new LoginLockException("로그인 시도가 지정된 횟수를 초과하여 계정이 잠금 처리되었습니다. 잠시후에 다시 시도하세요.");
		}
	}

	/**
	 * 이메일 검증 여부 확인
	 *
	 * @param session 회원가입 페이지에서의 세션
	 */
	private void validateEmailVerified(HttpSession session) {
		boolean isEmailVerified = (boolean) session.getAttribute("isEmailVerified");
		if (!isEmailVerified) {
			throw new EmailValidationException("이메일 인증이 완료되지 않았습니다.");
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
				Collections.singleton(new SimpleGrantedAuthority(ROLE_USER))
		);
	}
}
