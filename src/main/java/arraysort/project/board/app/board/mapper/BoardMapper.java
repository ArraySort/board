package arraysort.project.board.app.board.mapper;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.common.page.PageDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {

	// 게시판 세부내용 조회
	Optional<BoardVO> selectBoardDetailById(long boardId);

	// 게시판 전체 조회
	List<BoardVO> selectAllBoards();

	// 총 게시판 개수 조회
	int selectTotalBoardCount();

	// 관리자 : 게시판 추가
	void insertBoard(BoardVO vo);

	// 관리자 : 게시판 리스트 조회
	List<BoardVO> selectBoardListWithPaging(PageDTO pageDTO);

	// 관리자 : 게시판 존재 여부 조회
	boolean selectIsExistBoardName(String boardName);

	// 관리자 : 게시판 수정
	void updateBoard(BoardVO vo, long boardId);

	// 게시판 존재 여부 조회
	boolean selectIsExistBoard(long boardId);
}
