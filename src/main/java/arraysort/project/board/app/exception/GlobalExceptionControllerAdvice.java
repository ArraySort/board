package arraysort.project.board.app.exception;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionControllerAdvice {

	// 회원가입 시 아이디 중복 예외
	@ExceptionHandler(DuplicatedUserException.class)
	public ModelAndView handleDuplicatedUserException(DuplicatedUserException e) {
		return getModelAndView(e.getMessage());
	}

	// 회원가입 시 비밀번호 확인 예외
	@ExceptionHandler(PasswordCheckException.class)
	public ModelAndView handlePasswordCheckException() {
		return getModelAndView("입력한 비밀번호와 확인 값이 일치하지 않습니다.");
	}

	// 회원가입 시 발생하는 이메일 예외
	@ExceptionHandler(EmailValidationException.class)
	public ModelAndView handleEmailValidationException(EmailValidationException e) {
		return getModelAndView(e.getMessage());
	}

	// 게시글 추가 시 잘못된 게시판 ID 접근
	@ExceptionHandler(BoardNotFoundException.class)
	public ModelAndView handleBoardNotFoundException() {
		return getModelAndView("해당 게시판을 찾을 수 없습니다.");
	}

	// 게시글 추가 시 잘못된 게시판 ID 접근
	@ExceptionHandler(CategoryNotFoundException.class)
	public ModelAndView handleCategoryNotFoundException() {
		return getModelAndView("해당 카테고리를 찾을 수 없습니다.");
	}

	// 게시글 이미지 첨부 시 게시판이 허용한 이미지 개수보다 많을 때
	@ExceptionHandler(BoardImageOutOfRangeException.class)
	public ModelAndView handleBoardImageOutOfRangeException(BoardImageOutOfRangeException e) {
		return getModelAndView(e.getMessage());
	}

	// 게시물 세부 내용 확인 예외, 게시물 수정 시 잘못된 접근(DB 존재여부) 예외
	@ExceptionHandler({DetailNotFoundException.class, IdNotFoundException.class})
	public ModelAndView handleNotFoundException() {
		return getModelAndView("해당 게시물을 찾을 수 없습니다.");
	}

	// 갤러리 게시판에 게시글 추가 시 썸네일 없을 때 발생되는 예외
	@ExceptionHandler(ThumbnailImageNotFoundException.class)
	public ModelAndView handleThumbnailImageNotFoundException() {
		return getModelAndView("갤러리 게시판에서 썸네일 이미지는 필수로 업로드 되어야 합니다.");
	}

	// 이미지 업로드 시 발생 예외
	@ExceptionHandler(ImageUploadException.class)
	public ModelAndView handleImageUploadException(ImageUploadException e) {
		return getModelAndView(e.getMessage());
	}

	// 관리자가 설정한 정책에 위반하는 예외
	@ExceptionHandler(InvalidPrincipalException.class)
	public ModelAndView handleInvalidPrincipalException(InvalidPrincipalException e) {
		return getModelAndView(e.getMessage());
	}

	// ModelAttribute 바인딩 시 Validation 예외
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		StringBuilder errorMessages = new StringBuilder();

		for (FieldError error : e.getBindingResult().getFieldErrors()) {
			errorMessages.append(error.getDefaultMessage());
		}

		return getModelAndView(String.valueOf(errorMessages));
	}

	private ModelAndView getModelAndView(String message) {
		ModelAndView mav = new ModelAndView("common/alert");
		mav.addObject("message", message);
		return mav;
	}
}
