package arraysort.project.board.app.comment.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.comment.domain.*;
import arraysort.project.board.app.comment.mapper.CommentMapper;
import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.common.page.PageResDTO;
import arraysort.project.board.app.component.CommentComponent;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.history.service.CommentHistoryService;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.user.service.UserPointService;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final ImageService imageService;

	private final CommentHistoryService commentHistoryService;

	private final UserPointService userPointService;

	private final PostComponent postComponent;

	private final CommentComponent commentComponent;

	private final CommentMapper commentMapper;

	// 댓글 추가
	@Transactional
	public void addComment(CommentAddReqDTO dto, long boardId, long postId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		// 추가 전 검증
		commentComponent.validateAdd(boardDetail, boardId, postId);

		CommentVO vo = CommentVO.insertOf(dto, postId);

		// 댓글 정보 처리(최상위 댓글 ID, 댓글 depth 저장)
		handleSetCommentInfo(vo, postId);

		// 댓글 추가
		commentMapper.insertComment(vo);

		// 댓글 추가 시 사용자 포인트 지급
		userPointService.giveUserPointForComment();

		// 이미지 업로드
		handleAddCommentImages(dto, vo);

		// 댓글 기록 추가(이미지 포함)
		commentHistoryService.addCommentHistory(vo);
	}

	// 댓글 리스트 조회(페이징)
	@Transactional(readOnly = true)
	public PageResDTO<CommentListResDTO> findCommentListWithPaging(PageReqDTO dto, long boardId, long postId) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 최상위 댓글 개수, Page 정보 생성
		int totalTopLevelCommentCount = commentMapper.selectTotalTopLevelCommentCount(dto, postId);
		PageDTO pageDTO = new PageDTO(dto, boardId, postId);

		// 대댓글 렌더링을 위한 트리구조 변환
		List<CommentListResDTO> commentTree = commentComponent.buildCommentTree(getAllCommentList(postId, pageDTO));

		return new PageResDTO<>(totalTopLevelCommentCount, dto.getCommentPage(), commentTree);
	}

	// 댓글 수정
	@Transactional
	public void modifyComment(CommentEditReqDTO dto, long boardId, long postId) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 댓글 검증(존재, 상태 검증)
		commentComponent.validateComment(dto.getCommentId());

		// 댓글 이미지 업데이트
		handleModifyCommentImages(dto);

		// 댓글 수정
		CommentVO vo = CommentVO.updateOf(dto, postId);
		commentMapper.updateComment(vo);

		// 댓글 기록 추가(이미지 포함)
		commentHistoryService.addCommentHistory(vo);
	}

	// 댓글 삭제
	@Transactional
	public void removeComment(long boardId, long postId, long commentId) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 댓글 검증(존재, 상태 검증)
		commentComponent.validateComment(commentId);

		// 하위 댓글 삭제
		handleRemoveRelies(commentId);

		// 댓글 이미지 삭제 처리
		handleRemoveCommentImage(commentId);

		// 댓글 삭제
		commentMapper.deleteComment(commentId, UserUtil.getCurrentLoginUserId());
	}

	// 게시글 삭제 시 게시글 내부 댓글 삭제(이미지 포함)
	@Transactional
	public void removeCommentByPostRemove(long boardId, long postId) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 게시글 내부 댓글 전부 삭제
		commentMapper.deleteCommentsByPostId(postId, UserUtil.getCurrentLoginUserId());

		// 게시글 내부 댓글 이미지 삭제 -> 댓글 내부 이미지 조회 쿼리 한번 실행(in 절 활용)
		List<Long> commentIds = commentMapper.selectCommentListByPostId(postId)
				.stream()
				.map(CommentVO::getCommentId)
				.toList();

		// 댓글 이미지 삭제 처리(댓글 ID 리스트)
		handleRemoveCommentImages(commentIds);
	}

	// 댓글 채택
	@Transactional
	public void adoptComment(CommentAdoptReqDTO dto, long boardId, long postId) {
		// 게시판, 게시글 검증(존재, 상태)
		commentComponent.validateBoardAndPost(boardId, postId);

		// 게시글, 채택 댓글 검증(존재, 상태, 채택 사용자 검증)
		commentComponent.validateAdoptComment(dto, postComponent.getValidatedPost(postId, boardId));

		// 기존 댓글 채택 초기화(게시글 내부)
		commentMapper.resetAdoptedComment(postId, UserUtil.getCurrentLoginUserId());

		// 댓글 채택
		CommentVO vo = CommentVO.adoptOf(dto, postId);
		commentMapper.updateIsAdopted(vo);
	}

	// 관리자 : 모든 댓글 수 조회
	@Transactional(readOnly = true)
	public long findAllCommentsCount() {
		return commentMapper.selectAllCommentsCount();
	}

	/**
	 * [댓글 이미지 처리(추가)]
	 * 게시판 이미지 허용 여부가 Y 일 때만 실행
	 * 댓글 이미지는 최대 2개까지만 업로드 가능
	 *
	 * @param dto 추가하려는 댓글 정보
	 * @param vo  추가된 댓글
	 */
	private void handleAddCommentImages(CommentAddReqDTO dto, CommentVO vo) {
		if (dto.getCommentImages() == null || dto.getCommentImages().isEmpty()) {
			return;
		}

		if (dto.getCommentImages().size() > Constants.MAX_COMMENT_IMAGE_LIMIT) {
			throw new BoardImageOutOfRangeException("댓글은 최대 " + Constants.MAX_COMMENT_IMAGE_LIMIT + " 개까지 업로드 가능합니다.");
		}

		imageService.addCommentImages(dto.getCommentImages(), vo.getCommentId());
	}

	/**
	 * [댓글 이미지 처리(수정)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 댓글 최대 업로드 개수 2에 대한 검증 실행
	 *
	 * @param dto 수정되는 게시글 정보(사용자 입력)
	 */
	private void handleModifyCommentImages(CommentEditReqDTO dto) {
		boolean addedCommentImageCheck = dto.getAddedCommentImages()
				.stream()
				.anyMatch(MultipartFile::isEmpty);

		int addedCommentImageCount = addedCommentImageCheck ? 0 : dto.getAddedCommentImages().size();

		int commentImageCount = imageService.findCommentImageCountByCommentId(dto.getCommentId()) + addedCommentImageCount - dto.getRemovedCommentImageIds().size();

		if (commentImageCount > 2) {
			throw new BoardImageOutOfRangeException("댓글은 최대 2개까지 업로드 가능합니다.");
		}

		if (!dto.getRemovedCommentImageIds().isEmpty()) {
			imageService.removeCommentImages(dto.getRemovedCommentImageIds());
		}

		imageService.addCommentImages(dto.getAddedCommentImages(), dto.getCommentId());
	}

	/**
	 * [댓글 삭제 시 이미지 삭제 처리]
	 * 댓글 이미지 삭제처리 'Y' 업데이트, 댓글 이미지 관계 삭제
	 *
	 * @param commentId 삭제하려는 댓글 ID
	 */
	private void handleRemoveCommentImage(long commentId) {
		// 댓글 이미지 조회(댓글 ID로 조회)
		List<Long> deleteCommentImageIds = imageService.findCommentImagesByCommentId(commentId)
				.stream()
				.map(ImageVO::getImageId)
				.toList();

		// 댓글에 포함된 이미지 일괄 삭제
		if (!deleteCommentImageIds.isEmpty()) {
			imageService.removeCommentImages(deleteCommentImageIds);
		}
	}

	/**
	 * [게시글 삭제 시 댓글 이미지 삭제 처리]
	 * 댓글 이미지 삭제처리 'Y' 업데이트, 댓글 이미지 관계 삭제
	 *
	 * @param commentIds 게시글 내부 댓글 ID 리스트
	 */
	private void handleRemoveCommentImages(List<Long> commentIds) {
		if (commentIds.isEmpty()) {
			return;
		}

		// 댓글 이미지 조회(삭제 댓글 ID 리스트로 조회 in 절 활용)
		List<Long> deleteCommentImageIds = imageService.findCommentImagesByCommentIds(commentIds)
				.stream()
				.map(ImageVO::getImageId)
				.toList();

		// 댓글에 포함된 이미지 일괄 삭제
		if (!deleteCommentImageIds.isEmpty()) {
			imageService.removeCommentImages(deleteCommentImageIds);
		}
	}


	/**
	 * 댓글 정보 처리
	 * 추가되는 댓글에 대한 최상위 부모 댓글 ID, depth 설정
	 *
	 * @param vo     추가되는 댓글 VO 객체
	 * @param postId 댓글이 추가되는 게시글 ID
	 */
	private void handleSetCommentInfo(CommentVO vo, long postId) {
		List<CommentListResDTO> allComments = commentMapper.selectCommentListByPostId(postId)
				.stream()
				.map(CommentListResDTO::of)
				.toList();

		Map<Long, CommentListResDTO> commentMap = allComments
				.stream()
				.collect(Collectors.toMap(CommentListResDTO::getCommentId, Function.identity()));

		// 최상위 부모 댓글 ID와 depth 계산
		Long topParentId = vo.getParentId() == null ? null : findCommentTopParentId(commentMap, vo.getParentId());
		int depth = vo.getParentId() == null ? 1 : findCommentDepth(commentMap, vo.getParentId());

		if (depth > 6) {
			throw new InvalidPrincipalException("대댓글은 최대 5개까지 작성 가능합니다.");
		}

		vo.setCommentInfo(topParentId, depth);
	}

	/**
	 * 최상위 댓글 부모 ID 생성 (재귀)
	 *
	 * @param commentMap Map(key : 댓글 부모 ID, value : 댓글 정보)
	 * @param parentId   댓글 부모 ID
	 * @return 최상위 댓글 부모 ID
	 */
	private Long findCommentTopParentId(Map<Long, CommentListResDTO> commentMap, Long parentId) {
		CommentListResDTO parentComment = commentMap.get(parentId);
		if (parentComment == null || parentComment.getParentId() == null) {
			return parentId;
		}
		return findCommentTopParentId(commentMap, parentComment.getParentId());
	}

	/**
	 * 댓글 depth 생성
	 *
	 * @param commentMap Map(key : 댓글 부모 ID, value : 댓글 정보)
	 * @param parentId   댓글 부모 ID
	 * @return 댓글 depth;
	 */
	private int findCommentDepth(Map<Long, CommentListResDTO> commentMap, Long parentId) {
		int depth = 1;
		while (parentId != null) {
			CommentListResDTO parentComment = commentMap.get(parentId);
			if (parentComment == null) {
				break;
			}
			depth++;
			parentId = parentComment.getParentId();
		}
		return depth;
	}

	/**
	 * 댓글 페이징을 위한 전체 댓글 조회
	 * 전체 댓글에 대한 이미지 추가
	 *
	 * @param postId  조회하려는 게시글 ID
	 * @param pageDTO 페이징을 위한 페이지 정보
	 * @return 해당 댓글 페이지에 있는 최상위 댓글과 대댓글(자식댓글)들이 포함된 전체 댓글 리스트
	 */
	private List<CommentListResDTO> getAllCommentList(long postId, PageDTO pageDTO) {
		// 최상위 댓글 조회(이미지 포함)
		List<CommentListResDTO> topLevelComments = commentMapper.selectTopLevelCommentListWithPaging(pageDTO)
				.stream()
				.map(CommentListResDTO::of)
				.toList();

		// 최상위 댓글 ID 저장
		List<Long> topParentIds = topLevelComments
				.stream()
				.map(CommentListResDTO::getCommentId)
				.toList();

		// 전체 댓글 리스트 생성(최상위 댓글 + 대댓글 리스트)
		List<CommentListResDTO> allComments = new ArrayList<>(topLevelComments);

		// 최상위 댓글 + 대댓글 리스트 추가(이미지 포함)
		if (!topParentIds.isEmpty()) {
			List<CommentListResDTO> childComments = commentMapper.selectRepliesForTopLevelComments(postId, topParentIds, UserUtil.getCurrentLoginUserId())
					.stream()
					.map(CommentListResDTO::of)
					.toList();

			allComments.addAll(childComments);
		}

		return allComments;
	}

	/**
	 * 댓글 하위 대댓글 삭제
	 *
	 * @param commentId 삭제하려는 댓글 ID
	 */
	private void handleRemoveRelies(Long commentId) {
		List<Long> childCommentsIds = commentMapper.selectRepliesIdByParentCommentId(commentId);

		if (childCommentsIds == null) {
			return;
		}

		childCommentsIds.forEach(this::handleRemoveRelies);

		handleRemoveCommentImage(commentId);

		commentMapper.deleteComment(commentId, UserUtil.getCurrentLoginUserId());
	}
}
