package arraysort.project.board.app.post.mapper;

import arraysort.project.board.app.post.domain.PostVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {

    // 게시글 추가
    void insertPost(PostVO vo);

    // 게시글 리스트 조회
    List<PostVO> selectPostList();
}
