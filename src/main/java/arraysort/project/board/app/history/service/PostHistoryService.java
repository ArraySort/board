package arraysort.project.board.app.history.service;


import arraysort.project.board.app.history.domain.PostHistoryVO;
import arraysort.project.board.app.history.mapper.PostHistoryMapper;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.PostVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostHistoryService {

	private final PostHistoryMapper postHistoryMapper;

	private final ImageService imageService;

	// 게시글 기록 추가
	@Transactional
	public void addPostHistory(PostVO postVO, String categoryName) {
		PostHistoryVO postHistoryVO = PostHistoryVO.of(postVO);
		postHistoryVO.updateCategoryName(categoryName);

		// 게시글 내용 기록 추가
		postHistoryMapper.insertPostHistory(postHistoryVO);

		List<Long> postImageIds = imageService.findImagesByPostId(postVO.getPostId())
				.stream()
				.map(ImageVO::getImageId)
				.toList();

		if (!postImageIds.isEmpty()) {
			postHistoryMapper.insertPostImageHistory(postHistoryVO.getPostHistoryId(), postImageIds);
		}
	}
}
