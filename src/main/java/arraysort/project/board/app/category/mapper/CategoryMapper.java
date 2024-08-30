package arraysort.project.board.app.category.mapper;

import arraysort.project.board.app.category.domain.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {

	// 카테고리 세부내용 조회
	Optional<CategoryVO> selectCategoryDetailById(long categoryId);

	// 카테고리 목록 조회
	List<CategoryVO> selectCategoryListByBoardId(long boardId);

	// 관리자 : 카테고리 추가
	void insertCategoryList(List<CategoryVO> categories);

	// 관리자 : 카테고리 삭제
	void deleteCategories(List<Long> removedCategoryIds);

	// 카테고리 개수 조회
	int selectCategoryCountByBoardId(long boardId);
}
