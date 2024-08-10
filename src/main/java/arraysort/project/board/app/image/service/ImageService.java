package arraysort.project.board.app.image.service;

import arraysort.project.board.app.component.ImageComponent;
import arraysort.project.board.app.exception.ThumbnailImageNotFoundException;
import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.domain.PostImageVO;
import arraysort.project.board.app.image.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final ImageComponent imageComponent;

	private final ImageMapper imageMapper;

	// 이미지 추가
	@Transactional
	public void addImages(List<MultipartFile> images, long postId) {
		List<ImageVO> imageVOList = imageComponent.uploadImages(images)
				.stream()
				.map(ImageVO::insertOf)
				.toList();

		if (imageVOList.isEmpty()) {
			return;
		}

		// 이미지가 추가 되었을 때만 처리
		for (ImageVO image : imageVOList) {
			imageMapper.insertImage(image);
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

	// 게시글 아이디로 이미지 조회
	@Transactional(readOnly = true)
	public List<ImageVO> findImagesByPostId(long postId) {
		return imageMapper.selectImagesByPostId(postId);
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

	// 게시글에 존재하는 이미지 개수 조회
	@Transactional(readOnly = true)
	public int findImageCountByPostId(long postId) {
		return imageMapper.selectImageCountByPostId(postId);
	}
}
