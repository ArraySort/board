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

		// TODO : 갤러리 게시판인지, 일반 게시판인지 검증 필요

		// [게시판 검증]
		// 1. 게시글을 추가하려는 게시판 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 게시글을 추가하려는 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// 3. 게시글 추가 시 선택한 카테고리가 존재하는지 검증
		CategoryVO categoryDetail = categoryMapper.selectCategoryDetailById(dto.getCategoryId())
				.orElseThrow(CategoryNotFoundException::new);

		// 4. 게시글에서 고른 카테고리가 게시판에서 설정한 값과 일치하지 않을 때
		if (!Objects.equals(categoryDetail.getBoardId(), boardDetail.getBoardId())) {
			throw new InvalidPrincipalException("올바르지 않은 카테고리입니다.");
		}

		// [사용자 검증]
		// 1. 게시글을 추가할 때 작성자가 현재 로그인 한 사용자인지 검증
		UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
				.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

		// 2. 게시글을 추가할 때 작성자가 비활성화 상태, 삭제된 상태인지 검증
		if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}

		// 3. 게시글을 추가하려는 게시판의 접근 가능한 사용자 등급이 현재 작성자의 등급보다 높은 경우, 작성자의 등급이 작성 가능 등급보다 낮은 경우 검증
		if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel()) || userDetail.getAccessLevel() < 2) {
			throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
		}

		postMapper.insertPost(vo);
	}

	// 게시글 리스트 조회(페이징 적용)
	@Transactional(readOnly = true)
	public PageResDTO findPostListWithPaging(PageReqDTO dto, long boardId) {
		// [게시판 검증]
		// 1. 현재 조회하는 게시판이 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2.현재 조회하는 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// [사용자 검증]
		if (UserUtil.isAuthenticatedUser()) {
			// 1. 현재 조회하는 사용자가 존재하는지 검증
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			// 2. 현재 조회하는 사용자가 비활성화 상태, 삭제된 상태인지 검증
			if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
				throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
			}

			// 3. 현재 조회하는 게시판의 접근 가능한 사용자 등급이 현재 사용자 등급보다 높은 경우
			if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel())) {
				throw new InvalidPrincipalException("현재 등급으로는 게시판에 접속할 수 없습니다. 필요 등급 : " + boardDetail.getAccessLevel());
			}
		} else {    // 4. 비로그인 사용자 검증
			if (boardDetail.getAccessLevel() != 0) {
				throw new InvalidPrincipalException("현재 등급으로는 게시판에 접속할 수 없습니다. 로그인이 필요합니다.");
			}
		}

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
	public PostDetailResDTO findPostDetailByPostId(long postId, long boardId) {
		// 조회수 증가
		increaseViews(postId);

		// [게시판 검증]
		// 1. 게시글 세부내용 조회 한 게시글의 게시판이 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 게시글 세부내용 조회 한 게시글의 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// [사용자 검증]
		if (UserUtil.isAuthenticatedUser()) {
			// 1. 현재 조회 한 사용자가 존재하는지 검증
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			// 2. 현재 조회하는 사용자가 비활성화 상태, 삭제된 상태인지 검증
			if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
				throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
			}
			// 3. 현재 조회하는 게시판의 접근 가능한 사용자 등급이 현재 사용자 등급보다 높은 경우
			if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel())) {
				throw new InvalidPrincipalException("현재 등급으로는 게시판에 접속할 수 없습니다.");
			}
		} else {    // 4. 비로그인 사용자 검증
			if (boardDetail.getAccessLevel() != 0) {
				throw new InvalidPrincipalException("현재 등급으로는 게시판에 접속할 수 없습니다. 로그인이 필요합니다.");
			}
		}

		// [게시글 검증]
		// 1. 조회 한 게시글이 존재하는지 검증
		PostDetailResDTO postDetail = PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId, boardId)
				.orElseThrow(DetailNotFoundException::new));

		// 2. 조회 한 게시글의 삭제, 비활성화 여부 검증
		if (postDetail.getActivateFlag().equals("N") || postDetail.getDeleteFlag().equals("Y")) {
			throw new DetailNotFoundException();
		}

		// 3. 조회 한 게시글의 비공개 여부 검증 : 비공개 게시글은 작성자만 접근 가능
		if (postDetail.getPrivateFlag().equals("Y") &&
				!Objects.equals(postDetail.getUserId(), UserUtil.getCurrentLoginUserId())) {
			throw new InvalidPrincipalException("비공개 게시글은 작성자만 볼 수 있습니다.");
		}

		return postDetail;
	}

	// 게시글 수정
	@Transactional
	public void modifyPost(PostEditReqDTO dto, long postId, long boardId) {
		// [게시판 검증]
		// 1. 게시글을 수정하려는 게시판 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 게시글을 수정하려는 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// 3. 게시글 수정 시 선택한 카테고리가 존재하는지 검증
		CategoryVO categoryDetail = categoryMapper.selectCategoryDetailById(dto.getCategoryId())
				.orElseThrow(CategoryNotFoundException::new);

		// 4. 게시글에서 고른 카테고리가 게시판에서 설정한 값과 일치하지 않을 때
		if (!Objects.equals(categoryDetail.getBoardId(), boardDetail.getBoardId())) {
			throw new InvalidPrincipalException("올바르지 않은 카테고리입니다.");
		}

		// [사용자 검증]
		// 1. 게시글을 수정할 때 수정자가 현재 로그인 한 사용자인지 검증
		UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
				.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

		// 2. 게시글을 수정할 때 수정자가 비활성화 상태, 삭제된 상태인지 검증
		if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}

		// 3. 게시글을 수정하려는 게시판의 접근 가능한 사용자 등급이 현재 수정자의 등급보다 높은 경우, 수정자의 등급이 수정 가능 등급보다 낮은 경우 검증
		if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel()) || userDetail.getAccessLevel() < 2) {
			throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
		}

		// [게시글 검증]
		// 1. 수정하려는 게시글이 존재하는지 검증
		PostDetailResDTO postDetail = PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId, boardId)
				.orElseThrow(DetailNotFoundException::new));

		// 2. 수정하려는 게시글이 현재 수정자의 것인지 검증
		if (!Objects.equals(postDetail.getUserId(), UserUtil.getCurrentLoginUserId())) {
			throw new IdNotFoundException();
		}

		// 3. 조회 한 게시글의 삭제, 비활성화 여부 검증
		if (postDetail.getActivateFlag().equals("N") || postDetail.getDeleteFlag().equals("Y")) {
			throw new DetailNotFoundException();
		}

		postMapper.updatePost(PostVO.updateOf(dto), postId);
	}

	// 게시글 삭제
	@Transactional
	public void removePost(long postId, long boardId) {
		// [게시판 검증]
		// 1. 삭제하려는 게시글의 게시판이 존재하는지 검증
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 2. 삭제하려는 게시글의 게시판이 비활성화 상태인지, 삭제된 상태인지 검증
		if (boardDetail.getActivateFlag().equals("N") || boardDetail.getDeleteFlag().equals("Y")) {
			throw new BoardNotFoundException();
		}

		// [사용자 검증]
		// 1. 게시글을 삭제할 때 삭제자가 현재 로그인 한 사용자인지 검증
		UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
				.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

		// 2. 게시글을 삭제할 때 삭제자가 비활성화 상태, 삭제된 상태인지 검증
		if (Objects.equals(userDetail.getActivateFlag(), "N") || Objects.equals(userDetail.getDeleteFlag(), "Y")) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}

		// 3. 게시글을 삭제하려는 게시판의 접근 가능한 사용자 등급이 현재 삭제자의 등급보다 높은 경우, 삭제자의 등급이 삭제 가능 등급보다 낮은 경우 검증
		if ((boardDetail.getAccessLevel() > userDetail.getAccessLevel()) || userDetail.getAccessLevel() < 2) {
			throw new InvalidPrincipalException("올바르지 않은 사용자 접근 등급입니다.");
		}

		// [게시글 검증]
		// 1. 삭제하려는 게시글이 존재하는지 검증
		PostDetailResDTO postDetail = PostDetailResDTO.of(postMapper.selectPostDetailByPostId(postId, boardId)
				.orElseThrow(DetailNotFoundException::new));

		// 2. 삭제하려는 게시글이 현재 수정자의 것인지 검증
		if (!Objects.equals(postDetail.getUserId(), UserUtil.getCurrentLoginUserId())) {
			throw new IdNotFoundException();
		}

		// 3. 삭제하려는 게시글의 삭제, 비활성화 여부 검증
		if (postDetail.getActivateFlag().equals("N") || postDetail.getDeleteFlag().equals("Y")) {
			throw new DetailNotFoundException();
		}

		postMapper.deletePost(postId);
	}

	// 게시물 작성 페이지 요청에 대한 사용자 검증
	@Transactional(readOnly = true)
	public void validateAddByUserLevel() {
		if (UserUtil.isAuthenticatedUser()) {
			UserVO userDetail = userMapper.selectUserByUserId(UserUtil.getCurrentLoginUserId())
					.orElseThrow(() -> new UsernameNotFoundException(UserUtil.getCurrentLoginUserId()));

			if (userDetail.getAccessLevel() < 2) {
				throw new InvalidPrincipalException("게시글을 작성하려면 등급 2 이상이어야 합니다.");
			}
		} else {
			throw new InvalidPrincipalException("게시글을 작성하려면 로그인이 필요합니다.");
		}
	}

	/**
	 * 게시글 리스트에서 게시글 세부내용 조회 시 조회수 증가
	 *
	 * @param postId 세부내용 조회 한 게시글 ID
	 */
	private void increaseViews(long postId) {
		if (postMapper.selectExistPostId(postId).isEmpty()) {
			throw new IdNotFoundException();
		}
		postMapper.updateViews(postId);
	}
}
