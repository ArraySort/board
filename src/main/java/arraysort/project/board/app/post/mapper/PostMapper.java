package arraysort.project.board.app.post.mapper;

import arraysort.project.board.app.post.domain.PostVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    // 게시글 추가
    void insertPost(PostVO vo);
}
