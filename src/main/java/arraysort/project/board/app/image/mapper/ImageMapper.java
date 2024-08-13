package arraysort.project.board.app.image.mapper;

import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.domain.PostImageVO;
import arraysort.project.board.app.image.domain.TempPostImageVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ImageMapper {

	// 이미지 추가
	void insertImage(ImageVO vo);

	// 게시글 이미지 관계 추가
	void insertPostImage(List<PostImageVO> postImages);

	// 임시저장 게시글 이미지 관계 추가
	void insertTempPostImage(List<TempPostImageVO> tempPostImages);

	// 게시글 세부조회 -> 이미지 리스트 조회
	List<ImageVO> selectImagesByPostId(long postId);

	//  임시저장 게시글 세부조회 -> 이미지 리스트 조회
	List<ImageVO> selectImagesByTempPostId(long tempPostId);

	// 게시글 세부조회 -> 이미지 조회
	Optional<ImageVO> selectImageById(long imageId);

	// 게시글 수정 -> 이미지 삭제
	void deleteImages(List<Long> imageIds);

	// 게시글 이미지 관계 삭제
	void deletePostImageByPostId(List<Long> imageIds);

	// 게시글 썸네일 이미지 삭제
	void deleteThumbnailImageByPostId(long postId);

	// 게시글 썸네일 이미지 ID 업데이트 -> 이미지 수정 시 null 값으로 변경
	void updateThumbnailImageIdByPostId(long postId);

	// 게시글에 존재하는 이미지 개수 조회
	int selectImageCountByPostId(long postId);

	// 임시저장 게시글 게시 -> 이미지 삭제
	void deleteTempImages(List<Long> imageIds);

	// 임시저장 게시글 이미지 관계 삭제
	void deleteTempPostImageByPostId(long tempPostId);

	// 임시저장 게시글 게시 -> 썸네일 이미지 변경
	void deleteThumbnailImageByTempPostId(long imageId);

	// 임시저장 게시글에 존재하는 이미지 개수 조회
	int selectTempImageCountByTempPostId(long tempPostId);
}
