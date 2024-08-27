package arraysort.project.board.app.like.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDislikeVO {

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	// 게시글 싫어요
	public static PostDislikeVO of(String userId, long postId) {
		return PostDislikeVO.builder()
				.userId(userId)
				.postId(postId)
				.build();
	}
}
