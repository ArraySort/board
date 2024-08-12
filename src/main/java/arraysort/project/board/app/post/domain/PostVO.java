package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.temp.domain.TempPostEditReqDTO;
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

	private String privateFlag;    // 비공개 여부

	private String noticeFlag;    // 공지사항 여부

	private String activateFlag;    // 활성화 여부

	private String adoptedCommentFlag;    // 채택 댓글 여부

	private String deleteFlag;    // 삭제 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 생성 날짜

	private Date updatedAt;    // 수정 날짜

	// 게시물 추가
	public static PostVO insertOf(PostAddReqDTO dto, long boardId) {
		return PostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 게시물 수정
	public static PostVO updateOf(PostEditReqDTO dto, long postId) {
		return PostVO.builder()
				.postId(postId)
				.title(dto.getTitle())
				.content(dto.getContent())
				.categoryId(dto.getCategoryId())
				.privateFlag(dto.getPrivateFlag())
				.build();
	}

	// 임시저장 게시물 추가
	public static PostVO insertOf(TempPostEditReqDTO dto, long boardId) {
		return PostVO.builder()
				.userId(UserUtil.getCurrentLoginUserId())
				.boardId(boardId)
				.categoryId(dto.getCategoryId())
				.title(dto.getTitle())
				.content(dto.getContent())
				.privateFlag(dto.getPrivateFlag())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.updatedBy(UserUtil.getCurrentLoginUserId())
				.build();
	}

	// 갤러리 게시판 썸네일 이미지 업로드 시 ID 업데이트
	public void updateThumbnailImageId(Long thumbnailImageId) {
		this.imageId = thumbnailImageId;
	}
}
