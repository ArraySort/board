package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.NumberAssignable;
import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostListResDTO implements NumberAssignable {

	private long postId;

	private Long imageId;

	private String userName;

	private String title;

	private Date createdAt;

	private Date updatedAt;

	private String categoryName;

	private long views;

	private Flag privateFlag;

	private long postNumber;

	// 게시글 리스트 조회
	public static PostListResDTO of(PostVO vo) {
		return PostListResDTO.builder()
				.postId(vo.getPostId())
				.imageId(vo.getImageId())
				.userName(vo.getUserName())
				.title(vo.getTitle())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.categoryName(vo.getCategoryName())
				.views(vo.getViews())
				.privateFlag(vo.getPrivateFlag())
				.build();
	}

	// 게시글 번호 업데이트
	@Override
	public void updateNumber(long number) {
		this.postNumber = number;
	}
}
