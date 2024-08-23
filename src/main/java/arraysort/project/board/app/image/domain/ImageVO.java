package arraysort.project.board.app.image.domain;

import arraysort.project.board.app.common.enums.Flag;
import arraysort.project.board.app.utils.UserUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageVO {

	private Long imageId;       // 이미지 ID

	private String originalName;        // 원본 이미지 이름

	private String savedName;       // 저장된 이미지 이름

	private long size;      // 이미지 크기

	private String imagePath;      // 저장된 이미지 경로

	private String createdBy;        // 최초 생성자

	private Date createdAt;     // 이미지 생성 날짜

	private Flag deleteFlag;      // 삭제 여부

	// 이미지 추가
	public static ImageVO insertOf(ImageMetaData imageMetaData) {
		return ImageVO.builder()
				.originalName(imageMetaData.getOriginalName())
				.savedName(imageMetaData.getSavedName())
				.size(imageMetaData.getSize())
				.imagePath(imageMetaData.getImagePath())
				.createdBy(UserUtil.getCurrentLoginUserId())
				.build();
	}
}
