package arraysort.project.board.app.history.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentImageHistoryVO {

	private Long commentHistoryId;    // 댓글 기록 ID

	private Long imageId;    // 이미지 ID
}
