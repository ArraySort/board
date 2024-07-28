package arraysort.project.board.app.user.mapper;

import arraysort.project.board.app.user.domain.UserVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 회원가입
    void insertUser(UserVO userVO);

    // 아이디 중복 확인을 위한 카운트
    int selectUserCountByUserId(String userId);
}
