package arraysort.project.board.app.category.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryVO {

	private Long categoryId;    // 카테고리 ID

	private Long boardId;    // 게시판 ID

	private String categoryName;    // 카테고리 이름

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private String deleteFlag;    // 삭제 여부
	
}
