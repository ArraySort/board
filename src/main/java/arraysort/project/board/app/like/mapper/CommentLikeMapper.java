package arraysort.project.board.app.like.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeMapper {

	// 댓글 좋아요 추가
	void insertCommentLike(String userId, long commentId);

	// 댓글 좋아요 개수 조회
	long selectCommentLikeCount(long commentId);

	// 댓글 좋아요 여부 조회(유저 ID)
	boolean selectCommentLikeCountByUserId(String userId, long commentId);

	// 댓글 좋아요 삭제
	void deleteCommentLike(String userId, long commentId);

	// 댓글 싫어요 추가
	void insertCommentDislike(String userId, long commentId);

	// 댓글 싫어요 개수 조회
	long selectCommentDislikeCount(long commentId);

	// 댓글 싫어요 여부 조회(유저 ID)
	boolean selectCommentDislikeCountByUserId(String userId, long commentId);

	// 댓글 좋아요 삭제
	void deleteCommentDislike(String userId, long commentId);
}
