package arraysort.project.board.app.board.domain;

import arraysort.project.board.app.common.enums.BoardType;
import arraysort.project.board.app.common.enums.Flag;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class BoardVO {

	private Long boardId;    // 게시판 ID

	private String boardName;    // 게시판 이름

	private BoardType boardType;    // 게시판 타입 [GENERAL, GALLERY]

	private int boardOrder;    // 게시판 순서

	private Flag imageFlag;    // 첨부이미지 사용 여부

	private Integer imageLimit;    // 첨부이미지 최대 개수

	private int noticeCount;    // 공지글 개수

	private Flag commentFlag;    // 댓글 사용 여부

	private int accessLevel;    // 게시판 접근 등급

	private Flag activateFlag;    // 활성화 여부

	private String createdBy;    // 최초 생성자

	private String updatedBy;    // 최종 수정자

	private Date createdAt;    // 최초 생성 시간

	private Date updatedAt;    // 최종 수정 시간

	private Flag deleteFlag;    // 삭제 여부

}
