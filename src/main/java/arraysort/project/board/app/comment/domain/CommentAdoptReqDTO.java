package arraysort.project.board.app.comment.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentAdoptReqDTO {

	@NotNull(message = "채택하려는 댓글이 존재하지 않습니다.")
	private Long commentId;
}
