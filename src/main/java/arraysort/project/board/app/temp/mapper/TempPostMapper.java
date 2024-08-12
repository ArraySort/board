package arraysort.project.board.app.temp.mapper;

import arraysort.project.board.app.temp.domain.TempPostVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TempPostMapper {

	// 임시저장 게시글 추가
	void insertTempPost(TempPostVO vo);
}
