package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.utils.UserUtil;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class PostVO {

    private Long postId;        // 게시글 고유 번호

    private String userId;      // 사용자 ID

    private String userName;    // 사용자 이름

    private String title;       // 제목

    private String content;     // 내용

    private Date createdAt;     // 생성 날짜

    private Date updatedAt;     // 수정 날짜

    private String category;    // 카테고리

    private BoardType type;     // 게시판 타입(GENERAL : 일반, GALLERY : 사진)

    private long views;         // 조회수

    // 게시물 추가
    public static PostVO of(PostAddDTO dto) {
        return PostVO.builder()
                .userId(UserUtil.getCurrentLoginUserId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .type(dto.getType())
                .build();
    }

    // 게시물 수정
    public static PostVO of(PostEditDTO dto) {
        return PostVO.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(dto.getCategory())
                .type(dto.getType())
                .build();
    }
}
