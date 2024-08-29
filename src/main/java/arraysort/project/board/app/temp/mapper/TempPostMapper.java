package arraysort.project.board.app.temp.mapper;

import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.temp.domain.TempPostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface TempPostMapper {

	// 임시저장 게시글 추가
	void insertTempPost(TempPostVO vo);

	// 임시저장 게시글 리스트 조회
	List<TempPostVO> selectTempPostListWithPaging(PageDTO dto);

	// 임시저장 게시글 총 개수 조회
	int selectTotalTempPostCount(PageReqDTO dto, long boardId);

	// 임시저장 게시글 세부내용 조회
	Optional<TempPostVO> selectTempPostDetailByPostId(long tempPostId, long boardId);

	// 임시저장 게시글 게시 후 임시저장 게시글 삭제
	void deleteTempPost(long tempPostId);

	// 임시저장 게시글 수정
	void updateTempPost(TempPostVO vo, long tempPostId);
}
