package arraysort.project.board.app.image.controller;

import arraysort.project.board.app.image.domain.ImageVO;
import arraysort.project.board.app.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	// 게시글 세부조회에서 이미지 링크 클릭 시
	@GetMapping("/image/{imageId}")
	public ResponseEntity<byte[]> getImage(@PathVariable long imageId) {
		ImageVO vo = imageService.findImageById(imageId);

		try {
			byte[] imageBytes = Files.readAllBytes(Paths.get(vo.getImagePath()));
			return ResponseEntity.ok()
					.contentType(MediaType.IMAGE_PNG)
					.body(imageBytes);
		} catch (IOException e) {
			log.error("[ 이미지 업로드 에러 : ]", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
