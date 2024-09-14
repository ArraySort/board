package arraysort.project.board.app.home.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.post.domain.PostListResDTO;
import arraysort.project.board.app.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HomeService {

	private final BoardService boardService;

	private final PostService postService;

	@Transactional(readOnly = true)
	public Map<String, List<PostListResDTO>> findRecentPostPerBoard() {
		Map<String, List<PostListResDTO>> recentPostsPerBoardMap = new HashMap<>();

		for (BoardVO board : boardService.findAllBoards()) {
			recentPostsPerBoardMap.put(board.getBoardName(),
					postService.findRecentPostsByBoardId(board.getBoardId(), 5));
		}
		return recentPostsPerBoardMap;
	}
}
