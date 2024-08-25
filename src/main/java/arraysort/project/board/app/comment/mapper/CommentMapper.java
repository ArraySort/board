package arraysort.project.board.app.comment.mapper;

import arraysort.project.board.app.comment.domain.CommentAdoptReqDTO;
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

	// 댓글 리스트 조회(최상위 댓글, 페이징)
	List<CommentVO> selectTopLevelCommentListWithPaging(PageDTO dto);

	// 댓글 리스트 조회(모든 대댓글)
	List<CommentVO> selectRepliesForTopLevelComments(long postId, List<Long> topParentIds);

	// 댓글 리스트 조회(대댓글)
	List<Long> selectRepliesIdByParentCommentId(long parentId);

	// 최상위 댓글 총 개수 조회(페이징)
	int selectTotalTopLevelCommentCount(PageReqDTO dto, long postId);

	// 댓글 리스트 조회(게시글 내부 전체)
	List<CommentVO> selectCommentListByPostId(long postId);

	// 댓글 조회
	Optional<CommentVO> selectCommentById(long commentId);

	// 댓글 수정
	void updateComment(CommentVO vo);

	// 댓글 삭제
	void deleteComment(long commentId);

	// 댓글 삭제(게시글 ID)
	void deleteCommentsByPostId(long postId);

	// 댓글 채택 여부 업데이트
	void updateIsAdopted(CommentAdoptReqDTO dto, long postId);

	// 댓글 채택 여부 초기화
	void resetAdoptedComment(long postId);

}
