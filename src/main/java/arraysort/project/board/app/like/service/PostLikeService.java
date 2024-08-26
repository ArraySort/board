package arraysort.project.board.app.like.service;

import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.like.domain.PostLikeDislikeResDTO;
import arraysort.project.board.app.like.mapper.PostLikeMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeMapper postLikeMapper;

	private final PostComponent postComponent;

	@Transactional
	public PostLikeDislikeResDTO handlePostLikeDislike(long boardId, long postId, boolean isLike) {
		// 게시판 검증(존재, 상태)
		postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태)
		postComponent.getValidatedPost(postId, boardId);

		// 인증된 사용자만 좋아요 싫어요 가능
		if (UserUtil.isAuthenticatedUser()) {
			processLikeOrDislike(UserUtil.getCurrentLoginUserId(), postId, isLike);
		}

		return getPostLikeDislikeResponse(postId);
	}

	/**
	 * 좋아요/싫어요 증감
	 *
	 * @param userId 좋아요/싫어요를 누르는 유저 ID
	 * @param postId 좋아요/싫어요를 누른 게시글 ID
	 * @param isLike 좋아요/싫어요 여부
	 */
	private void processLikeOrDislike(String userId, long postId, boolean isLike) {
		// 좋아요/싫어요 입력 여부
		boolean hasLiked = postLikeMapper.selectPostLikeCountByUserId(userId, postId);
		boolean hasDisliked = postLikeMapper.selectPostDislikeCountByUserId(userId, postId);

		// 좋아요 인 경우
		if (isLike) {
			// 좋아요가 이미 눌린 상태
			if (hasLiked) {
				postLikeMapper.deletePostLike(userId, postId);
				// 싫어요가 이미 눌린 상태
			} else if (hasDisliked) {
				postLikeMapper.deletePostDislike(userId, postId);
				postLikeMapper.insertPostLike(userId, postId);
				// 최초 좋아요를 눌렀을 때
			} else {
				postLikeMapper.insertPostLike(userId, postId);
			}
		}

		// 싫어요 인 경우
		if (!isLike) {
			// 싫어요가 이미 눌린 상태
			if (hasDisliked) {
				postLikeMapper.deletePostDislike(userId, postId);
				// 좋아요가 이미 눌린 상태
			} else if (hasLiked) {
				postLikeMapper.deletePostLike(userId, postId);
				postLikeMapper.insertPostDislike(userId, postId);
				// 최초 싫어요를 눌렀을 때
			} else {
				postLikeMapper.insertPostDislike(userId, postId);
			}
		}
	}

	/**
	 * 좋아요/싫어요 조회 결과
	 *
	 * @param postId 좋아요/싫어요
	 * @return 좋아요/싫어요 Response 객체
	 */
	private PostLikeDislikeResDTO getPostLikeDislikeResponse(long postId) {
		// 좋아요 개수
		long likeCount = postLikeMapper.selectPostLikeCount(postId);
		// 싫어요 개수
		long dislikeCount = postLikeMapper.selectPostDislikeCount(postId);

		// 좋아요 여부
		boolean hasLiked = postLikeMapper.selectPostLikeCountByUserId(UserUtil.getCurrentLoginUserId(), postId);
		// 싫어요 여부
		boolean hasDisliked = postLikeMapper.selectPostDislikeCountByUserId(UserUtil.getCurrentLoginUserId(), postId);

		return new PostLikeDislikeResDTO(likeCount, dislikeCount, hasLiked, hasDisliked);
	}
}