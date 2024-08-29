package arraysort.project.board.app.board.domain;

import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class BoardListResDTO {

	private long boardId;

	private String boardName;

	private BoardType boardType;

	private int boardOrder;

	private Flag imageFlag;

	private Integer imageLimit;

	private int noticeCount;

	private Flag commentFlag;

	private int accessLevel;

	private Flag activateFlag;

	private String createdBy;

	private String updatedBy;

	private Date createdAt;

	private Date updatedAt;

	private Flag deleteFlag;

	// 게시판 리스트 조회
	public static BoardListResDTO of(BoardVO vo) {
		return BoardListResDTO.builder()
				.boardId(vo.getBoardId())
				.boardName(vo.getBoardName())
				.boardType(vo.getBoardType())
				.boardOrder(vo.getBoardOrder())
				.imageFlag(vo.getImageFlag())
				.imageLimit(vo.getImageLimit())
				.noticeCount(vo.getNoticeCount())
				.commentFlag(vo.getCommentFlag())
				.accessLevel(vo.getAccessLevel())
				.activateFlag(vo.getActivateFlag())
				.createdBy(vo.getCreatedBy())
				.updatedBy(vo.getUpdatedBy())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.deleteFlag(vo.getDeleteFlag())
				.build();
	}
}
