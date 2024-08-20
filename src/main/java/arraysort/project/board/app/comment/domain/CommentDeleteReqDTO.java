package arraysort.project.board.app.comment.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDeleteReqDTO {

	@NotNull(message = "삭제하려는 댓글이 존재하지 않습니다.")
	private Long commentId;

	private String commentContent;
}
