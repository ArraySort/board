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
	private String uploadPath;

	@Value("${file.allowed-extensions}")
	private String allowedExtensions;

	public List<ImageMetaData> uploadImages(List<MultipartFile> multipartFiles) {

		List<ImageMetaData> images = new ArrayList<>();

		for (MultipartFile multipartFile : multipartFiles) {
			if (!multipartFile.isEmpty()) {
				images.add(uploadFile(multipartFile));
			}
		}

		return images;
	}

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

	private String generateSavedFileName(final String originalName) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String extensions = StringUtils.getFilenameExtension(originalName);
		List<String> allowedEx = List.of(allowedExtensions.split(","));

		if (extensions == null || !allowedEx.contains(extensions.toLowerCase())) {
			throw new ImageUploadException("지원하지 않는 확장자입니다. 지원 방식 : " + allowedEx);
		}

		return uuid + "." + extensions;
	}

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
