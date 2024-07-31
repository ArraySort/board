package arraysort.project.board.app.post.mapper;

import arraysort.project.board.app.post.domain.PostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

    // 게시글 추가
    void insertPost(PostVO vo);

    // 게시글 리스트 조회
    List<PostVO> selectPostListWithPaging(int pageRowCount, int offset);

    // 총 게시물 개수 조회
    int selectTotalPostCount();

    // 게시글 세부내용 조회
    Optional<PostVO> selectPostDetailByPostId(long postId);

    // 게시글 수정
    void updatePost(PostVO vo, long postId);

    // 게시글 고유번호 조회
    Optional<Integer> selectExistPostId(long postId, String userId);

    // 게시글 삭제
    void deletePost(long postId);

    // 조회수 증가
    void updateViews(long postId);
}
