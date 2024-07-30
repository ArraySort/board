package arraysort.project.board.app.post.service;

import arraysort.project.board.app.exception.DetailNotFoundException;
import arraysort.project.board.app.exception.IdNotFoundException;
import arraysort.project.board.app.post.domain.*;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    // 게시글 추가
    @Transactional
    public void addPost(PostAddDTO dto) {
        postMapper.insertPost(PostVO.of(dto));
    }

    // 게시글 리스트 조회
    @Transactional(readOnly = true)
    public List<PostListDTO> findPostList() {
        return postMapper.selectPostList()
                .stream()
                .map(PostListDTO::of)
                .toList();
    }

    // 게시글 세부내용 조회
    @Transactional(readOnly = true)
    public PostDetailDTO findPostDetailByPostId(long postId) {
        return PostDetailDTO.of(postMapper.selectPostDetailByPostId(postId)
                .orElseThrow(DetailNotFoundException::new));
    }

    // 게시글 수정
    @Transactional
    public void modifyPost(PostEditDTO dto, long postId) {
        validateModify(postId);
        postMapper.updatePost(PostVO.of(dto), postId);
    }

    /**
     * 게시물 수정 시 로그인 한 유저의 post 인지, DB에 존재하는 지 검증
     *
     * @param postId 수정 요청한 게시물 고유 번호
     */
    private void validateModify(long postId) {
        Optional<Integer> validPostId = postMapper.selectExistPostId(postId, UserUtil.getCurrentLoginUserId());
        if (validPostId.isEmpty()) {
            throw new IdNotFoundException();
        }
    }
}
