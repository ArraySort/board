package arraysort.project.board.app.user.controller;


import arraysort.project.board.app.exception.EmailValidationException;
import arraysort.project.board.app.user.service.MailService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MailController {

	private final MailService mailService;

	// 입력된 사용자 이메일에 인증 번호 전송 요청
	@PostMapping("/user/send-email-verification")
	public ResponseEntity<String> sendVerifyEmail(@RequestParam String email, HttpSession session) {
		try {
			// 서버에서 보낸 인증 번호
			String verificationCode = mailService.sendMail(email);

			// 인증번호 세션에 저장
			session.setAttribute("emailVerificationCode", verificationCode);

			return ResponseEntity.ok("인증번호가 전송되었습니다.");
		} catch (EmailValidationException e) {
			log.error("[메일 전송 오류: ]", e);
			return ResponseEntity.status(500).body("인증번호 전송 중 오류가 발생했습니다.");
		}
	}

	// 입력된 사용자 인증 번호 검증 요청
	@PostMapping("/user/verify-email-code")
	public ResponseEntity<String> sendVerifyCode(@RequestParam String inputVerificationCode, HttpSession session) {
		try {
			// 서버에서 보낸 인증 번호
			String verificationCode = String.valueOf(session.getAttribute("emailVerificationCode"));

			// 인증번호 검증
			mailService.verifyCode(inputVerificationCode, verificationCode);

			// 이메일 인증 완료 상태 저장
			session.setAttribute("isEmailVerified", true);

			// 인증 완료 시 세션 제거
			session.removeAttribute("emailVerificationCode");

			return ResponseEntity.ok("인증이 완료되었습니다.");
		} catch (EmailValidationException e) {
			log.error("[인증번호 오류 : ]", e);
			return ResponseEntity.status(500).body("인증번호 확인 중 오류가 발생했습니다.");
		}
	}
}