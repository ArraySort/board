package arraysort.project.board.app.image.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentImageVO {

	private Long commentId;    // 댓글 ID

	private Long imageId;    // 이미지 ID

}
