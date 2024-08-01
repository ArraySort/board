package arraysort.project.board.app.post.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostDetailResDTO {

    private Long postId;

    private String userId;

    private String userName;

    private String title;

    private String content;

    private Date createdAt;

    private Date updatedAt;

    private String category;

    private BoardType type;

    private long views;

    public static PostDetailResDTO of(PostVO vo) {
        return builder()
                .postId(vo.getPostId())
                .userId(vo.getUserId())
                .userName(vo.getUserName())
                .title(vo.getTitle())
                .content(vo.getContent())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .category(vo.getCategory())
                .type(vo.getType())
                .views(vo.getViews())
                .build();
    }
}
