package arraysort.project.board.app.post.mapper;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.post.domain.PostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

	// 게시글 추가
	void insertPost(PostVO vo);

	// 게시글 리스트 조회
	List<PostVO> selectPostListWithPaging(PageDTO dto);

	// 총 게시물 개수 조회
	int selectTotalPostCount(PageReqDTO dto, long boardId);

	// 게시글 세부내용 조회
	Optional<PostVO> selectPostDetailByPostId(long postId, long boardId, String userId);

	// 게시글 수정
	void updatePost(PostVO vo, long postId);

	// 게시글 고유번호 조회
	Optional<Integer> selectExistPostId(long postId);

	// 게시글 삭제
	void deletePost(long postId);

	// 조회수 증가
	void updateViews(long postId);

	// 관리자 : 카테고리 사용 여부 반환
	boolean selectIsCategoryInUse(long categoryId);

	// 관리자 : 게시글 활성화 상태 변경
	void updateActivateFlag(long boardId, long postId, Flag flag);
}
