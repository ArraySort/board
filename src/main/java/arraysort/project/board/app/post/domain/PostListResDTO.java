package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostListResDTO {

	private long postId;

	private String adminId;

	private long boardId;

	private Long imageId;

	private String userName;

	private String title;

	private String content;

	private Date createdAt;

	private Date updatedAt;

	private String categoryName;

	private long views;

	private Flag privateFlag;

	private Flag noticeFlag;

	private Flag adoptedCommentFlag;

	private long postNumber;

	private long commentCount;

	private long likeCount;

	// 게시글 리스트 조회
	public static PostListResDTO of(PostVO vo) {
		return PostListResDTO.builder()
				.postId(vo.getPostId())
				.adminId(vo.getAdminId())
				.boardId(vo.getBoardId())
				.imageId(vo.getImageId())
				.userName(vo.getUserName())
				.title(vo.getTitle())
				.content(vo.getContent())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.categoryName(vo.getCategoryName())
				.views(vo.getViews())
				.privateFlag(vo.getPrivateFlag())
				.noticeFlag(vo.getNoticeFlag())
				.adoptedCommentFlag(vo.getAdoptedCommentFlag())
				.commentCount(vo.getCommentCount())
				.likeCount(vo.getLikeCount())
				.build();
	}
}
