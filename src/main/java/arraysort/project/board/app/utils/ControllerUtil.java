package arraysort.project.board.app.utils;

import org.springframework.ui.Model;

public class ControllerUtil {

    private ControllerUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Controller 에서 Post 요청 시 완료 Alert, 요청을 진행한 페이지 주소를 Attribute 로 넣어주는 메서드
     *
     * @param model   Attribute 를 추가할 model
     * @param message Alert 로 전달할 message
     * @param request 요청을 보낸 페이지
     */
    public static void addMessageAndRequest(Model model, String message, String request) {
        model.addAttribute("message", message);
        model.addAttribute("request", request);
    }
}
