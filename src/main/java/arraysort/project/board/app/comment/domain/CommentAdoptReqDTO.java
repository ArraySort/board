package arraysort.project.board.app.comment.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentAdoptReqDTO {

	private Long commentId;

	private Flag adoptedFlag;

}
