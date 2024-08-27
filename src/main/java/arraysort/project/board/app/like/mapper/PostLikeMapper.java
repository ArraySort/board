package arraysort.project.board.app.like.mapper;

import arraysort.project.board.app.like.domain.PostDislikeVO;
import arraysort.project.board.app.like.domain.PostLikeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostLikeMapper {

	// 게시글 좋아요 추가
	void insertPostLike(PostLikeVO vo);

	// 게시글 좋아요 리스트 조회
	List<PostLikeVO> selectPostLikeList(long postId);

	// 게시글 좋아요 여부 조회(유저 ID)
	boolean selectPostLikeCountByUserId(PostLikeVO vo);

	// 게시글 좋아요 삭제
	void deletePostLike(PostLikeVO vo);

	// 게시글 싫어요 추가
	void insertPostDislike(PostDislikeVO vo);

	// 게시글 싫어요 리스트 조회
	List<PostDislikeVO> selectPostDislikeList(long postId);

	// 게시글 싫어요 여부 조회(유저 ID)
	boolean selectPostDislikeCountByUserId(PostDislikeVO vo);

	// 게시글 싫어요 삭제
	void deletePostDislike(PostDislikeVO vo);
}
