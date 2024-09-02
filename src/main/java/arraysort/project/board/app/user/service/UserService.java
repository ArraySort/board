package arraysort.project.board.app.user.service;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.exception.*;
import arraysort.project.board.app.user.domain.UserSignupReqDTO;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
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

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;

import static arraysort.project.board.app.common.Constants.*;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

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
				.orElseThrow(() -> new UsernameNotFoundException(userId));

		// 유저 검증
		validateUser(vo);

		// 로그인 시도 검증, 잠금 시간 체크
		validateLoginAttempts(vo);

		return createUserDetails(vo);
	}

	// 게시글 작성에 따른 사용자 포인트 지급, Level 2 해당
	@Transactional
	public void giveUserPointForPost() {
		String userId = UserUtil.getCurrentLoginUserId();

		// 게시글 작성 시 포인트 지급(20)
		userMapper.updateUserPointForPost(userId, POST_POINT);
	}

	// 댓글 작성에 따른 사용자 포인트 지급
	@Transactional
	public void giveUserPointForComment() {
		UserVO vo = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId()).orElseThrow();

		// 레벨 2 보다 낮은 사용자인 경우
		if (vo.isBelowAccessLevel2()) {
			// 일일 댓글 제한에 도달하지 못했을 때(20)
			if (vo.isDailyCommentLimitNotReached()) {
				userMapper.updateUserPointForComment(vo.getUserId(), COMMENT_PONT_FOR_LEVEL1);
			}
		} else {
			// 레벨 2인 유저는 제한 없이 포인트 지급(10)
			userMapper.updateUserPointForComment(vo.getUserId(), COMMENT_PONT);
		}
	}

	/**
	 * 로그인 시도 실패 시 시도 횟수 증가
	 * 시도 횟수가 설정된 횟수보다 많으면 로그인 잠금 활성화
	 *
	 * @param userId 로그인을 시도하는 유저 ID
	 */
	@Transactional
	public void handleFailedLoginAttempts(String userId) {
		userMapper.selectUserByUserId(userId).ifPresent(vo -> {
			vo.incrementLoginTryCount();

			if (vo.getLoginTryCount() >= Constants.MAX_ATTEMPTS_COUNT &&
					vo.getLoginTryCount() % Constants.MAX_ATTEMPTS_COUNT == 0) {
				vo.activateLoginLock();
			}

			userMapper.updateLoginAttempts(vo);
		});
	}

	/**
	 * 로그인 상태 초기화
	 * 로그인 성공 시 로그인 시도 횟수, 로그인 잠금에 대한 초기화
	 * 로그인 성공 시 사용자 접근 시간 업데이트
	 *
	 * @param userId 로그인을 시도하는 유저 ID
	 */
	@Transactional
	public void resetLoginAttempts(String userId) {
		userMapper.selectUserByUserId(userId).ifPresent(vo -> {
			if (vo.getLoginTryCount() > 0 || vo.getLoginLock() != null) {
				vo.resetLoginStatus();
				// 로그인 시도 횟수 초기화
				userMapper.updateLoginAttempts(vo);
				// 사용자 접근 시간 업데이트
				userMapper.updateAccessTime(userId);
			}
		});
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
		if (vo.getActivateFlag() == Flag.N) {
			throw new NotActivatedUserException("관리자에 의해 비활성화 된 계정입니다.");
		}

		if (vo.getDeleteFlag() == Flag.Y) {
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
		if (vo.getLoginTryCount() >= Constants.MAX_ATTEMPTS_COUNT &&
				vo.getLoginLock() != null && vo.getLoginLock().toInstant().isAfter(Instant.now())) {
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
