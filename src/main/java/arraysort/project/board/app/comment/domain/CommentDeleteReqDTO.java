package arraysort.project.board.app.comment.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDeleteReqDTO {

	private Long commentId;

	private String commentContent;
}
