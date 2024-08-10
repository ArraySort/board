package arraysort.project.board.app.component;

import arraysort.project.board.app.exception.ImageUploadException;
import arraysort.project.board.app.image.domain.ImageMetaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ImageComponent {

	@Value("${file.upload-path}")
	private String uploadPath;        // 이미지 업로드(저장) 경로

	@Value("${file.allowed-extensions}")
	private String allowedExtensions;        // 허용된 확장자

	/**
	 * 이미지 다중 업로드
	 * 이미지가 선택되지 않으면 빈 리스트 반환
	 *
	 * @param multipartFiles 업로드 하려는 이미지 파일들
	 * @return ImageMetaData : originalName(원본이름), savedName(저장된 이름), imagePath(저장경로), size(크기)
	 */
	public List<ImageMetaData> uploadImages(List<MultipartFile> multipartFiles) {

		if (multipartFiles == null) {
			throw new ImageUploadException("이미지 업로드 에러 발생");
		}

		List<ImageMetaData> images = new ArrayList<>();

		for (MultipartFile multipartFile : multipartFiles) {
			if (!multipartFile.isEmpty()) {
				images.add(uploadFile(multipartFile));
			}
		}

		return images;
	}

	/**
	 * 이미지 기본 정보 저장
	 *
	 * @param multipartFile 업로드 하려는 이미지
	 * @return 이미지 기본 정보
	 */
	private ImageMetaData uploadFile(MultipartFile multipartFile) {
		String savedName = generateSavedFileName(multipartFile.getOriginalFilename());
		String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
		Path uploadImage = makeDirectories(Paths.get(uploadPath, today)).resolve(savedName);

		try {
			multipartFile.transferTo(uploadImage);
		} catch (IOException e) {
			log.error("[이미지 업로드 에러 : {} ]", multipartFile.getOriginalFilename(), e);
			throw new ImageUploadException("이미지 업로드 에러 발생");
		}

		return ImageMetaData.builder()
				.originalName(multipartFile.getOriginalFilename())
				.savedName(savedName)
				.imagePath(String.valueOf(uploadImage))
				.size(multipartFile.getSize())
				.build();
	}

	/**
	 * 저장된 이미지 이름 생성(UUID 로 변경)
	 *
	 * @param originalName 업로드한 이미지 원본이름
	 * @return UUID 로 변경된 이미지 이름
	 */
	private String generateSavedFileName(final String originalName) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String extensions = StringUtils.getFilenameExtension(originalName);
		List<String> allowedEx = List.of(allowedExtensions.split(","));

		if (extensions == null || !allowedEx.contains(extensions.toLowerCase())) {
			throw new ImageUploadException("지원하지 않는 확장자입니다. 지원 방식 : " + allowedEx);
		}

		return uuid + "." + extensions;
	}

	/**
	 * 저장 경로에 새로운 디렉터리 생성
	 *
	 * @param path 업로드 경로
	 * @return 업로드 경로
	 */
	private Path makeDirectories(final Path path) {
		try {
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
		} catch (IOException e) {
			log.error("[디렉터리 생성 실패 : {}", path, e);
			throw new ImageUploadException("디렉터리 생성 실패");
		}

		return path;
	}
}
