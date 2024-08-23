package arraysort.project.board.app.history.mapper;

import arraysort.project.board.app.history.domain.CommentHistoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentHistoryMapper {
	// 댓글 기록 추가
	void insertCommentHistory(CommentHistoryVO vo);

	//  댓글 이미지 기록 관계 추가
	void insertCommentImageHistory(long commentHistoryId, List<Long> imageIds);
}
