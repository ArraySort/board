package arraysort.project.board.app.post.controller;

import arraysort.project.board.app.post.domain.PostAddDTO;
import arraysort.project.board.app.post.domain.PostEditDTO;
import arraysort.project.board.app.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 보드 페이지 이동, 게시글 리스트 조회
    @GetMapping
    public String showBoardPage(Model model) {
        model.addAttribute("postLists", postService.findPostList());
        return "board/board";
    }

    // 게시글 추가 페이지 이동
    @GetMapping("/post/add")
    public String showAddPostPage() {
        return "board/addPost";
    }

    // 게시글 추가 요청
    @PostMapping("/post/process-add-post")
    public String processAddPost(@Valid @ModelAttribute PostAddDTO dto, Model model) {
        postService.addPost(dto);

        addMessageAndRequest(model, "게시글이 추가되었습니다.", "ADD_POST");
        return "common/alert";
    }

    // 게시글 세부 페이지 이동
    @GetMapping("/post/detail/{postId}")
    public String showPostDetailPage(@PathVariable long postId, Model model) {
        model.addAttribute("postDetail", postService.findPostDetailByPostId(postId));
        return "board/detailPost";
    }

    // 게시글 수정 페이지 이동
    @GetMapping("/post/detail/{postId}/edit")
    public String showPostEditPage(@PathVariable long postId, Model model) {
        model.addAttribute("postDetail", postService.findPostDetailByPostId(postId));
        return "board/editPost";
    }

    // 게시글 수정 요청
    @PostMapping("/post/detail/{postId}/edit")
    public String processModifyPost(@PathVariable long postId, @Valid @ModelAttribute PostEditDTO dto, Model model) {
        postService.modifyPost(dto, postId);

        addMessageAndRequest(model, "게시글이 수정되었습니다.", "MODIFY_POST");
        model.addAttribute("postId", postId);
        return "common/alert";
    }

    // 게시글 삭제 요청
    @PostMapping("/post/detail/{postId}/delete")
    public String processRemovePost(@PathVariable long postId, Model model) {
        postService.removePost(postId);

        addMessageAndRequest(model, "게시글이 삭제되었습니다.", "DELETE_POST");
        return "common/alert";
    }

    private void addMessageAndRequest(Model model, String message, String request) {
        model.addAttribute("message", message);
        model.addAttribute("request", request);
    }
}

