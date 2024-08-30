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
import arraysort.project.board.app.component.AdminComponent;
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

	private final AdminComponent adminComponent;

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
		// 관리자인지 검증
		adminComponent.validateAdmin();

		// 게시판 중복 검사
		if (boardMapper.selectIsExistBoardName(dto.getBoardName())) {
			throw new DuplicatedBoardException();
		}

		BoardVO vo = BoardVO.insertOf(dto);
		boardMapper.insertBoard(vo);

		// 카테고리 추가
		categoryService.addCategory(vo.getBoardId(), dto.getCategories());
	}

	// 관리자 : 게시판 조회
	@Transactional(readOnly = true)
	public PageResDTO<BoardListResDTO> findBoardListWithPaging(PageReqDTO dto) {
		// 관리자인지 검증
		adminComponent.validateAdmin();

		int totalBoardCount = boardMapper.selectTotalBoardCount();
		PageDTO pageDTO = new PageDTO(totalBoardCount, dto);

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
			return;
		}

		// 게시판 업데이트
		BoardVO vo = BoardVO.updateOf(dto);
		boardMapper.updateBoard(vo, boardId);
		
		// 카테고리 수정
		categoryService.modifyCategory(boardId, dto.getAddedCategoryList(), dto.getRemovedCategoryIds());
	}
}
