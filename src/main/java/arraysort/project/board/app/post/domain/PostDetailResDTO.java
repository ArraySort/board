package arraysort.project.board.app.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostDetailResDTO {

	private Long postId;

	private String userId;

	private Long boardId;

	private Long imageId;

	private String userName;

	private String boardName;

	private String categoryName;

	private String title;

	private String content;

	private String privateFlag;

	private String activateFlag;

	private String deleteFlag;

	private Date createdAt;

	private Date updatedAt;

	private long views;

	public static PostDetailResDTO of(PostVO vo) {
		return builder()
				.postId(vo.getPostId())
				.userId(vo.getUserId())
				.boardId(vo.getBoardId())
				.imageId(vo.getImageId())
				.userName(vo.getUserName())
				.boardName(vo.getBoardName())
				.categoryName(vo.getCategoryName())
				.title(vo.getTitle())
				.content(vo.getContent())
				.privateFlag(vo.getPrivateFlag())
				.activateFlag(vo.getActivateFlag())
				.deleteFlag(vo.getDeleteFlag())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.views(vo.getViews())
				.build();
	}
}
