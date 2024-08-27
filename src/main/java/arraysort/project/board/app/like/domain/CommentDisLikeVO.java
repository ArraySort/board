package arraysort.project.board.app.like.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDisLikeVO {

	private String userId;    // 유저 ID

	private Long commentId;    // 댓글 ID

	// 댓글 싫어요
	public static CommentDisLikeVO of(String userId, long commentId) {
		return CommentDisLikeVO.builder()
				.userId(userId)
				.commentId(commentId)
				.build();
	}
}
