package arraysort.project.board.app.common.page;

import arraysort.project.board.app.post.domain.SearchType;
import arraysort.project.board.app.post.domain.SortType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReqDTO {

	private int page;

	private int commentPage;

	private String search;

	private SearchType searchType;

	private SortType sortType;

	public PageReqDTO() {
		this.page = 1;
		this.commentPage = 1;
		this.searchType = SearchType.ALL;
		this.sortType = SortType.LATEST;
	}
}
