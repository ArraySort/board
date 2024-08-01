package arraysort.project.board.app.post.service;

import arraysort.project.board.app.common.Constants;
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
    public void addPost(PostAddReqDTO dto) {
        postMapper.insertPost(PostVO.of(dto));
    }

    // 게시글 리스트 조회(페이징 적용)
    @Transactional(readOnly = true)
    public PageResDTO findPostListWithPaging(PageReqDTO dto) {

        int totalPostCount = postMapper.selectTotalPostCount(dto);
        int offset = (dto.getPage() - 1) * Constants.PAGE_ROW_COUNT;

        List<PostListResDTO> postList = postMapper.selectPostListWithPaging(
                        Constants.PAGE_ROW_COUNT,
                        offset,
                        dto
                )
                .stream()
                .map(PostListResDTO::of)
                .toList();

        return new PageResDTO(totalPostCount, dto.getPage(), postList);
    }

    // 게시글 세부내용 조회, 게시글 조회수 증가
    @Transactional
    public PostDetailResDTO findPostDetailByPostId(long postId) {
        increaseViews(postId);
        return PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId)
                .orElseThrow(DetailNotFoundException::new));
    }

    // 게시글 수정
    @Transactional
    public void modifyPost(PostEditReqDTO dto, long postId) {
        validatePostIdByUserId(postId);
        postMapper.updatePost(PostVO.of(dto), postId);
    }

    // 게시글 삭제
    @Transactional
    public void removePost(long postId) {
        validatePostIdByUserId(postId);
        postMapper.deletePost(postId);
    }

    // 게시글 조회수 증가
    private void increaseViews(long postId) {
        if (postMapper.selectExistPostId(postId).isEmpty()) {
            throw new IdNotFoundException();
        }
        postMapper.updateViews(postId);
    }

    /**
     * 게시물 수정 시 로그인 한 유저의 post 인지, DB에 존재하는 지 검증
     *
     * @param postId 수정 요청한 게시물 고유 번호
     */
    private void validatePostIdByUserId(long postId) {
        Optional<Integer> validPostId = postMapper.selectExistPostIdByUserId(postId, UserUtil.getCurrentLoginUserId());
        if (validPostId.isEmpty()) {
            throw new IdNotFoundException();
        }
    }
}
