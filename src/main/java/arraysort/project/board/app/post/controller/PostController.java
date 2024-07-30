package arraysort.project.board.app.post.controller;

import arraysort.project.board.app.post.domain.PostAddDTO;
import arraysort.project.board.app.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute("message", "게시글이 추가 되었습니다.");
        model.addAttribute("request", "ADD_POST");
        return "common/alert";
    }
}

