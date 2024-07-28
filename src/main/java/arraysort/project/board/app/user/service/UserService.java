package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.DuplicatedUserException;
import arraysort.project.board.app.user.domain.UserSignupDto;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    // 회원가입
    @Transactional
    public void addUser(UserSignupDto dto) {
        if (userMapper.selectUserCountByUserId(dto.getUserId()) != 0) {
            throw new DuplicatedUserException();
        }

        // 비밀번호 암호화
        dto.encodePassword(passwordEncoder.encode(dto.getUserPassword()));

        UserVO vo = UserVO.of(dto);

        userMapper.insertUser(vo);
    }
}
