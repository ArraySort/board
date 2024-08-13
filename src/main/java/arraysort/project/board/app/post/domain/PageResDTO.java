package arraysort.project.board.app.post.domain;

import arraysort.project.board.app.common.Constants;
import arraysort.project.board.app.common.NumberAssignable;
import lombok.Getter;

import java.util.List;

@Getter
public class PageResDTO<T> {

	private final long totalPostCount;                   // 총 게시물 개수

	private final int currentPage;                      // 현재 페이지

	private final List<T> postList;           // 페이지에 보여줄 게시물 리스트

	private int startBlockPage = 1;                     // 페이지 블럭의 시작 페이지

	private int endBlockPage;                           // 페이지 블럭의 마지막 페이지

	private int totalPageCount;                         // 총 페이지 개수

	private boolean isPrev = false;                     // 다음 페이지 버튼 유무

	private boolean isNext = false;                     // 이전 페이지 버튼 유무

	public PageResDTO(int totalPostCount, int currentPage, List<T> postList) {
		this.totalPostCount = totalPostCount == 0 ? 1 : totalPostCount;
		this.currentPage = currentPage;
		this.postList = postList;
		setPage();
		setPostNumber();
	}


	/**
	 * 페이지 세팅
	 * 총 페이지, 시작/끝 블록 페이지 계산, 이전/다음 버튼 유무 계산
	 */
	private void setPage() {
		// 총 페이지 개수 계산
		totalPageCount = (int) Math.ceil(totalPostCount * 1.0 / Constants.PAGE_ROW_COUNT);

		// 시작 블록 페이지 계산
		startBlockPage = startBlockPage + (((currentPage - startBlockPage) / Constants.PAGE_BLOCK_COUNT) * Constants.PAGE_BLOCK_COUNT);

		// 마지막 블록 페이지 계산
		endBlockPage = (Math.min((startBlockPage - 1) + Constants.PAGE_BLOCK_COUNT, totalPageCount));

		// 이전 버튼 유무
		isPrev = ((currentPage * 1.0) / Constants.PAGE_BLOCK_COUNT) > 1;

		// 다음 버튼 유무
		isNext = endBlockPage < totalPageCount;
	}

	/**
	 * 게시글 번호 지정
	 */
	private void setPostNumber() {
		long number = totalPostCount - ((currentPage - 1L) * Constants.PAGE_ROW_COUNT);

		for (int i = 0; i < postList.size(); i++) {
			if (postList.get(i) instanceof NumberAssignable assignable) {
				assignable.updateNumber(number - i);
			}
		}
	}
}
