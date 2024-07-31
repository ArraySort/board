package arraysort.project.board.app.post.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageDTO {

    private int page;

    private String search;

    private SearchType searchType;

    public PageDTO() {
        this.page = 1;
        this.searchType = SearchType.ALL;
    }
}
