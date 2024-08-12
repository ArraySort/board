package arraysort.project.board.app.temp.mapper;

import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.temp.domain.TempPostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TempPostMapper {

	// 임시저장 게시글 추가
	void insertTempPost(TempPostVO vo);

	// 임시저장 게시글 총 개수 조회
	int selectTotalTempPostCount(PageReqDTO dto, long boardId);

	// 임시저장 게시글 리스트 조회
	List<TempPostVO> selectTempPostListWithPaging(int pageRowCount, int offset, PageReqDTO dto, long boardId, String userId);

	// 게시글 세부내용 조회
	Optional<TempPostVO> selectTempPostDetailByPostId(long tempPostId, long boardId);
}
