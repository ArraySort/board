package arraysort.project.board.app.image.service;

import arraysort.project.board.app.component.ImageComponent;
import arraysort.project.board.app.exception.ThumbnailImageNotFoundException;
import arraysort.project.board.app.image.domain.CommentImageVO;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.domain.PostImageVO;
import arraysort.project.board.app.image.domain.TempPostImageVO;
import arraysort.project.board.app.image.mapper.ImageMapper;
import arraysort.project.board.app.temp.domain.TempPostPublishReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageComponent imageComponent;

	private final ImageMapper imageMapper;

	// 게시글 이미지 추가
	@Transactional
	public void addImages(List<MultipartFile> images, long postId) {
		List<ImageVO> imageVOList = getImageVOList(images);

		if (imageVOList.isEmpty()) {
			return;
		}

		List<PostImageVO> postImageVOList = imageVOList
				.stream()
				.map(image -> PostImageVO.builder()
						.postId(postId)
						.imageId(image.getImageId())
						.build())
				.toList();

		imageMapper.insertPostImage(postImageVOList);
	}

	// 임시저장 이미지 추가
	@Transactional
	public void addTempImages(List<MultipartFile> images, long tempPostId) {
		List<ImageVO> imageVOList = getImageVOList(images);

		if (imageVOList.isEmpty()) {
			return;
		}

		List<TempPostImageVO> tempPostImageVOList = imageVOList
				.stream()
				.map(image -> TempPostImageVO.builder()
						.tempPostId(tempPostId)
						.imageId(image.getImageId())
						.build())
				.toList();

		imageMapper.insertTempPostImage(tempPostImageVOList);
	}

	// 게시글 이미지 추가
	@Transactional
	public void addCommentImages(List<MultipartFile> images, long commentId) {
		List<ImageVO> imageVOList = getImageVOList(images);

		if (imageVOList.isEmpty()) {
			return;
		}

		List<CommentImageVO> commentImageVOList = imageVOList
				.stream()
				.map(image -> CommentImageVO.builder()
						.commentId(commentId)
						.imageId(image.getImageId())
						.build())
				.toList();

		imageMapper.insertCommentImage(commentImageVOList);
	}

	// 임시저장 게시글에서 일반 게시글로 게시
	@Transactional
	public void publishTempImages(TempPostPublishReqDTO dto, long tempPostId, long postId) {
		List<ImageVO> postImages = new ArrayList<>();

		// 기존 임시 게시글 이미지 목록 추가
		postImages.addAll(imageMapper.selectImagesByTempPostId(tempPostId));

		// 새로 추가된 이미지 목록 추가
		postImages.addAll(getImageVOList(dto.getAddedImages()));

		// 삭제된 이미지 제거
		postImages.removeIf(image -> dto.getRemovedImageIds().contains(image.getImageId()));

		// 임시저장 게시글 이미지 관계 삭제, 임시저장 게시글 이미지 삭제(테이블)
		imageMapper.deleteTempPostImageByPostId(tempPostId);

		if (!dto.getRemovedImageIds().isEmpty()) {
			imageMapper.deleteTempImages(dto.getRemovedImageIds());
		}

		if (postImages.isEmpty()) {
			return;
		}

		List<PostImageVO> postImageVOList = postImages
				.stream()
				.map(image -> PostImageVO.builder()
						.postId(postId)
						.imageId(image.getImageId())
						.build())
				.toList();

		imageMapper.insertPostImage(postImageVOList);
	}

	// 썸네일 이미지 추가
	@Transactional
	public long addThumbnailImage(MultipartFile multipartFile) {
		if (multipartFile == null || multipartFile.isEmpty()) {
			throw new ThumbnailImageNotFoundException();
		}

		ImageVO thumbnailImage = ImageVO.insertOf(imageComponent.uploadThumbnailImage(multipartFile));
		imageMapper.insertImage(thumbnailImage);

		return thumbnailImage.getImageId();
	}

	// 썸네일 이미지 수정
	@Transactional
	public Long modifyThumbnailImage(MultipartFile multipartFile, long postId) {
		// 기존 이미지 삭제
		imageMapper.deleteThumbnailImageByPostId(postId);

		// 게시글 썸네일 이미지 ID 업데이트 -> 이미지 수정 시 null 값으로 변경
		imageMapper.updateThumbnailImageIdByPostId(postId);

		// 새로운 이미지 추가
		ImageVO thumbnailImage = ImageVO.insertOf(imageComponent.uploadThumbnailImage(multipartFile));
		imageMapper.insertImage(thumbnailImage);

		return thumbnailImage.getImageId();
	}

	// 게시글 삭제 시 썸네일 이미지 삭제
	@Transactional
	public void removeThumbnailImage(long postId) {
		imageMapper.deleteThumbnailImageByPostId(postId);
	}

	// 임시저장 게시글 게시 -> 썸네일 수정
	@Transactional
	public void removeTempThumbnailImage(long imageId) {
		imageMapper.deleteThumbnailImageByTempPostId(imageId);
	}

	// 게시글 아이디로 이미지 조회
	@Transactional(readOnly = true)
	public List<ImageVO> findImagesByPostId(long postId) {
		return imageMapper.selectImagesByPostId(postId);
	}

	// 임시저장 게시글 아이디로 이미지 조회
	@Transactional(readOnly = true)
	public List<ImageVO> findImagesByTempPostId(long tempPostId) {
		return imageMapper.selectImagesByTempPostId(tempPostId);
	}

	// 이미지 ID 로 이미지 조회
	@Transactional(readOnly = true)
	public ImageVO findImageById(long imageId) {
		return imageMapper.selectImageById(imageId).orElseThrow();
	}

	// 게시글 이미지 삭제
	@Transactional
	public void removeImages(List<Long> imageIds) {
		imageMapper.deleteImages(imageIds);
		imageMapper.deletePostImageByPostId(imageIds);
	}

	// 임시저장 게시글 이미지 삭제
	@Transactional
	public void removeTempImages(List<Long> imageIds, long tempPostId) {
		imageMapper.deleteTempPostImageByPostId(tempPostId);
		imageMapper.deleteTempImages(imageIds);
	}

	// 게시글에 존재하는 이미지 개수 조회
	@Transactional(readOnly = true)
	public int findImageCountByPostId(long postId) {
		return imageMapper.selectImageCountByPostId(postId);
	}

	// 임시저장 게시글에 존재하는 이미지 개수 조회
	@Transactional(readOnly = true)
	public int findTempImageCountByTempPostId(long tempPostId) {
		return imageMapper.selectTempImageCountByTempPostId(tempPostId);
	}

	/**
	 * 업로드된 이미지 리스트 생성
	 *
	 * @param images 업로드된 이미지들
	 * @return 업로드 된 이미지 리스트
	 */
	private List<ImageVO> getImageVOList(List<MultipartFile> images) {
		List<ImageVO> imageVOList = imageComponent.uploadImages(images)
				.stream()
				.map(ImageVO::insertOf)
				.toList();

		if (imageVOList.isEmpty()) {
			return Collections.emptyList();
		}

		imageVOList.forEach(imageMapper::insertImage);
		return imageVOList;
	}
}
