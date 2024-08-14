package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageDTO {

	private long totalPostCount;

	private int offset;

	private int pageRowCount;

	private PageReqDTO dto;

	private long boardId;

	private String userId;

	public PageDTO(long totalPostCount, PageReqDTO dto, long boardId) {
		this.totalPostCount = totalPostCount;
		this.pageRowCount = Constants.PAGE_ROW_COUNT;
		this.boardId = boardId;
		this.dto = dto;
		this.userId = UserUtil.getCurrentLoginUserId();
		offset = (dto.getPage() - 1) * pageRowCount;
	}
}
