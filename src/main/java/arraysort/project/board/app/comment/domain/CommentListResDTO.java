package arraysort.project.board.app.comment.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.image.domain.ImageVO;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Builder
public class CommentListResDTO {

	private long commentId;

	private String adminId;

	private String userId;

	private long postId;

	private Long topParentId;

	private Long parentId;

	private int depth;

	private String userName;

	private String commentContent;

	private Flag activateFlag;

	private Flag adoptedFlag;

	private String createdBy;

	private String updatedBy;

	private Date createdAt;

	private Date updatedAt;

	private Flag deleteFlag;

	private List<ImageVO> commentImages;

	private List<CommentListResDTO> replies;

	private long likeCount;    // 좋아요 개수

	private long dislikeCount;    // 싫어요 개수

	private boolean hasLiked;    // 좋아요 여부

	private boolean hasDisliked;    // 싫어요 여부

	// 댓글 리스트 조회
	public static CommentListResDTO of(CommentVO vo) {
		return CommentListResDTO.builder()
				.commentId(vo.getCommentId())
				.adminId(vo.getAdminId())
				.userId(vo.getUserId())
				.postId(vo.getPostId())
				.topParentId(vo.getTopParentId())
				.parentId(vo.getParentId())
				.depth(vo.getDepth())
				.userName(vo.getUserName())
				.commentContent(vo.getCommentContent())
				.activateFlag(vo.getActivateFlag())
				.adoptedFlag(vo.getAdoptedFlag())
				.createdBy(vo.getCreatedBy())
				.updatedBy(vo.getUpdatedBy())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.deleteFlag(vo.getDeleteFlag())
				.commentImages(vo.getCommentImages())
				.likeCount(vo.getLikeCount())
				.dislikeCount(vo.getDislikeCount())
				.hasLiked(vo.isHasLiked())
				.hasDisliked(vo.isHasDisliked())
				.build();
	}

	// 자식 댓글 리스트 추가
	public void addReply(CommentListResDTO reply) {
		if (this.replies == null) {
			this.replies = new ArrayList<>();
		}
		this.replies.add(reply);
	}
}
