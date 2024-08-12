package arraysort.project.board.app.temp.controller;

import arraysort.project.board.app.temp.domain.TempPostAddDTO;
import arraysort.project.board.app.temp.service.TempPostService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{boardId}/post")
@RequiredArgsConstructor
public class TempPostController {

	private final TempPostService tempPostService;

	// 게시글 임시저장 요청
	@PostMapping("/process-save-temp-post")
	public String processAddTempPost(@PathVariable long boardId, @Valid @ModelAttribute TempPostAddDTO dto, Model model) {
		tempPostService.addTempPost(dto, boardId);

		ControllerUtil.addMessageAndRequest(model, "게시글이 임시저장되었습니다.", "ADD_TEMP");
		return "common/alert";
	}
}
