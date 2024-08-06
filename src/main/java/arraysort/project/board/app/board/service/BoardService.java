package arraysort.project.board.app.board.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

	private final BoardMapper boardMapper;

	// 게시판 전체 조회
	public List<BoardVO> findAllBoards() {
		return boardMapper.selectAllBoards();
	}
}
