package arraysort.project.board.app.image.mapper;

import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.domain.PostImageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ImageMapper {

	// 이미지 추가
	void insertImages(ImageVO vo);

	// 게시글 이미지 관계 추가
	void insertPostImage(List<PostImageVO> postImages);

	// 게시글 세부조회 -> 이미지 리스트 조회
	List<ImageVO> selectImagesByPostId(long postId);

	// 게시글 세부조회 -> 이미지 조회
	Optional<ImageVO> selectImageById(long imageId);

	// 게시글 수정 -> 이미지 삭제
	void deleteImages(List<Long> imageIds);
}
