package arraysort.project.board.app.category.mapper;

import arraysort.project.board.app.category.domain.CategoryVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface CategoryMapper {

	// 카테고리 세부내용 조회
	Optional<CategoryVO> selectCategoryDetailById(long categoryId);
}
