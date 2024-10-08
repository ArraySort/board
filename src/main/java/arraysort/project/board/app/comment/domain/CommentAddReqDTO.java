package arraysort.project.board.app.comment.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class CommentAddReqDTO {

	private Long parentId;

	@NotBlank(message = "댓글을 등록하려면 입력하세요.")
	@Size(min = 1, max = 200, message = "댓글은 최대 200자까지 등록 가능합니다.")
	private String commentContent;

	private List<MultipartFile> commentImages;
}
