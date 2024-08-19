package arraysort.project.board.app.comment.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentEditReqDTO {

	private Long commentId;

	@NotBlank(message = "댓글을 수정하려면 내용을 입력하세요.")
	@Size(min = 1, max = 200, message = "댓글은 최대 200자까지 등록 가능합니다.")
	private String commentContent;

}
