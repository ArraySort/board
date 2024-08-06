package arraysort.project.board.app.post.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReqDTO {

	private int page;

	private String search;

	private SearchType searchType;

	private SortType sortType;

	public PageReqDTO() {
		this.page = 1;
		this.searchType = SearchType.ALL;
		this.sortType = SortType.ID;
	}
}
