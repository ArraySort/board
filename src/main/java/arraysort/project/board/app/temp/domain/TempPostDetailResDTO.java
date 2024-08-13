package arraysort.project.board.app.temp.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class TempPostDetailResDTO {

	private Long tempPostId;

	private String userId;

	private Long boardId;

	private Long imageId;

	private String userName;

	private String categoryName;

	private String title;

	private String content;

	private Flag privateFlag;

	private Date createdAt;

	private Date updatedAt;

	// 임시저장 게시글 저장 정보 조회
	public static TempPostDetailResDTO of(TempPostVO vo) {
		return builder()
				.tempPostId(vo.getTempPostId())
				.userId(vo.getUserId())
				.boardId(vo.getBoardId())
				.imageId(vo.getImageId())
				.userName(vo.getUserName())
				.categoryName(vo.getCategoryName())
				.title(vo.getTitle())
				.content(vo.getContent())
				.privateFlag(vo.getPrivateFlag())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.build();
	}
}
