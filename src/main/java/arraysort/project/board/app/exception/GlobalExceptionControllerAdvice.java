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
