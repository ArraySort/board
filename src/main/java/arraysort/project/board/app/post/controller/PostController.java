package arraysort.project.board.app.post.controller;

import arraysort.project.board.app.post.domain.PageReqDTO;
import arraysort.project.board.app.post.domain.PostAddReqDTO;
import arraysort.project.board.app.post.domain.PostEditReqDTO;
import arraysort.project.board.app.post.service.PostService;
import arraysort.project.board.app.utils.ControllerUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 보드 페이지 이동, 게시글 리스트 조회
    @GetMapping
    public String showBoardPage(@ModelAttribute("page") PageReqDTO dto, Model model) {
        model.addAttribute("pagination", postService.findPostListWithPaging(dto));
        return "post/post";
    }

    // 게시글 추가 페이지 이동
    @GetMapping("/add")
    public String showAddPostPage() {
        return "post/addPost";
    }

    // 게시글 추가 요청
    @PostMapping("/process-add-post")
    public String processAddPost(@Valid @ModelAttribute PostAddReqDTO dto, Model model) {
        postService.addPost(dto);

        ControllerUtil.addMessageAndRequest(model, "게시글이 추가되었습니다.", "ADD_POST");
        return "common/alert";
    }

    // 게시글 세부 페이지 이동
    @GetMapping("/detail/{postId}")
    public String showPostDetailPage(@PathVariable long postId, @ModelAttribute("page") PageReqDTO dto, Model model) {
        model.addAttribute("postDetail", postService.findPostDetailByPostId(postId));
        return "post/detailPost";
    }

    // 게시글 수정 페이지 이동
    @GetMapping("/detail/{postId}/edit")
    public String showPostEditPage(@PathVariable long postId, Model model) {
        model.addAttribute("postDetail", postService.findPostDetailByPostId(postId));
        return "post/editPost";
    }

    // 게시글 수정 요청
    @PostMapping("/detail/{postId}/edit")
    public String processModifyPost(@PathVariable long postId, @Valid @ModelAttribute PostEditReqDTO dto, Model model) {
        postService.modifyPost(dto, postId);

        ControllerUtil.addMessageAndRequest(model, "게시글이 수정되었습니다.", "MODIFY_POST");
        model.addAttribute("postId", postId);
        return "common/alert";
    }

    // 게시글 삭제 요청
    @PostMapping("/detail/{postId}/delete")
    public String processRemovePost(@PathVariable long postId, Model model) {
        postService.removePost(postId);

        ControllerUtil.addMessageAndRequest(model, "게시글이 삭제되었습니다.", "DELETE_POST");
        return "common/alert";
    }
}

