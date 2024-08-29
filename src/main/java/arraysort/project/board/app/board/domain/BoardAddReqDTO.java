package arraysort.project.board.app.board.domain;

import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardAddReqDTO {

	@NotBlank(message = "게시판 이름은 공백일 수 없습니다.")
	@Size(min = 2, max = 20, message = "게시판 이름은 최소2글자 이상, 최대 20글자 이내여야 합니다.")
	private String boardName;

	@NotNull(message = "게시판 타입은 필수로 입력되어야 합니다.")
	private BoardType boardType;

	@NotNull(message = "게시판 순서는 필수로 입력되어야 합니다.")
	private Integer boardOrder;

	@NotNull(message = "카테고리는 최소 1개 이상 입력되어야 합니다.")
	private List<String> categories;

	@NotNull(message = "이미지 업로드 여부는 필수로 입력되어야 합니다.")
	private Flag imageFlag;

	private Integer imageLimit;

	@NotNull(message = "공지사항 최대 개수는 필수로 입력되어야 합니다.")
	private Integer noticeCount;

	@NotNull(message = "댓글 사용 여부는 필수로 입력되어야 합니다.")
	private Flag commentFlag;

	@NotNull(message = "접근 허용 등급은 필수로 입력되어야 합니다.")
	private Integer accessLevel;
}
