package arraysort.project.board.app.comment.controller;

import arraysort.project.board.app.comment.domain.CommentAddReqDTO;
import arraysort.project.board.app.comment.domain.CommentDeleteReqDTO;
import arraysort.project.board.app.comment.domain.CommentEditReqDTO;
import arraysort.project.board.app.comment.service.CommentService;
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
@RequestMapping("/{boardId}/post/detail/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	// 댓글 추가 요청
	@PostMapping("/add")
	public String processAddComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentAddReqDTO dto, Model model) {
		commentService.addComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 추가되었습니다.", "ADD_COMMENT");
		return "common/alert";
	}

	// 댓글 수정 요청
	@PostMapping("/edit")
	public String processEditComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentEditReqDTO dto, Model model) {
		commentService.modifyComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 수정 되었습니다.", "MODIFY_COMMENT");
		return "common/alert";
	}

	// 댓글 삭제 요청
	@PostMapping("/delete")
	public String processRemoveComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentDeleteReqDTO dto, Model model) {
		commentService.removeComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 삭제 되었습니다.", "DELETE_COMMENT");
		return "common/alert";
	}
}
