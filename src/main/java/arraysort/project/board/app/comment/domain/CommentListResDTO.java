package arraysort.project.board.app.comment.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.image.domain.ImageVO;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
@Builder
public class CommentListResDTO {

	private long commentId;

	private String userId;

	private long postId;

	private String userName;

	private String commentContent;

	private Flag activateFlag;

	private String createdBy;

	private String updatedBy;

	private Date createdAt;

	private Date updatedAt;

	private Flag deleteFlag;

	private List<ImageVO> commentImages;

	// 댓글 리스트 조회
	public static CommentListResDTO of(CommentVO vo) {
		return CommentListResDTO.builder()
				.commentId(vo.getCommentId())
				.userId(vo.getUserId())
				.postId(vo.getPostId())
				.userName(vo.getUserName())
				.commentContent(vo.getCommentContent())
				.activateFlag(vo.getActivateFlag())
				.createdBy(vo.getCreatedBy())
				.updatedBy(vo.getUpdatedBy())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.deleteFlag(vo.getDeleteFlag())
				.build();
	}

	// 댓글 이미지 리스트 업데이트
	public void updateCommentImages(List<ImageVO> commentImages) {
		this.commentImages = commentImages;
	}
}
