package arraysort.project.board.app.like.domain;

import arraysort.project.board.app.utils.UserUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentLikeVO {

	private String userId;    // 유저 ID

	private Long commentId;    // 댓글 ID

	// 댓글 좋아요
	public static CommentLikeVO of(long commentId) {
		return CommentLikeVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.commentId(commentId)
				.build();
	}
}
