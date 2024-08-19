package arraysort.project.board.app.user.service;

import arraysort.project.board.app.exception.EmailValidationException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

	private static final String SENDER_MAIL = "dudwns104google.com";

	private static final SecureRandom secureRandom = new SecureRandom();

	private static final int CODE_BOUND = 900000;

	private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

	private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

	private final JavaMailSender javaMailSender;

	// 인증 메일 전송
	public String sendMail(String mail) {
		String verificationCode = String.valueOf(secureRandom.nextInt(CODE_BOUND) + 100000);

		if (!pattern.matcher(mail).matches()) {
			throw new EmailValidationException("유효하지 않은 이메일 형식입니다.");
		}

		MimeMessage message = createMail(mail, verificationCode);
		javaMailSender.send(message);

		return verificationCode;
	}

	// 인증 번호 검증
	public void verifyCode(String inputCode, String verificationCode) {
		if (!Objects.equals(inputCode, verificationCode) || inputCode == null) {
			throw new EmailValidationException("인증 코드가 유효하지 않습니다.");
		}
	}

	/**
	 * 인증 메일 생성
	 *
	 * @param mail             사용자 메일
	 * @param verificationCode 인증번호
	 * @return 인증번호를 가진 사용자 인증 메일 메세지
	 */
	private MimeMessage createMail(String mail, String verificationCode) {
		MimeMessage message = javaMailSender.createMimeMessage();

		try {
			message.setFrom(SENDER_MAIL);
			message.setRecipients(Message.RecipientType.TO, mail);
			message.setSubject("이메일 인증");
			String body = "";
			body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
			body += "<h1>" + verificationCode + "</h1>";
			body += "<h3>" + "인증 번호를 입력해주세요." + "</h3>";
			message.setText(body, "UTF-8", "html");
		} catch (MessagingException e) {
			log.error("[인증 메일 생성 오류 : ]", e);
			throw new EmailValidationException("메일 생성 중 오류가 발생했습니다.");
		}

		return message;
	}
}
