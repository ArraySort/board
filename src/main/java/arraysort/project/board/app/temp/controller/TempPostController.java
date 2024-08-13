package arraysort.project.board.app.temp.controller;

import arraysort.project.board.app.board.service.BoardService;
import arraysort.project.board.app.category.service.CategoryService;
import arraysort.project.board.app.image.service.ImageService;
import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.post.service.PostService;
import arraysort.project.board.app.temp.domain.TempPostAddReqDTO;
import arraysort.project.board.app.temp.domain.TempPostEditReqDTO;
import arraysort.project.board.app.temp.domain.TempPostPublishReqDTO;
import arraysort.project.board.app.temp.service.TempPostService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/{boardId}/post")
@RequiredArgsConstructor
public class TempPostController {

	private final TempPostService tempPostService;

	private final BoardService boardService;

	private final PostService postService;

	private final CategoryService categoryService;

	private final ImageService imageService;

	// 임시저장 페이지 이동, 임시저장 게시글 리스트 조회
	@GetMapping("/temp")
	public String showBoardPage(@PathVariable long boardId, @ModelAttribute("page") PageReqDTO dto, Model model) {
		model.addAttribute("pagination", tempPostService.findTempPostListWithPaging(dto, boardId));
		model.addAttribute("boardDetail", boardService.findBoardDetailById(boardId));
		return "post/tempPost";
	}

	// 게시글 임시저장 요청
	@PostMapping("/process-save-temp-post")
	public String processAddTempPost(@PathVariable long boardId, @Valid @ModelAttribute TempPostAddReqDTO dto, Model model) {
		tempPostService.addTempPost(dto, boardId);

		ControllerUtil.addMessageAndRequest(model, "게시글이 임시저장되었습니다.", "ADD_TEMP");
		return "common/alert";
	}

	// 임시저장 게시글 수정 페이지 이동
	@GetMapping("/temp/{tempPostId}/edit")
	public String showTempPostEditPage(@PathVariable long boardId, @PathVariable long tempPostId, @ModelAttribute("page") PageReqDTO dto, Model model) {
		postService.validateAddByUserLevel();

		model.addAttribute("boardDetail", boardService.findBoardDetailById(boardId));
		model.addAttribute("postDetail", tempPostService.findTempPostDetailByPostId(boardId, tempPostId));
		model.addAttribute("categories", categoryService.findCategoryList(boardId));
		model.addAttribute("images", imageService.findImagesByTempPostId(tempPostId));
		return "post/editTempPost";
	}

	// 임시저장 게시글 수정
	@PostMapping("/temp/{tempPostId}/save")
	public String processEditTempPost(@PathVariable long boardId, @PathVariable long tempPostId, @Valid @ModelAttribute TempPostEditReqDTO dto, Model model) {
		tempPostService.modifyTempPost(dto, boardId, tempPostId);

		ControllerUtil.addMessageAndRequest(model, "임시저장 게시물이 저장되었습니다.", "MODIFY_TEMP");
		return "common/alert";
	}

	// 임시저장 게시글 게시 요청
	@PostMapping("/temp/{tempPostId}/publish")
	public String processPublishTempPost(@PathVariable long boardId, @PathVariable long tempPostId, @Valid @ModelAttribute TempPostPublishReqDTO dto, Model model) {
		tempPostService.publishTempPost(dto, boardId, tempPostId);

		ControllerUtil.addMessageAndRequest(model, "임시저장 게시물이 게시 되었습니다.", "PUBLISH_POST");
		return "common/alert";
	}

	// 임시저장 게시글 삭제 요청
	@PostMapping("/temp/{tempPostId}/delete")
	public String processRemovePost(@PathVariable long boardId, @PathVariable long tempPostId, Model model) {
		tempPostService.removeTempPost(boardId, tempPostId);

		ControllerUtil.addMessageAndRequest(model, "임시저장 게시글이 삭제되었습니다.", "DELETE_TEMP");
		return "common/alert";
	}
}
