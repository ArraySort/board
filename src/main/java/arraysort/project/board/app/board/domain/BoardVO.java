package arraysort.project.board.app.board.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class BoardVO {

	private Long boardId;    // 게시판 ID

	private String boardName;    // 게시판 이름

	private String boardType;    // 게시판 타입 [GENERAL, GALLERY]

	private Integer boardOrder;    // 게시판 순서

	private String imageFlag;    // 첨부이미지 사용 여부

	private Integer imageLimit;    // 첨부이미지 최대 개수

	private Integer noticeCount;    // 공지글 개수

	private String commentFlag;    // 댓글 사용 여부

	private Integer accessLevel;    // 게시판 접근 등급

	private String activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 최초 생성 시간

	private Date updatedAt;    // 최종 수정 시간

	private String deleteFlag;    // 삭제 여부

}
