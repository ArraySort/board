package arraysort.project.board.app.image.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostImageVO {

	private Long postId;        // 게시글 ID

	private Long imageId;        // 이미지 ID
	
}
