package arraysort.project.board.app.temp.service;

import arraysort.project.board.app.board.domain.BoardVO;
import arraysort.project.board.app.component.PostComponent;
import arraysort.project.board.app.exception.BoardImageOutOfRangeException;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.temp.domain.TempPostAddDTO;
import arraysort.project.board.app.temp.domain.TempPostVO;
import arraysort.project.board.app.temp.mapper.TempPostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TempPostService {

	private final TempPostMapper tempPostMapper;

	private final ImageService imageService;

	private final PostComponent postComponent;

	// 임시저장 게시글 추가
	@Transactional
	public void addTempPost(TempPostAddDTO dto, long boardId) {
		TempPostVO vo = TempPostVO.insertOf(dto, boardId);
		BoardVO boardDetail = postComponent.getValidatedBoard(boardId);
		postComponent.getValidatedCategory(dto.getCategoryId(), boardDetail);

		// 갤러리 게시판일 때 썸네일 이미지 추가
		if (boardDetail.getBoardType().equals("GALLERY")) {
			vo.updateThumbnailImageId(imageService.addThumbnailImage(dto.getThumbnailImage()));
		}

		// 게시글 추가
		tempPostMapper.insertTempPost(vo);

		// 게시글 이미지 업로드
		handleTempPostImages(dto, boardDetail, vo.getTempPostId());
	}

	/**
	 * [임시저장 게시글 이미지 처리(추가)]
	 * 게시판의 이미지 허용 여부가 Y 일 때만 실행
	 * 게시판의 이미지 허용 여부가 Y 일 때 최대 업로드 가용 이미지 검증
	 *
	 * @param dto         추가되는 게시글 정보(사용자 입력)
	 * @param boardDetail 검증된 게시판 세부정보
	 * @param postId      게시글 ID
	 */
	private void handleTempPostImages(TempPostAddDTO dto, BoardVO boardDetail, long postId) {
		if (boardDetail.getImageFlag().equals("N")) {
			return;
		}

		if (dto.getImages().size() > boardDetail.getImageLimit()) {
			throw new BoardImageOutOfRangeException("해당 게시판은 최대 " + boardDetail.getImageLimit() + " 개 까지 업로드 가능합니다.");
		}

		imageService.addTempImages(dto.getImages(), postId);
	}
}
