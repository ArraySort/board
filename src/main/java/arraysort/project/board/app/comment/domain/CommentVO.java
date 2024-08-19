package arraysort.project.board.app.comment.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.utils.UserUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class CommentVO {

	private Long commentId;    // 댓글 ID

	private String adminId;    // 관리자 ID

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	private Long parentId;    // 댓글 부모 ID

	private String commentContent;    // 댓글 내용

	private Flag activateFlag;    // 활성화 여부

	private Flag adoptedFlag;    // 댓글 채택 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 최초 생성 날짜

	private Date updatedAt;    // 최종 수정 날짜

	private Flag deleteFlag;    // 삭제여부

	// 댓글 추가
	public static CommentVO insertOf(CommentAddReqDTO dto, long postId) {
		return CommentVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.postId(postId)
				.commentContent(dto.getCommentContent())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

}
