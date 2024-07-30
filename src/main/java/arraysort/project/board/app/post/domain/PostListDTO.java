package arraysort.project.board.app.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostListDTO {

    private long postId;

    private String userName;

    private String title;

    private Date createdAt;

    private Date updatedAt;

    private String category;

    private long views;

    public static PostListDTO of(PostVO vo) {
        return PostListDTO.builder()
                .postId(vo.getPostId())
                .userName(vo.getUserName())
                .title(vo.getTitle())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .category(vo.getCategory())
                .views(vo.getViews())
                .build();
    }
}
