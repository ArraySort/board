package arraysort.project.board.app.admin.mapper;

import arraysort.project.board.app.admin.domain.AdminVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface AdminMapper {

	// 관리자 추가
	void insertAdmin(AdminVO vo);

	// 관리자 조회
	Optional<AdminVO> selectAdminByAdminId(String adminId);
}
