package arraysort.project.board.app.temp.domain;

import arraysort.project.board.app.utils.UserUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class TempPostVO {

	private Long tempPostId;

	private String userId;

	private Long boardId;

	private Long categoryId;

	private Long imageId;

	private String title;

	private String content;

	private String privateFlag;

	private String noticeFlag;

	private String createdBy;

	private String updatedBy;

	private Date createdAt;

	private Date updatedAt;

	// 임시 게시글 추가
	public static TempPostVO insertOf(TempPostAddDTO dto, long boardId) {
		return TempPostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 갤러리 게시판 썸네일 이미지 업로드 시 ID 업데이트
	public void updateThumbnailImageId(Long thumbnailImageId) {
		this.imageId = thumbnailImageId;
	}
}
