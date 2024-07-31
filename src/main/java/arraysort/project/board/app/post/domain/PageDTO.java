package arraysort.project.board.app.post.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDTO {

    private int page;

    public PageDTO() {
        this.page = 1;
    }
}
