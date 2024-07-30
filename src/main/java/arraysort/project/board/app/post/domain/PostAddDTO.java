package arraysort.project.board.app.post.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostAddDTO {

    @NotBlank(message = "제목은 필수 입력사항입니다.")
    @Size(min = 1, max = 50, message = "제목은 최소 1글자 최대 50글자까지 입력 가능합니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력사항입니다.")
    @Size(min = 1, max = 500, message = "내용은 최소 1글자 최대 500글자까지 입력 가능합니다.")
    private String content;

    @NotBlank(message = "카테고리 선택은 필수입니다.")
    private String category;

    private BoardType type;

}
