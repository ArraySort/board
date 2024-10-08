package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.temp.domain.TempPostPublishReqDTO;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostVO {

	private Long postId;    // 게시글 고유 번호

	private String userId;    // 사용자 ID

	private String adminId;    // 관리자 ID

	private Long boardId;    // 게시판 ID

	private Long categoryId;    // 카테고리 ID

	private Long imageId;    // 썸네일 이미지 ID

	private String userName;    // 사용자 이름

	private String boardName;    // 게시판 이름

	private String categoryName;    // 카테고리 이름

	private String title;    // 제목

	private String content;    // 내용

	private long views;    // 조회수

	private Flag privateFlag;    // 비공개 여부

	private Flag noticeFlag;    // 공지사항 여부

	private Flag activateFlag;    // 활성화 여부

	private Flag adoptedCommentFlag;    // 채택 댓글 여부

	private Flag deleteFlag;    // 삭제 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 생성 날짜

	private Date updatedAt;    // 수정 날짜

	private long commentCount;    // 댓글 개수

	private long likeCount;    // 좋아요 개수

	private long dislikeCount;    // 싫어요 개수

	private boolean hasLiked;    // 좋아요 여부

	private boolean hasDisliked;    // 싫어요 여부

	// 게시글 추가
	public static PostVO insertOf(PostAddReqDTO dto, long boardId) {
		return PostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(Flag.N)
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 관리자 : 게시글 추가
	public static PostVO insertAdminOf(PostAddAdminReqDTO dto, long boardId) {
		return PostVO.builder()
				.adminId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(dto.getNoticeFlag())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 게시글 수정
	public static PostVO updateOf(PostEditReqDTO dto, long postId) {
		return PostVO.builder()
				.postId(postId)
				.title(dto.getTitle())
				.content(dto.getContent())
				.categoryId(dto.getCategoryId())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(Flag.N)
				.build();
	}


	// 관리자 : 게시글 수정
	public static PostVO updateAdminOf(PostEditAdminReqDTO dto, long postId) {
		return PostVO.builder()
				.postId(postId)
				.title(dto.getTitle())
				.content(dto.getContent())
				.categoryId(dto.getCategoryId())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(dto.getNoticeFlag())
				.build();
	}

	// 임시저장 게시글 추가
	public static PostVO insertOf(TempPostPublishReqDTO dto, long boardId) {
		return PostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.noticeFlag(Flag.N)
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 갤러리 게시판 썸네일 이미지 업로드 시 ID 업데이트
	public void updateThumbnailImageId(Long thumbnailImageId) {
		this.imageId = thumbnailImageId;
	}
}
