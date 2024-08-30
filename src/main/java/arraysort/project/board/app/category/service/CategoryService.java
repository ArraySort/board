package arraysort.project.board.app.category.service;

import arraysort.project.board.app.category.domain.CategoryAddReqDTO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.exception.CategoryCountException;
import arraysort.project.board.app.exception.CategoryNotFoundException;
import arraysort.project.board.app.exception.InvalidPrincipalException;
import arraysort.project.board.app.post.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryMapper categoryMapper;

	private final PostMapper postMapper;

	// 카테고리 목록 조회
	@Transactional(readOnly = true)
	public List<CategoryVO> findCategoryList(long boardId) {
		return categoryMapper.selectCategoryListByBoardId(boardId);
	}

	// 카테고리 추가
	@Transactional
	public void addCategory(long boardId, List<String> categories) {
		if (categories == null || categories.isEmpty()) {
			throw new CategoryNotFoundException();
		}

		List<CategoryVO> categoryList = getCategoryList(boardId, categories);

		categoryMapper.insertCategoryList(categoryList);
	}

	// 카테고리 수정
	@Transactional
	public void modifyCategory(long boardId, List<String> addedCategoryList, List<Long> removedCategoryIds) {
		// 수정되는 카테고리 개수 검증
		validateCategoryCount(boardId, addedCategoryList, removedCategoryIds);

		// 삭제되는 카테고리 사용중인지 검증
		validateCategoryInUse(removedCategoryIds);

		if (!addedCategoryList.isEmpty()) {
			List<CategoryVO> categoryList = getCategoryList(boardId, addedCategoryList);

			categoryMapper.insertCategoryList(categoryList);
		}

		// 기존 카테고리 삭제
		if (!removedCategoryIds.isEmpty()) {
			categoryMapper.deleteCategories(removedCategoryIds);
		}
	}

	// 게시판 삭제 시 카테고리 삭제
	@Transactional
	public void removeCategoryOnBoardRemoval(long boardId) {
		categoryMapper.deleteCategoriesByBoardId(boardId);
	}

	/**
	 * 추가 카테고리 리스트 반환
	 *
	 * @param boardId    게시판 ID
	 * @param categories 추가된 카테고리 리스트
	 * @return 추가된 카테고리 VO 리스트
	 */
	private List<CategoryVO> getCategoryList(long boardId, List<String> categories) {
		Set<String> uniqueCategories = new HashSet<>(categories);
		return uniqueCategories.stream()
				.map(categoryName -> {
					CategoryAddReqDTO dto = CategoryAddReqDTO.builder()
							.boardId(boardId)
							.categoryName(categoryName)
							.build();
					return CategoryVO.insertOf(dto);
				}).toList();
	}

	/**
	 * 게시판 수정 시 카테고리 개수 검증
	 * 카테고리 개수는 1개 이상이어야 함
	 *
	 * @param boardId            카테고리가 수정되는 게시판 ID
	 * @param addedCategoryList  추가된 카테고리 리스트
	 * @param removedCategoryIds 삭제된 케터고리 ID 리스트
	 */
	private void validateCategoryCount(long boardId, List<String> addedCategoryList, List<Long> removedCategoryIds) {
		int categoryCount = categoryMapper.selectCategoryCountByBoardId(boardId);
		boolean isInvalidCategoryCount = categoryCount + addedCategoryList.size() - removedCategoryIds.size() < 1;

		if (isInvalidCategoryCount) {
			throw new CategoryCountException();
		}
	}

	/**
	 * 해당 게시판에서 이미 사용중인 카테고리 검증
	 * 게시판에서 삭제하려는 카테고리가 존재하면 삭제 불가
	 *
	 * @param removedCategoryIds 삭제하려는 카테고리 ID
	 */
	private void validateCategoryInUse(List<Long> removedCategoryIds) {
		for (Long categoryId : removedCategoryIds) {
			if (postMapper.selectIsCategoryInUse(categoryId)) {
				throw new InvalidPrincipalException("카테고리가 게시글에서 사용 중이므로 삭제할 수 없습니다.");
			}
		}
	}
}
