package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.DuplicatedUserException;
import arraysort.project.board.app.exception.PasswordCheckException;
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
        validateAdd(dto);

        dto.encodePassword(passwordEncoder.encode(dto.getUserPassword()));

        UserVO vo = UserVO.of(dto);

        userMapper.insertUser(vo);
    }

    // 로그인 : Spring Security 적용
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return userMapper.selectUserByUserId(userId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(userId));
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
