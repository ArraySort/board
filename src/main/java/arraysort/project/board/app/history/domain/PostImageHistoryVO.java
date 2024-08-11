package arraysort.project.board.app.history.domain;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostImageHistoryVO {

	private Long imageHistoryId;        // 게시글 ID

	private Long imageId;        // 이미지 ID

}
