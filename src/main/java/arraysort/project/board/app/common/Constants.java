package arraysort.project.board.app.common;

public class Constants {

	public static final int PAGE_ROW_COUNT = 10;    // 페이징 : 한페이지 당 보여줄 게시물 개수
	public static final int PAGE_BLOCK_COUNT = 5;    // 페이징 : 페이지 블럭에 보여줄 페이지 개수

	// OAuth : registrationId
	public static final String REGISTRATION_ID_GOOGLE = "google";
	public static final String REGISTRATION_ID_KAKAO = "kakao";
	public static final String REGISTRATION_ID_NAVER = "naver";

	// OAuth : Provider
	public static final String OAUTH_PROVIDER_GOOGLE = "GOOGLE";
	public static final String OAUTH_PROVIDER_KAKAO = "KAKAO";
	public static final String OAUTH_PROVIDER_NAVER = "NAVER";

	// Login Attempts
	public static final int MAX_ATTEMPTS_COUNT = 3;    // 로그인 시도 최대 횟수
	public static final int LOGIN_LOCK_SEC = 30;    // 로그인 잠금 시간


	private Constants() {
	}
}
