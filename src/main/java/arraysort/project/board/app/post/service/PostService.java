package arraysort.project.board.app.post.service;

import arraysort.project.board.app.post.domain.PostAddDTO;
import arraysort.project.board.app.post.domain.PostListDTO;
import arraysort.project.board.app.post.domain.PostVO;
import arraysort.project.board.app.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // 게시글 리스트 조회
    @Transactional(readOnly = true)
    public List<PostListDTO> findPostList() {
        return postMapper.selectPostList()
                .stream()
                .map(PostListDTO::of)
                .toList();
    }
}
