package arraysort.project.board.app.history.domain;

import arraysort.project.board.app.post.domain.PostVO;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class PostHistoryVO {

	private Long postHistoryId;        // 게시글 기록 ID

	private Long postId;        // 게시글 ID

	private Long imageId;        // 썸네일 이미지 ID

	private String categoryName;        // 카테고리 이름

	private String title;        // 제목

	private String content;        // 내용

	private String privateFlag;        // 비공개 여부

	private String activateFlag;        // 활성화 여부

	private Date createdAt;        // 생성 날짜

	// 게시물 기록 추가
	public static PostHistoryVO of(PostVO vo, String categoryName) {
		return PostHistoryVO.builder()
				.postId(vo.getPostId())
				.imageId(vo.getImageId())
				.categoryName(categoryName)
				.title(vo.getTitle())
				.content(vo.getContent())
				.privateFlag(vo.getPrivateFlag())
				.activateFlag(vo.getActivateFlag())
				.build();
	}
}
