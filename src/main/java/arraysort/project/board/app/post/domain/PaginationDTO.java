package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.Constants;
import lombok.Getter;

import java.util.List;

@Getter
public class PaginationDTO {

    private final int totalPostCount;                   // 총 게시물 개수

    private final int currentPage;                      // 현재 페이지

    private final List<PostListDTO> postList;           // 페이지에 보여줄 게시물 리스트

    private int startBlockPage = 1;                     // 페이지 블럭의 시작 페이지
    private int endBlockPage;                           // 페이지 블럭의 마지막 페이지
    private int totalPageCount;                         // 총 페이지 개수
    private boolean isPrev = false;                     // 다음 페이지 버튼 유무
    private boolean isNext = false;                     // 이전 페이지 버튼 유무

    public PaginationDTO(int totalPostCount, int currentPage, List<PostListDTO> postList) {
        this.totalPostCount = totalPostCount == 0 ? 1 : totalPostCount;
        this.currentPage = currentPage;
        this.postList = postList;
        setPage();
    }

    // 페이지 세팅
    private void setPage() {
        totalPageCount = (int) Math.ceil(totalPostCount * 1.0 / Constants.PAGE_ROW_COUNT);
        startBlockPage = startBlockPage + (((currentPage - startBlockPage) / Constants.PAGE_BLOCK_COUNT) * Constants.PAGE_BLOCK_COUNT);
        endBlockPage = (Math.min((startBlockPage - 1) + Constants.PAGE_BLOCK_COUNT, totalPageCount));
        isPrev = ((currentPage * 1.0) / Constants.PAGE_BLOCK_COUNT) > 1;
        isNext = endBlockPage < totalPageCount;
    }
}
