package arraysort.project.board.app.category.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryAddReqDTO {

	private Long boardId;

	@NotBlank(message = "카테고리 이름은 공백일 수 없습니다.")
	private String categoryName;
}
