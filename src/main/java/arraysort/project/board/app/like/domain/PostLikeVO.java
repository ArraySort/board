package arraysort.project.board.app.like.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLikeVO {

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	// 게시글 좋아요
	public static PostLikeVO of(String userId, long postId) {
		return PostLikeVO.builder()
				.userId(userId)
				.postId(postId)
				.build();
	}
}
