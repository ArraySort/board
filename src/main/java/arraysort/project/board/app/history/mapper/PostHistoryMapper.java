package arraysort.project.board.app.history.mapper;

import arraysort.project.board.app.history.domain.PostHistoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostHistoryMapper {

	// 게시물 기록 추가
	void insertPostHistory(PostHistoryVO vo);

	//  게시글 이미지 기록 관계 추가
	void insertPostImageHistory(long postHistoryId, List<Long> imageIds);
}
