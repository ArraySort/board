package arraysort.project.board.app.like.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostLikeMapper {

	// 게시글 좋아요 추가
	void insertPostLike(String userId, long postId);

	// 게시글 좋아요 개수 조회
	long selectPostLikeCount(long postId);

	// 게시글 좋아요 여부 조회(유저 ID)
	boolean selectPostLikeCountByUserId(String userId, long postId);

	// 게시글 좋아요 삭제
	void deletePostLike(String userId, long postId);

	// 게시글 싫어요 추가
	void insertPostDislike(String userId, long postId);

	// 게시글 싫어요 개수 조회
	long selectPostDislikeCount(long postId);

	// 게시글 싫어요 여부 조회(유저 ID)
	boolean selectPostDislikeCountByUserId(String userId, long postId);

	// 게시글 싫어요 삭제
	void deletePostDislike(String userId, long postId);
}
