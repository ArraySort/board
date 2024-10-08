package arraysort.project.board.app.temp.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempPostVO {

	private Long tempPostId;

	private String userId;

	private Long boardId;

	private Long categoryId;

	private Long imageId;

	private String userName;

	private String categoryName;

	private String title;

	private String content;

	private Flag privateFlag;

	private Flag noticeFlag;

	private String createdBy;

	private String updatedBy;

	private Date createdAt;

	private Date updatedAt;

	// 임시 게시글 추가
	public static TempPostVO insertOf(TempPostAddReqDTO dto, long boardId) {
		return TempPostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(Flag.N)
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 임시 게시글 수정
	public static TempPostVO updateOf(TempPostEditReqDTO dto, long tempPostId) {
		return TempPostVO.builder()
				.tempPostId(tempPostId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 갤러리 게시판 썸네일 이미지 업로드 시 ID 업데이트
	public void updateThumbnailImageId(Long thumbnailImageId) {
		this.imageId = thumbnailImageId;
	}
}
