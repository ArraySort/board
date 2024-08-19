package arraysort.project.board.app.comment.mapper;

import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.post.domain.PageDTO;
import arraysort.project.board.app.post.domain.PageReqDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommentMapper {

	// 댓글 추가
	void insertComment(CommentVO vo);

	// 댓글 리스트 조회
	List<CommentVO> selectCommentListWithPaging(PageDTO dto);

	// 댓글 총 개수 조회
	int selectTotalCommentCount(PageReqDTO dto, long postId);

	// 댓글 조회
	Optional<CommentVO> selectCommentById(long commentId);

	// 댓글 수정
	void updateComment(CommentVO vo);

}
