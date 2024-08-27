package arraysort.project.board.app.like.service;

import arraysort.project.board.app.component.CommentComponent;
import arraysort.project.board.app.like.domain.CommentLikeDislikeResDTO;
import arraysort.project.board.app.like.mapper.CommentLikeMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

	private final CommentLikeMapper commentLikeMapper;

	private final CommentComponent commentComponent;


	@Transactional
	public CommentLikeDislikeResDTO handleCommentLikeDislike(long boardId, long postId, long commentId, boolean isLike) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 인증된 사용자만 좋아요 싫어요 가능
		if (UserUtil.isAuthenticatedUser()) {
			processLikeOrDislike(UserUtil.getCurrentLoginUserId(), commentId, isLike);
		}

		return getCommentLikeDislikeResponse(commentId);
	}

	/**
	 * 좋아요/싫어요 증감
	 *
	 * @param userId    좋아요/싫어요를 누르는 유저 ID
	 * @param commentId 좋아요/싫어요를 누른 댓글 ID
	 * @param isLike    좋아요/싫어요 여부
	 */
	private void processLikeOrDislike(String userId, long commentId, boolean isLike) {
		// 좋아요/싫어요 입력 여부
		boolean hasLiked = commentLikeMapper.selectCommentLikeCountByUserId(userId, commentId);
		boolean hasDisliked = commentLikeMapper.selectCommentDislikeCountByUserId(userId, commentId);

		// 좋아요 인 경우
		if (isLike) {
			// 좋아요가 이미 눌린 상태
			if (hasLiked) {
				commentLikeMapper.deleteCommentLike(userId, commentId);
				// 싫어요가 이미 눌린 상태
			} else if (hasDisliked) {
				commentLikeMapper.deleteCommentDislike(userId, commentId);
				commentLikeMapper.insertCommentLike(userId, commentId);
				// 최초 좋아요를 눌렀을 때
			} else {
				commentLikeMapper.insertCommentLike(userId, commentId);
			}
		}

		// 싫어요 인 경우
		if (!isLike) {
			// 싫어요가 이미 눌린 상태
			if (hasDisliked) {
				commentLikeMapper.deleteCommentDislike(userId, commentId);
				// 좋아요가 이미 눌린 상태
			} else if (hasLiked) {
				commentLikeMapper.deleteCommentLike(userId, commentId);
				commentLikeMapper.insertCommentDislike(userId, commentId);
				// 최초 싫어요를 눌렀을 때
			} else {
				commentLikeMapper.insertCommentDislike(userId, commentId);
			}
		}
	}

	/**
	 * 좋아요/싫어요 조회 결과
	 *
	 * @param postId 좋아요/싫어요
	 * @return 좋아요/싫어요 Response 객체
	 */
	private CommentLikeDislikeResDTO getCommentLikeDislikeResponse(long comment) {
		// 좋아요 개수
		long likeCount = commentLikeMapper.selectCommentLikeCount(comment);
		// 싫어요 개수
		long dislikeCount = commentLikeMapper.selectCommentDislikeCount(comment);

		// 좋아요 여부
		boolean hasLiked = commentLikeMapper.selectCommentLikeCountByUserId(UserUtil.getCurrentLoginUserId(), comment);
		// 싫어요 여부
		boolean hasDisliked = commentLikeMapper.selectCommentDislikeCountByUserId(UserUtil.getCurrentLoginUserId(), comment);

		return new CommentLikeDislikeResDTO(likeCount, dislikeCount, hasLiked, hasDisliked);
	}

}
