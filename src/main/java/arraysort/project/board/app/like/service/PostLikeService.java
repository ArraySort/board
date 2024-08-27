package arraysort.project.board.app.like.service;

import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.like.domain.PostDislikeVO;
import arraysort.project.board.app.like.domain.PostLikeDislikeResDTO;
import arraysort.project.board.app.like.domain.PostLikeVO;
import arraysort.project.board.app.like.mapper.PostLikeMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeMapper postLikeMapper;

	private final PostComponent postComponent;

	// 게시글 좋아요/싫어요 처리
	@Transactional
	public PostLikeDislikeResDTO handlePostLikeDislike(long boardId, long postId, boolean isLike) {
		// 게시판 검증(존재, 상태)
		postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태)
		postComponent.getValidatedPost(postId, boardId);

		PostLikeVO likeVO = PostLikeVO.of(UserUtil.getCurrentLoginUserId(), postId);
		PostDislikeVO dislikeVO = PostDislikeVO.of(UserUtil.getCurrentLoginUserId(), postId);

		// 인증된 사용자만 좋아요 싫어요 가능
		if (UserUtil.isAuthenticatedUser()) {
			processLikeOrDislike(likeVO, dislikeVO, isLike);
		}

		// 좋아요/싫어요 조회 결과 반환
		return getPostLikeDislikeResponse(postId);
	}

	/**
	 * 좋아요/싫어요 입력 처리
	 *
	 * @param likeVO    좋아요 VO
	 * @param dislikeVO 싫어요 VO
	 * @param isLike    좋아요/싫어요 여부
	 */
	private void processLikeOrDislike(PostLikeVO likeVO, PostDislikeVO dislikeVO, boolean isLike) {
		// 좋아요/싫어요 입력 여부
		boolean hasLiked = postLikeMapper.selectPostLikeCountByUserId(likeVO);
		boolean hasDisliked = postLikeMapper.selectPostDislikeCountByUserId(dislikeVO);

		if (isLike) {
			handleLikeInput(likeVO, dislikeVO, hasLiked, hasDisliked);
		} else {
			handleDislikeInput(likeVO, dislikeVO, hasDisliked, hasLiked);
		}
	}

	/**
	 * 좋아요가 눌렸을 때
	 *
	 * @param likeVO      좋아요 VO
	 * @param dislikeVO   싫어요 VO
	 * @param hasLiked    기존 좋아요 입력 여부
	 * @param hasDisliked 기존 싫어요 입력 여부
	 */
	private void handleLikeInput(PostLikeVO likeVO, PostDislikeVO dislikeVO, boolean hasLiked, boolean hasDisliked) {
		// 좋아요가 이미 눌린 상태
		if (hasLiked) {
			postLikeMapper.deletePostLike(likeVO);
			// 싫어요가 이미 눌린 상태
		} else if (hasDisliked) {
			postLikeMapper.deletePostDislike(dislikeVO);
			postLikeMapper.insertPostLike(likeVO);
			// 최초 좋아요를 눌렀을 때
		} else {
			postLikeMapper.insertPostLike(likeVO);
		}
	}

	/**
	 * 싫어요가 눌렸을 때
	 *
	 * @param likeVO      좋아요 VO
	 * @param dislikeVO   싫어요 VO
	 * @param hasLiked    기존 좋아요 입력 여부
	 * @param hasDisliked 기존 싫어요 입력 여부
	 */
	private void handleDislikeInput(PostLikeVO likeVO, PostDislikeVO dislikeVO, boolean hasDisliked, boolean hasLiked) {
		// 싫어요가 이미 눌린 상태
		if (hasDisliked) {
			postLikeMapper.deletePostDislike(dislikeVO);
			// 좋아요가 이미 눌린 상태
		} else if (hasLiked) {
			postLikeMapper.deletePostLike(likeVO);
			postLikeMapper.insertPostDislike(dislikeVO);
			// 최초 싫어요를 눌렀을 때
		} else {
			postLikeMapper.insertPostDislike(dislikeVO);
		}
	}

	/**
	 * 좋아요/싫어요 조회 결과
	 *
	 * @param postId 게시글 ID
	 * @return 좋아요/싫어요 조회 결과 ResponseDTO 객체
	 */
	private PostLikeDislikeResDTO getPostLikeDislikeResponse(long postId) {
		List<PostLikeVO> likeVO = postLikeMapper.selectPostLikeList(postId);
		List<PostDislikeVO> dislikeVO = postLikeMapper.selectPostDislikeList(postId);

		boolean hasLiked = false;
		boolean hasDisliked = false;

		long likeCount = likeVO.size();
		long dislikeCount = dislikeVO.size();

		for (PostLikeVO like : likeVO) {
			if (UserUtil.isCurrentUserOwner(like.getUserId())) {
				hasLiked = true;
				break;
			}
		}

		for (PostDislikeVO dislike : dislikeVO) {
			if (UserUtil.isCurrentUserOwner(dislike.getUserId())) {
				hasDisliked = true;
				break;
			}
		}

		return new PostLikeDislikeResDTO(likeCount, dislikeCount, hasLiked, hasDisliked);
	}
}