package arraysort.project.board.app.like.domain;

import arraysort.project.board.app.utils.UserUtil;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDislikeVO {

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	// 게시글 싫어요
	public static PostDislikeVO of(long postId) {
		return PostDislikeVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.postId(postId)
				.build();
	}
}
