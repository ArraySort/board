package arraysort.project.board.app.temp.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class TempPostEditReqDTO {

	@NotBlank(message = "제목은 필수 입력사항입니다.")
	@Size(min = 1, max = 50, message = "제목은 최소 1글자 최대 50글자까지 입력 가능합니다.")
	private String title;

	private String content;

	private String privateFlag;

	private Long categoryId;

	private MultipartFile thumbnailImage;

	private List<MultipartFile> addedImages;

	private List<Long> removedImageIds;

}
