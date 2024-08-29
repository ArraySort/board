package arraysort.project.board.app.common.page;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageDTO {

	private long totalPostCount;

	private long totalCommentCount;

	private int offset;

	private int pageRowCount;

	private PageReqDTO dto;

	private long boardId;

	private long postId;

	private String userId;

	public PageDTO(long totalPostCount, PageReqDTO dto, long boardId) {
		this.totalPostCount = totalPostCount;
		this.pageRowCount = Constants.PAGE_ROW_COUNT;
		this.boardId = boardId;
		this.dto = dto;
		this.userId = UserUtil.getCurrentLoginUserId();
		offset = (dto.getPage() - 1) * pageRowCount;
	}

	public PageDTO(long totalCommentCount, PageReqDTO dto, long boardId, long postId) {
		this.totalCommentCount = totalCommentCount;
		this.pageRowCount = Constants.PAGE_ROW_COUNT;
		this.boardId = boardId;
		this.postId = postId;
		this.dto = dto;
		this.userId = UserUtil.getCurrentLoginUserId();
		offset = (dto.getCommentPage() - 1) * pageRowCount;
	}

	public PageDTO(long totalBoardCount, PageReqDTO dto) {
		this.totalPostCount = totalBoardCount;
		this.pageRowCount = Constants.PAGE_ROW_COUNT;
		this.dto = dto;
		offset = (dto.getPage() - 1) * pageRowCount;
	}
}
