package arraysort.project.board.app.board.service;

import arraysort.project.board.app.board.domain.BoardAddReqDTO;
import arraysort.project.board.app.board.domain.BoardEditReqDTO;
import arraysort.project.board.app.board.domain.BoardListResDTO;
import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.mapper.BoardMapper;
import arraysort.project.board.app.category.service.CategoryService;
import arraysort.project.board.app.common.page.PageDTO;
import arraysort.project.board.app.common.page.PageReqDTO;
import arraysort.project.board.app.common.page.PageResDTO;
import arraysort.project.board.app.exception.BoardNotFoundException;
import arraysort.project.board.app.exception.DuplicatedBoardException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper boardMapper;

	private final CategoryService categoryService;

	// 게시판 전체 조회
	@Transactional(readOnly = true)
	public List<BoardVO> findAllBoards() {
		return boardMapper.selectAllBoards();
	}

	// 게시판 ID 로 게시판 조회
	@Transactional(readOnly = true)
	public BoardVO findBoardDetailById(long boardId) {
		return boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);
	}

	// 관리자 : 게시판 추가
	@Transactional
	public void addBoard(BoardAddReqDTO dto) {
		// 게시판 이름 중복 검사
		if (boardMapper.selectIsExistBoardName(dto.getBoardName())) {
			throw new DuplicatedBoardException();
		}

		BoardVO vo = BoardVO.insertOf(dto);
		boardMapper.insertBoard(vo);

		// 게시판 순서 업데이트
		updateBoardOrder(dto.getBoardOrder(), boardMapper.selectTotalBoardCount(), vo.getBoardId());

		// 카테고리 추가
		categoryService.addCategory(vo.getBoardId(), dto.getCategories());
	}

	// 관리자 : 게시판 조회
	@Transactional(readOnly = true)
	public PageResDTO<BoardListResDTO> findBoardListWithPaging(PageReqDTO dto) {
		int totalBoardCount = boardMapper.selectTotalBoardCount();
		PageDTO pageDTO = new PageDTO(dto);

		List<BoardListResDTO> boardList = boardMapper.selectBoardListWithPaging(pageDTO)
				.stream()
				.map(BoardListResDTO::of)
				.toList();

		return new PageResDTO<>(totalBoardCount, dto.getPage(), boardList);
	}

	// 관리자 : 게시판 수정
	@Transactional
	public void modifyBoard(long boardId, BoardEditReqDTO dto) {
		// 게시판 존재 검증
		if (!boardMapper.selectIsExistBoard(boardId)) {
			throw new BoardNotFoundException();
		}

		// 게시판 업데이트
		BoardVO vo = BoardVO.updateOf(dto);
		boardMapper.updateBoard(vo, boardId);

		// 게시판 순서 업데이트
		updateBoardOrderModify(boardId, dto);

		// 카테고리 수정
		categoryService.modifyCategory(boardId, dto.getAddedCategoryList(), dto.getRemovedCategoryIds());
	}

	// 게시판 삭제
	@Transactional
	public void removeBoard(long boardId) {
		BoardVO boardDetail = boardMapper.selectBoardDetailById(boardId)
				.orElseThrow(BoardNotFoundException::new);

		// 게시판 삭제
		boardMapper.deleteBoard(boardId);

		// 게시판 순서 조정
		updateBoardOrder(boardMapper.selectTotalBoardCount(), boardDetail.getBoardOrder(), boardId);

		// 카테고리 삭제
		categoryService.removeCategoryOnBoardRemoval(boardId);
	}

	/**
	 * 게시판 순서 업데이트(수정)
	 *
	 * @param boardId 수정하려는 게시판 ID
	 * @param dto     게시판 수정 입력 값
	 */
	private void updateBoardOrderModify(long boardId, BoardEditReqDTO dto) {
		int newOrder = dto.getBoardOrder();
		int currentOrder = boardMapper.selectBoardOrder(boardId);

		if (newOrder < currentOrder) {
			boardMapper.shiftBoardOrderForward(newOrder, currentOrder, boardId);
		} else if (newOrder > currentOrder) {
			boardMapper.shiftBoardOrderBackward(newOrder, currentOrder, boardId);
		}
	}

	/**
	 * 게시판 순서 업데이트(추가, 삭제)
	 *
	 * @param order   바꾸려는 순서
	 * @param boardId 추가, 삭제당하는 게시판 ID
	 */
	private void updateBoardOrder(int order, int end, long boardId) {
		if (order > 0) {
			boardMapper.shiftBoardOrderForward(order, end, boardId);
		}
	}
}
