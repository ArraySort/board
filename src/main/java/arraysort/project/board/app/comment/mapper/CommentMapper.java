package arraysort.project.board.app.comment.mapper;

import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.post.domain.PageDTO;
import arraysort.project.board.app.post.domain.PageReqDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

	// 댓글 추가
	void insertComment(CommentVO vo);

	// 댓글 리스트 조회
	List<CommentVO> selectCommentListWithPaging(PageDTO dto);

	// 댓글 총 개수 조회
	int selectTotalCommentCount(PageReqDTO dto, long postId);
}
