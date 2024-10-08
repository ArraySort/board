package arraysort.project.board.app.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import static arraysort.project.board.app.common.Constants.*;

@Slf4j
@ControllerAdvice
public class GlobalExceptionControllerAdvice {

	// 회원가입 시 아이디 중복 예외
	@ExceptionHandler(DuplicatedUserException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ModelAndView handleDuplicatedUserException(DuplicatedUserException e) {
		return getModelAndView(e.getMessage());
	}

	// 회원가입 시 비밀번호 확인 예외
	@ExceptionHandler(PasswordCheckException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handlePasswordCheckException() {
		return getModelAndView("입력한 비밀번호와 확인 값이 일치하지 않습니다.");
	}

	// 회원가입 시 발생하는 이메일 예외
	@ExceptionHandler(EmailValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleEmailValidationException(EmailValidationException e) {
		return getModelAndView(e.getMessage());
	}

	// 게시글 추가 시 잘못된 게시판 ID 접근
	@ExceptionHandler(BoardNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleBoardNotFoundException() {
		log.error("[404 에러 발생 : {} ]", "해당 게시판을 찾을 수 없습니다.");
		return MAV_ERROR_404;
	}

	// 게시글 추가 시 잘못된 게시판 ID 접근
	@ExceptionHandler(CategoryNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleCategoryNotFoundException() {
		log.error("[404 에러 발생 : {} ]", "해당 카테고리를 찾을 수 없습니다.");
		return MAV_ERROR_404;
	}

	// 게시글 이미지 첨부 시 게시판이 허용한 이미지 개수보다 많을 때
	@ExceptionHandler(BoardImageOutOfRangeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleBoardImageOutOfRangeException(BoardImageOutOfRangeException e) {
		return getModelAndView(e.getMessage());
	}

	// 게시물 세부 내용 확인 예외, 게시물 수정 시 잘못된 접근(DB 존재여부) 예외
	@ExceptionHandler({DetailNotFoundException.class, IdNotFoundException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFoundException() {
		log.error("[404 에러 발생 : {} ]", "해당 게시물을 찾을 수 없습니다.");
		return MAV_ERROR_404;
	}

	// 갤러리 게시판에 게시글 추가 시 썸네일 없을 때 발생되는 예외
	@ExceptionHandler(ThumbnailImageNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleThumbnailImageNotFoundException() {
		return getModelAndView("갤러리 게시판에서 썸네일 이미지는 필수로 업로드 되어야 합니다.");
	}

	// 댓글 수정, 삭제 시 잘못된 댓글 ID 접근
	@ExceptionHandler(CommentNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleCommentNotFoundException() {
		log.error("[404 에러 발생 : {} ]", "해당 댓글을 찾을 수 없습니다.");
		return MAV_ERROR_404;
	}

	// 이미지 업로드 시 발생 예외
	@ExceptionHandler(ImageUploadException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleImageUploadException(ImageUploadException e) {
		log.error("[이미지 업로드 에러 발생 : {}]", e.getMessage(), e);
		return "error/code500";
	}

	// 관리자가 설정한 정책에 위반하는 예외
	@ExceptionHandler(InvalidPrincipalException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleInvalidPrincipalException(InvalidPrincipalException e) {
		return getModelAndView(e.getMessage());
	}

	// 등록된 관리자 아이디가 없을 때
	@ExceptionHandler(AdminIdNotFoundException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ModelAndView handleAdminIdNotFoundException() {
		return getModelAndView("등록된 관리자 아이디를 찾을 수 없습니다.");
	}

	// 관리자 로그인 시 비밀번호 불일치
	@ExceptionHandler(AdminPasswordNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleAdminPasswordNotFoundException() {
		return getModelAndView("비밀번호가 일치하지 않습니다.");
	}

	// 게시판 중복 예외
	@ExceptionHandler(DuplicatedBoardException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ModelAndView handleDuplicatedBoardException() {
		return getModelAndView("이미 존재하는 게시판입니다. 이름을 다시 지정해주세요.");
	}

	// 카테고리 입력 개수 미만
	@ExceptionHandler(CategoryCountException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleCategoryCountException() {
		return getModelAndView("카테고리는 1개 이상 입력되어야 합니다.");
	}

	// ModelAttribute 바인딩 시 Validation 예외
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		StringBuilder errorMessages = new StringBuilder();

		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			errorMessages.append(error.getDefaultMessage());
		}

		return getModelAndView(String.valueOf(errorMessages));
	}

	// 커스텀 Exception 이 아닌 경우
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleException(Exception e) {
		log.error("[서버 에러 발생 :]", e);
		return MAV_ERROR_500;
	}

	/**
	 * ModelAndView 생성 alert
	 *
	 * @param message 예외 메세지
	 * @return 예외메세지가 담긴 ModelAndView 객체
	 */
	private ModelAndView getModelAndView(String message) {
		ModelAndView mav = new ModelAndView(MAV_ALERT);
		mav.addObject("message", message);
		return mav;
	}
}
