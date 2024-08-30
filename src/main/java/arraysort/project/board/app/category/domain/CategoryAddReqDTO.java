package arraysort.project.board.app.category.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryAddReqDTO {

	@NotNull(message = "게시판을 찾을 수 없습니다.")
	private Long boardId;

	@NotBlank(message = "카테고리 이름은 공백일 수 없습니다.")
	private String categoryName;
}
