package arraysort.project.board.app.category.service;

import arraysort.project.board.app.category.domain.CategoryAddReqDTO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.component.AdminComponent;
import arraysort.project.board.app.exception.CategoryCountException;
import arraysort.project.board.app.exception.CategoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryMapper categoryMapper;

	private final AdminComponent adminComponent;

	// 카테고리 목록 조회
	@Transactional(readOnly = true)
	public List<CategoryVO> findCategoryList(long boardId) {
		return categoryMapper.selectCategoryListByBoardId(boardId);
	}

	// 카테고리 추가
	@Transactional
	public void addCategory(long boardId, List<String> categories) {
		// 관리자인지 검증
		adminComponent.validateAdmin();

		if (categories == null || categories.isEmpty()) {
			throw new CategoryNotFoundException();
		}

		for (String categoryName : categories) {
			CategoryAddReqDTO dto = CategoryAddReqDTO.builder()
					.boardId(boardId)
					.categoryName(categoryName)
					.build();

			CategoryVO vo = CategoryVO.insertOf(dto);
			categoryMapper.insertCategory(vo);
		}
	}

	// 카테고리 수정
	@Transactional
	public void modifyCategory(long boardId, List<String> addedCategoryList, List<Long> removedCategoryIds) {
		// 수정되는 카테고리 개수 검증
		validateCategoryCount(boardId, addedCategoryList, removedCategoryIds);

		if (!addedCategoryList.isEmpty()) {
			for (String categoryName : addedCategoryList) {
				CategoryAddReqDTO dto = CategoryAddReqDTO.builder()
						.boardId(boardId)
						.categoryName(categoryName)
						.build();

				CategoryVO vo = CategoryVO.insertOf(dto);
				categoryMapper.insertCategory(vo);
			}
		}

		// 기존 카테고리 삭제
		if (!removedCategoryIds.isEmpty()) {
			categoryMapper.deleteCategories(removedCategoryIds);
		}
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
		boolean categoryCountCheck = categoryCount + addedCategoryList.size() - removedCategoryIds.size() < 1;

		if (!categoryCountCheck) {
			throw new CategoryCountException();
		}
	}
}
