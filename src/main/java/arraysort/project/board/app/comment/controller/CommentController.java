package arraysort.project.board.app.comment.controller;

import arraysort.project.board.app.comment.domain.CommentAddReqDTO;
import arraysort.project.board.app.comment.service.CommentService;
import arraysort.project.board.app.utils.ControllerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{boardId}/post/detail/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/add")
	public String processAddComment(@PathVariable long boardId, @PathVariable long postId, @ModelAttribute CommentAddReqDTO dto, Model model) {
		commentService.addComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 추가되었습니다.", "ADD_COMMENT");
		return "common/alert";
	}
}
