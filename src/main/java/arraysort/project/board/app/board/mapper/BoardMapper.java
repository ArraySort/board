package arraysort.project.board.app.board.mapper;

import arraysort.project.board.app.board.domain.BoardVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface BoardMapper {

	// 게시판 세부내용 조회
	Optional<BoardVO> selectBoardDetailById(long boardId);

	// 게시판 전체 조회
	List<BoardVO> selectAllBoards();
}
