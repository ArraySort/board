package arraysort.project.board.app.category.service;

import arraysort.project.board.app.category.domain.CategoryVO;
import arraysort.project.board.app.category.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryMapper categoryMapper;

	// 카테고리 목록 조회
	public List<CategoryVO> findCategoryList(long boardId) {
		return categoryMapper.selectCategoryListByBoardId(boardId);
	}
}
