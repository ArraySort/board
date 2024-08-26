package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.enums.Flag;
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

	private Flag privateFlag;

	private Flag activateFlag;

	private Flag deleteFlag;

	private Date createdAt;

	private Date updatedAt;

	private long views;

	private long commentCount;

	private long likeCount;

	private long dislikeCount;

	private boolean hasLiked;

	private boolean hasDisliked;

	// 게시글 세부 조회
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
				.commentCount(vo.getCommentCount())
				.likeCount(vo.getLikeCount())
				.dislikeCount(vo.getDislikeCount())
				.hasLiked(vo.isHasLiked())
				.hasDisliked(vo.isHasDisliked())
				.build();
	}
}
