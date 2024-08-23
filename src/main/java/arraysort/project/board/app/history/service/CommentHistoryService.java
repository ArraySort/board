package arraysort.project.board.app.history.service;

import arraysort.project.board.app.comment.domain.CommentVO;
import arraysort.project.board.app.history.domain.CommentHistoryVO;
import arraysort.project.board.app.history.mapper.CommentHistoryMapper;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentHistoryService {

	private final CommentHistoryMapper commentHistoryMapper;

	private final ImageService imageService;

	// 댓글 기록 추가
	@Transactional
	public void addCommentHistory(CommentVO commentVO) {
		CommentHistoryVO commentHistoryVO = CommentHistoryVO.of(commentVO);

		commentHistoryMapper.insertCommentHistory(commentHistoryVO);

		List<Long> commentImageIds = imageService.findCommentImagesByCommentId(commentVO.getCommentId())
				.stream()
				.map(ImageVO::getImageId)
				.toList();

		if (!commentImageIds.isEmpty()) {
			commentHistoryMapper.insertCommentImageHistory(commentHistoryVO.getCommentHistoryId(), commentImageIds);
		}
	}
}
