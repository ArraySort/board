package arraysort.project.board.app.like.service;

import arraysort.project.board.app.component.CommentComponent;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.like.domain.CommentDisLikeVO;
import arraysort.project.board.app.like.domain.CommentLikeDislikeResDTO;
import arraysort.project.board.app.like.domain.CommentLikeVO;
import arraysort.project.board.app.like.mapper.CommentLikeMapper;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

	private final CommentLikeMapper commentLikeMapper;

	private final CommentComponent commentComponent;

	// 댓글 좋아요/싫어요 처리
	@Transactional
	public CommentLikeDislikeResDTO handleCommentLikeDislike(long boardId, long postId, long commentId, boolean isLike) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		CommentLikeVO likeVO = CommentLikeVO.of(commentId);
		CommentDisLikeVO dislikeVO = CommentDisLikeVO.of(commentId);

		// 인증된 사용자만 좋아요 싫어요 가능
		if (UserUtil.isAuthenticatedUser()) {
			processLikeOrDislike(likeVO, dislikeVO, isLike);
		} else {
			throw new InvalidPrincipalException("로그인이 필요합니다.");
		}

		return getCommentLikeDislikeResponse(commentId);
	}

	// 관리자 : 모든 댓글 좋아요 수 조회
	@Transactional(readOnly = true)
	public long findAllLikesCount() {
		return commentLikeMapper.selectAllLikesCount();
	}

	/**
	 * 좋아요/싫어요 입력 처리
	 *
	 * @param likeVO    좋아요 VO
	 * @param dislikeVO 싫어요 VO
	 * @param isLike    좋아요/싫어요 여부
	 */
	private void processLikeOrDislike(CommentLikeVO likeVO, CommentDisLikeVO dislikeVO, boolean isLike) {
		// 좋아요/싫어요 입력 여부
		boolean hasLiked = commentLikeMapper.selectCommentLikeCountByUserId(likeVO);
		boolean hasDisliked = commentLikeMapper.selectCommentDislikeCountByUserId(dislikeVO);

		if (isLike) {
			handleCommentLikeInput(likeVO, dislikeVO, hasLiked, hasDisliked);
		} else {
			handleCommentDislikeInput(likeVO, dislikeVO, hasDisliked, hasLiked);
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
	private void handleCommentLikeInput(CommentLikeVO likeVO, CommentDisLikeVO dislikeVO, boolean hasLiked, boolean hasDisliked) {
		// 좋아요가 이미 눌린 상태
		if (hasLiked) {
			commentLikeMapper.deleteCommentLike(likeVO);
			// 싫어요가 이미 눌린 상태
		} else if (hasDisliked) {
			commentLikeMapper.deleteCommentDislike(dislikeVO);
			commentLikeMapper.insertCommentLike(likeVO);
			// 최초 좋아요를 눌렀을 때
		} else {
			commentLikeMapper.insertCommentLike(likeVO);
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
	private void handleCommentDislikeInput(CommentLikeVO likeVO, CommentDisLikeVO dislikeVO, boolean hasDisliked, boolean hasLiked) {
		// 싫어요가 이미 눌린 상태
		if (hasDisliked) {
			commentLikeMapper.deleteCommentDislike(dislikeVO);
			// 좋아요가 이미 눌린 상태
		} else if (hasLiked) {
			commentLikeMapper.deleteCommentLike(likeVO);
			commentLikeMapper.insertCommentDislike(dislikeVO);
			// 최초 싫어요를 눌렀을 때
		} else {
			commentLikeMapper.insertCommentDislike(dislikeVO);
		}
	}

	/**
	 * 좋아요/싫어요 조회 결과
	 *
	 * @param commentId 댓글 ID
	 * @return 좋아요/싫어요 조회 결과 ResponseDTO 객체
	 */
	private CommentLikeDislikeResDTO getCommentLikeDislikeResponse(long commentId) {
		List<CommentLikeVO> likeVO = commentLikeMapper.selectCommentLikeList(commentId);
		List<CommentDisLikeVO> dislikeVO = commentLikeMapper.selectCommentDislikeList(commentId);

		boolean hasLiked = false;
		boolean hasDisliked = false;

		long likeCount = likeVO.size();
		long dislikeCount = dislikeVO.size();

		for (CommentLikeVO like : likeVO) {
			if (UserUtil.isCurrentUserOwner(like.getUserId())) {
				hasLiked = true;
				break;
			}
		}

		if (hasLiked) {
			return new CommentLikeDislikeResDTO(likeCount, dislikeCount, true, false);
		}

		for (CommentDisLikeVO dislike : dislikeVO) {
			if (UserUtil.isCurrentUserOwner(dislike.getUserId())) {
				hasDisliked = true;
				break;
			}
		}

		return new CommentLikeDislikeResDTO(likeCount, dislikeCount, false, hasDisliked);
	}

}
