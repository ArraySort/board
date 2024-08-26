package arraysort.project.board.app.comment.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.comment.domain.*;
import arraysort.project.board.app.comment.mapper.CommentMapper;
import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.exception.CommentNotFoundException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.history.service.CommentHistoryService;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.PageDTO;
import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.post.domain.PageResDTO;
import arraysort.project.board.app.post.domain.PostDetailResDTO;
import arraysort.project.board.app.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentMapper commentMapper;

	private final PostComponent postComponent;

	private final ImageService imageService;

	private final CommentHistoryService commentHistoryService;

	// 댓글 추가
	@Transactional
	public void addComment(CommentAddReqDTO dto, long boardId, long postId) {
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		// 추가 전 검증
		validateAdd(boardDetail, boardId, postId);

		CommentVO vo = CommentVO.insertOf(dto, postId);

		// 댓글 정보 처리(최상위 댓글 ID, 댓글 depth 저장)
		handleCommentInfo(vo, postId);

		// 댓글 추가
		commentMapper.insertComment(vo);

		// 이미지 업로드
		handleCommentImages(dto, boardDetail, vo);

		// 댓글 기록 추가(이미지 포함)
		commentHistoryService.addCommentHistory(vo);
	}

	// 댓글 리스트 조회(페이징)
	@Transactional(readOnly = true)
	public PageResDTO<CommentListResDTO> findCommentListWithPaging(PageReqDTO dto, long boardId, long postId) {
		// 게시판 검증(존재, 상태 검증)
		postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);

		int totalTopLevelCommentCount = commentMapper.selectTotalTopLevelCommentCount(dto, postId);
		PageDTO pageDTO = new PageDTO(totalTopLevelCommentCount, dto, boardId, postId);

		// 대댓글 렌더링을 위한 트리구조 변환
		List<CommentListResDTO> commentTree = buildCommentTree(getAllCommentList(postId, pageDTO));

		return new PageResDTO<>(totalTopLevelCommentCount, dto.getCommentPage(), commentTree);
	}

	// 댓글 수정
	@Transactional
	public void modifyComment(CommentEditReqDTO dto, long boardId, long postId) {
		// 게시판 검증(존재, 상태 검증)
		postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);

		// 댓글 검증(존재, 상태 검증)
		validateComment(dto.getCommentId());

		// 댓글 이미지 업데이트
		handleCommentImages(dto);

		// 댓글 수정
		CommentVO vo = CommentVO.updateOf(dto, postId);
		commentMapper.updateComment(vo);

		// 댓글 기록 추가(이미지 포함)
		commentHistoryService.addCommentHistory(vo);
	}

	// 댓글 삭제
	@Transactional
	public void removeComment(long boardId, long postId, long commentId) {
		// 게시판 검증(존재, 상태 검증)
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);

		// 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);

		// 댓글 검증(존재, 상태 검증)
		validateComment(commentId);

		// 하위 댓글 삭제
		removeRelies(commentId, boardDetail);

		// 댓글 이미지 삭제 처리
		handleCommentImageRemove(commentId, boardDetail);

		// 댓글 삭제
		commentMapper.deleteComment(commentId, UserUtil.getCurrentLoginUserId());
	}

	// 게시글 삭제 시 게시글 내부 댓글 삭제(이미지 포함)
	@Transactional
	public void removeCommentByPostRemove(BoardVO boardDetail, long boardId, long postId) {
		postComponent.getValidatedPost(postId, boardId);

		// 게시글 내부 댓글 전부 삭제
		commentMapper.deleteCommentsByPostId(postId, UserUtil.getCurrentLoginUserId());

		// 게시글 내부 댓글 이미지 삭제
		commentMapper.selectCommentListByPostId(postId).stream()
				.map(CommentVO::getCommentId)
				.forEach(commentId -> handleCommentImageRemove(commentId, boardDetail));
	}

	// 댓글 채택
	@Transactional
	public void adoptComment(CommentAdoptReqDTO dto, long boardId, long postId) {

		// 게시판 검증(존재, 상태 검증)
		postComponent.getValidatedBoard(boardId);

		// 게시글, 채택 댓글 검증(존재, 상태, 채택 사용자 검증)
		validateAdoptComment(dto, postComponent.getValidatedPost(postId, boardId));

		// 기존 댓글 채택 초기화(게시글 내부)
		commentMapper.resetAdoptedComment(postId, UserUtil.getCurrentLoginUserId());

		// 댓글 채택
		CommentVO vo = CommentVO.adoptOf(dto, postId);
		commentMapper.updateIsAdopted(vo);
	}

	/**
	 * 댓글 추가 전 검증
	 * 1. 게시판 댓글 허용 여부 검증
	 * 2. 로그인하지 않은 사용자에 대한 검증
	 * 3. 게시글 검증(존재, 상태)
	 * (댓글은 UserAccessLevel = 1 부터 가능, 비로그인은 0)
	 *
	 * @param boardId 현재 댓글을 작성하려는 게시판 ID
	 * @param postId  현재 댓글을 작성하려는 게시글 ID
	 */
	private void validateAdd(BoardVO boardDetail, long boardId, long postId) {
		// 1. 게시판 댓글 허용 여부 검증
		if (boardDetail.getCommentFlag() == Flag.N) {
			throw new InvalidPrincipalException("현재 게시판은 댓글을 허용하지 않습니다. ");
		}

		// 2. 로그인하지 않은 사용자에 대한 검증
		if (!UserUtil.isAuthenticatedUser()) {
			throw new InvalidPrincipalException("로그인이 필요합니다.");
		}

		// 3. 게시글 검증(존재, 상태 검증)
		postComponent.getValidatedPost(postId, boardId);
	}

	/**
	 * 댓글 검증
	 * 1. 댓글 존재 검증
	 * 2. 댓글 상태 검증
	 * 3. 댓글 소유자 검증
	 *
	 * @param commentId 수정하려는 댓글 ID
	 */
	private void validateComment(long commentId) {
		// 1. 댓글 존재 검증
		CommentVO commentDetail = commentMapper.selectCommentById(commentId)
				.orElseThrow(CommentNotFoundException::new);

		// 2. 댓글 상태 검증
		if (commentDetail.getActivateFlag() == Flag.N || commentDetail.getDeleteFlag() == Flag.Y) {
			throw new CommentNotFoundException();
		}

		validateCommentOwnership(commentDetail);
	}

	/**
	 * 댓글 소유자 검증
	 *
	 * @param commentDetail 현재 수정/삭제 하려는 댓글의 세부 정보
	 */
	private void validateCommentOwnership(CommentVO commentDetail) {
		if (UserUtil.isNotCurrentUserOwner(commentDetail.getUserId())) {
			throw new InvalidPrincipalException("본인이 작성한 댓글만 수정/삭제가 가능합니다.");
		}
	}

	/**
	 * [댓글 이미지 처리(추가)]
	 * 게시판 이미지 허용 여부가 Y 일 때만 실행
	 * 댓글 이미지는 최대 2개까지만 업로드 가능
	 *
	 * @param dto         추가하려는 댓글 정보
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param vo          추가된 댓글
	 */
	private void handleCommentImages(CommentAddReqDTO dto, BoardVO boardDetail, CommentVO vo) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		if (dto.getCommentImages() == null || dto.getCommentImages().isEmpty()) {
			return;
		}

		if (dto.getCommentImages().size() > 2) {
			throw new BoardImageOutOfRangeException("댓글은 최대 2개까지 업로드 가능합니다.");
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
	private void handleCommentImages(CommentEditReqDTO dto) {
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
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 댓글 이미지 삭제처리 'Y' 업데이트, 댓글 이미지 관계 삭제
	 *
	 * @param commentId   삭제하려는 댓글 ID
	 * @param boardDetail 검증된 게시판 세부정보
	 */
	private void handleCommentImageRemove(long commentId, BoardVO boardDetail) {
		if (boardDetail.getImageFlag() == Flag.N) {
			return;
		}

		List<Long> deleteCommentImageIds = imageService.findCommentImagesByCommentId(commentId)
				.stream()
				.map(ImageVO::getImageId)
				.toList();

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
	private void handleCommentInfo(CommentVO vo, long postId) {
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
			List<CommentListResDTO> childComments = commentMapper.selectRepliesForTopLevelComments(postId, topParentIds)
					.stream()
					.map(CommentListResDTO::of)
					.toList();

			allComments.addAll(childComments);
		}

		return allComments;
	}

	/**
	 * 댓글 트리구조 생성
	 *
	 * @param comments 현재 게시글 댓글 리스트
	 * @return 트리구조로 완성된 부모 댓글 리스트
	 */
	private List<CommentListResDTO> buildCommentTree(List<CommentListResDTO> comments) {
		// 쿼리 순서 보장
		Map<Long, CommentListResDTO> commentMap = comments
				.stream()
				.collect(Collectors.toMap(CommentListResDTO::getCommentId,
						comment -> comment,
						(existing, replacement) -> existing,
						LinkedHashMap::new
				));

		List<CommentListResDTO> rootComments = new ArrayList<>();

		// 부모 댓글 추가, 자식 댓글 리스트 추가
		commentMap.values().forEach(comment -> {
			if (comment.getParentId() == null) {
				rootComments.add(comment);
			} else {
				CommentListResDTO parent = commentMap.get(comment.getParentId());
				if (parent != null) {
					parent.addReply(comment);
				}
			}
		});

		return rootComments;
	}

	/**
	 * 댓글 하위 대댓글 삭제
	 *
	 * @param commentId   삭제하려는 댓글 ID
	 * @param boardDetail 검증된 게시판 정보
	 */
	private void removeRelies(Long commentId, BoardVO boardDetail) {
		List<Long> childCommentsIds = commentMapper.selectRepliesIdByParentCommentId(commentId);

		if (childCommentsIds == null) {
			return;
		}

		childCommentsIds.forEach(childCommentId -> removeRelies(childCommentId, boardDetail));

		handleCommentImageRemove(commentId, boardDetail);

		commentMapper.deleteComment(commentId, UserUtil.getCurrentLoginUserId());
	}

	/**
	 * 댓글 채택 전 검증
	 * 1. 댓글 존재 검증
	 * 2. 댓글 상태 검증
	 * 3. 채택 댓글 사용자 검증
	 * (채택당하는 댓글의 소유자 == 게시글 소유자, 채택하려는 사용자 != 게시글 소유자, 비로그인 경우 제한)
	 *
	 * @param dto        채택당하는 댓글의 정보
	 * @param postDetail 검증된 게시글 세부정보
	 */
	private void validateAdoptComment(CommentAdoptReqDTO dto, PostDetailResDTO postDetail) {
		// 1. 댓글 존재 검증
		CommentVO commentDetail = commentMapper.selectCommentById(dto.getCommentId())
				.orElseThrow(CommentNotFoundException::new);

		// 2. 댓글 상태 검증
		if (commentDetail.getActivateFlag() == Flag.N || commentDetail.getDeleteFlag() == Flag.Y) {
			throw new CommentNotFoundException();
		}

		// 3. 채택하려는 댓글이 게시글 소유자거나 인증되지 않은 사용자 인 경우, 채택하려는 사용자가 게시글 소유자가 아닌 경우
		if (UserUtil.isCurrentUserOwner(commentDetail.getUserId()) || UserUtil.isNotAuthenticatedUser() || UserUtil.isNotCurrentUserOwner(postDetail.getUserId())) {
			throw new InvalidPrincipalException("올바르지 않은 사용자입니다.");
		}
	}
}
