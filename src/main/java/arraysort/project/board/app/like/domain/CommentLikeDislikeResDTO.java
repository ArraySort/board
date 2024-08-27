package arraysort.project.board.app.like.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentLikeDislikeResDTO {

	private long commentLikeCount;

	private long commentDislikeCount;

	private boolean commentHasLiked;

	private boolean commentHasDisliked;
}
