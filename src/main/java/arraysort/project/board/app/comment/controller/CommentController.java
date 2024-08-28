package arraysort.project.board.app.comment.controller;

import arraysort.project.board.app.comment.domain.CommentAddReqDTO;
import arraysort.project.board.app.comment.domain.CommentAdoptReqDTO;
import arraysort.project.board.app.comment.domain.CommentEditReqDTO;
import arraysort.project.board.app.comment.service.CommentService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static arraysort.project.board.app.common.Constants.*;

@Controller
@RequestMapping("/{boardId}/post/detail/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	// 댓글 추가 요청
	@PostMapping("/add")
	public String processAddComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentAddReqDTO dto, Model model) {
		commentService.addComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 추가 되었습니다.", MAV_REQUEST_ADD_COMMENT);
		return MAV_ALERT;
	}

	// 댓글 수정 요청
	@PostMapping("/edit")
	public String processEditComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentEditReqDTO dto, Model model) {
		commentService.modifyComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 수정 되었습니다.", MAV_REQUEST_MODIFY_COMMENT);
		return MAV_ALERT;
	}

	// 댓글 삭제 요청
	@PostMapping("/delete")
	public String processRemoveComment(@PathVariable long boardId, @PathVariable long postId, @RequestParam long commentId, Model model) {
		commentService.removeComment(boardId, postId, commentId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 삭제 되었습니다.", MAV_REQUEST_DELETE_COMMENT);
		return MAV_ALERT;
	}

	// 댓글 채택 요청
	@PostMapping("/adopt")
	public String processAdoptComment(@PathVariable long boardId, @PathVariable long postId, @Valid @ModelAttribute CommentAdoptReqDTO dto, Model model) {
		commentService.adoptComment(dto, boardId, postId);

		ControllerUtil.addMessageAndRequest(model, "댓글이 채택 되었습니다.", MAV_REQUEST_ADOPT_COMMENT);
		return MAV_ALERT;
	}
}
