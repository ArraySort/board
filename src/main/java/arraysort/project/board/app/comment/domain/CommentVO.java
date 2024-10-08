package arraysort.project.board.app.comment.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {

	private Long commentId;    // 댓글 ID

	private String adminId;    // 관리자 ID

	private String userId;    // 유저 ID

	private Long postId;    // 게시글 ID

	private Long topParentId;    // 최상위 부모 댓글 ID

	private Long parentId;    // 댓글 부모 ID

	private int depth;    // 댓글 깊이

	private String userName;    // 유저 이름

	private String commentContent;    // 댓글 내용

	private Flag activateFlag;    // 활성화 여부

	private Flag adoptedFlag;    // 댓글 채택 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 최초 생성 날짜

	private Date updatedAt;    // 최종 수정 날짜

	private Flag deleteFlag;    // 삭제여부

	private List<ImageVO> commentImages;    // 댓글 이미지

	private long likeCount;    // 좋아요 개수

	private long dislikeCount;    // 싫어요 개수

	private boolean hasLiked;    // 좋아요 여부

	private boolean hasDisliked;    // 싫어요 여부

	// 댓글 추가
	public static CommentVO insertOf(CommentAddReqDTO dto, long postId) {
		return CommentVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.postId(postId)
				.parentId(dto.getParentId())
				.commentContent(dto.getCommentContent())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 댓글 수정
	public static CommentVO updateOf(CommentEditReqDTO dto, long postId) {
		return CommentVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.commentId(dto.getCommentId())
				.postId(postId)
				.commentContent(dto.getCommentContent())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 댓글 채택
	public static CommentVO adoptOf(CommentAdoptReqDTO dto, long postId) {
		return CommentVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.commentId(dto.getCommentId())
				.postId(postId)
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 최상위 댓글 ID, 댓글 depth 설정
	public void setCommentInfo(Long topParentId, int depth) {
		this.topParentId = topParentId;
		this.depth = depth;
	}
}
