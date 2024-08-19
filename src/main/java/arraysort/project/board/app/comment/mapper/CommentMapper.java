package arraysort.project.board.app.comment.mapper;

import arraysort.project.board.app.comment.domain.CommentVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

	// 댓글 추가
	void insertComment(CommentVO vo);
}
