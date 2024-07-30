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

    // 게시물 세부 내용 확인 예외, 게시물 수정 시 잘못된 접근(DB 존재여부) 예외
    @ExceptionHandler({DetailNotFoundException.class, IdNotFoundException.class})
    public ModelAndView handleNotFoundException() {
        return getModelAndView("해당 게시물을 찾을 수 없습니다.");
    }

    // 인가된 사용자 검증 예외 : Spring Security
    @ExceptionHandler(InvalidPrincipalException.class)
    public ModelAndView handleInvalidPrincipalException() {
        return getModelAndView("올바르지 않은 접근입니다.");
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
