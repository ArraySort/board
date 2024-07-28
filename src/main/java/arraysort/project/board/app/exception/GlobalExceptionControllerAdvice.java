package arraysort.project.board.app.exception;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionControllerAdvice {

    @ExceptionHandler(DuplicatedUserException.class)
    public ModelAndView handleDuplicatedUserException() {
        return getModelAndView("이미 가입한 아이디 입니다.");
    }

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
