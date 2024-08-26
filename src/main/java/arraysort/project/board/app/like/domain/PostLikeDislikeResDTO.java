package arraysort.project.board.app.like.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeDislikeResDTO {

	private long likeCount;

	private long dislikeCount;

	private boolean hasLiked;

	private boolean hasDisliked;

}
