package arraysort.project.board.app.temp.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class TempPostListResDTO {

	private long postId;

	private Long imageId;

	private String userName;

	private String categoryName;

	private String title;

	private long views;

	private Date createdAt;

	private Date updatedAt;

	private Flag privateFlag;

	private long postNumber;

	// 임시저장 게시글 목록 조회
	public static TempPostListResDTO of(TempPostVO vo) {
		return TempPostListResDTO.builder()
				.postId(vo.getTempPostId())
				.imageId(vo.getImageId())
				.userName(vo.getUserName())
				.categoryName(vo.getCategoryName())
				.title(vo.getTitle())
				.views(0)
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.privateFlag(vo.getPrivateFlag())
				.build();
	}
}
