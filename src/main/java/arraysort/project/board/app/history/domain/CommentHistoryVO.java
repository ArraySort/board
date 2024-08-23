package arraysort.project.board.app.history.domain;

import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class CommentHistoryVO {

	private Long commentHistoryId;    // 댓글 기록 ID

	private Long commentId;    // 댓글 ID

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	private String commentContent;    // 댓글 내용

	private Flag activateFlag;    // 활성화 여부

	private Date createdAt;    // 생성 날짜

	// 댓글 기록 추가
	public static CommentHistoryVO of(CommentVO vo) {
		return CommentHistoryVO.builder()
				.commentId(vo.getCommentId())
				.userId(vo.getUserId())
				.postId(vo.getPostId())
				.commentContent(vo.getCommentContent())
				.activateFlag(vo.getActivateFlag())
				.build();
	}
}
