package arraysort.project.board.app.post.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.mapper.BoardMapper;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.exception.*;
import arraysort.project.board.app.post.domain.*;
import arraysort.project.board.app.post.mapper.PostMapper;
import arraysort.project.board.app.user.domain.UserVO;
import arraysort.project.board.app.user.mapper.UserMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;

	private final BoardMapper boardMapper;

	private final CategoryMapper categoryMapper;

	private final UserMapper userMapper;

	// 게시글 추가
	@Transactional
	public void addPost(PostAddReqDTO dto, long boardId) {
		PostVO vo = PostVO.insertOf(dto, boardId);

		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);
		UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
				.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));
		CategoryVO categoryDetail = categoryMapper.selectCategoryDetailById(dto.getCategoryId())
				.orElseThrow(CategoryNotFoundException::new);

		// TODO : 갤러리 게시판인지, 일반 게시판인지 검증 필요

		if (boardDetail.getActivateFlag().equals("N")) {
			throw new BoardNotFoundException();
		}

		// 게시글에서 고른 카테고리가 게시판에서 설정한 값과 일치하지 않을 때
		if (!Objects.equals(categoryDetail.getBoardId(), boardDetail.getBoardId())) {
			throw new InvalidPrincipalException("올바르지 않은 카테고리입니다.");
		}

		// 게시글 작성자 상태 확인 -> 비활성화 사용자, 삭제된 사용자
		if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}

		// 게시판 설정 접근 등급 보다 사용자 접근 등급이 낮을 경우, 사용자 접근 등급이 2미만 인 경우
		if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel()) || userDetail.getAccessLevel() < 2) {
			throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
		}

		postMapper.insertPost(vo);
	}

	// 게시글 리스트 조회(페이징 적용)
	@Transactional(readOnly = true)
	public PageResDTO findPostListWithPaging(PageReqDTO dto, long boardId) {

		int totalPostCount = postMapper.selectTotalPostCount(dto);
		int offset = (dto.getPage() - 1) * Constants.PAGE_ROW_COUNT;

		List<PostListResDTO> postList = postMapper.selectPostListWithPaging(
						Constants.PAGE_ROW_COUNT,
						offset,
						dto,
						boardId
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
		postMapper.updatePost(PostVO.updateOf(dto), postId);
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
