package arraysort.project.board.app.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostListResDTO {

	private long postId;

	private String userName;

	private String title;

	private Date createdAt;

	private Date updatedAt;

	private String categoryName;

	private long views;

	public static PostListResDTO of(PostVO vo) {
		return PostListResDTO.builder()
				.postId(vo.getPostId())
				.userName(vo.getUserName())
				.title(vo.getTitle())
				.createdAt(vo.getCreatedAt())
				.updatedAt(vo.getUpdatedAt())
				.categoryName(vo.getCategoryName())
				.views(vo.getViews())
				.build();
	}
}
