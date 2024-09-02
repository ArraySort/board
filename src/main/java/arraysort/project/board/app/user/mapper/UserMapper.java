package arraysort.project.board.app.user.mapper;

import arraysort.project.board.app.user.domain.OAuthVO;
import arraysort.project.board.app.user.domain.UserVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

	// 회원가입
	void insertUser(UserVO userVO);

	// 아이디 중복 확인을 위한 카운트
	boolean selectIsExistsByUserId(String userId);

	// 이름 중복 확인을 위한 카운트
	boolean selectIsExistsByUserName(String userId);

	// 로그인 시 저장된 아이디 확인
	Optional<UserVO> selectUserByUserId(String userId);

	// OAuth 회원가입
	void insertOAuthUser(OAuthVO oAuthVO);

	// OAuth 로 회원가입 한 유저 조회
	Optional<OAuthVO> selectOAuthUserByUserId(String userId);

	// 로그인 시도 정보 업데이트
	void updateLoginAttempts(UserVO vo);

	// 사용자 접근 시간 업데이트
	void updateAccessTime(String userId);

	// 게시글 작성에 따른 사용자 포인트 지급
	void updateUserPointForPost(String userId, int point);

	// 댓글 작성에 따른 사용자 포인트 지급
	void updateUserPointForComment(String userId, int point);

	// 일일 최초 로그인 시 포인트 지급
	void updateUserPointForAttendance(String userId, int point);
}
