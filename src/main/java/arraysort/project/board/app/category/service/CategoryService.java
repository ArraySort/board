package arraysort.project.board.app.category.service;

import arraysort.project.board.app.category.domain.CategoryAddReqDTO;
import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import arraysort.project.board.app.component.AdminComponent;
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
}
