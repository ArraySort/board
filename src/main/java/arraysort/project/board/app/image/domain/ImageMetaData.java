package arraysort.project.board.app.image.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageMetaData {

	private String originalName;        // 이미지 원본 이름

	private String savedName;       // 저장된 이미지 이름

	private String imagePath;      // 저장된 이미지 경로

	private long size;      // 저장된 이미지 크기

}

