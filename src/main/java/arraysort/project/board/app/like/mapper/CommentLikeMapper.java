package arraysort.project.board.app.like.mapper;

import arraysort.project.board.app.like.domain.CommentDisLikeVO;
import arraysort.project.board.app.like.domain.CommentLikeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentLikeMapper {

	// 댓글 좋아요 추가
	void insertCommentLike(CommentLikeVO vo);

	// 댓글 좋아요 리스트 조회
	List<CommentLikeVO> selectCommentLikeList(long commentId);

	// 댓글 좋아요 여부 조회(유저 ID)
	boolean selectCommentLikeCountByUserId(CommentLikeVO vo);

	// 댓글 좋아요 삭제
	void deleteCommentLike(CommentLikeVO vo);

	// 댓글 싫어요 추가
	void insertCommentDislike(CommentDisLikeVO vo);

	// 댓글 싫어요 리스트 조회
	List<CommentDisLikeVO> selectCommentDislikeList(long commentId);

	// 댓글 싫어요 여부 조회(유저 ID)
	boolean selectCommentDislikeCountByUserId(CommentDisLikeVO vo);

	// 댓글 좋아요 삭제
	void deleteCommentDislike(CommentDisLikeVO vo);
}
