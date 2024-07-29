package arraysort.project.board.app.post.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostAddDTO {

    @NotBlank(message = "제목은 필수 입력사항입니다.")
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    @Size(min = 1, max = 500)
    private String content;

    @NotBlank
    private String category;

    @NotBlank
    private String type;

}
