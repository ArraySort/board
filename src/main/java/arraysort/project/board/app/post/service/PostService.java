package arraysort.project.board.app.post.service;

import arraysort.project.board.app.post.domain.PostAddDTO;
import arraysort.project.board.app.post.domain.PostVO;
import arraysort.project.board.app.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    // 게시글 추가
    @Transactional
    public void addPost(PostAddDTO dto) {
        PostVO vo = PostVO.of(dto);
        postMapper.insertPost(vo);
    }
}
