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

	// Max Comment SIZE
	public static final int MAX_COMMENT_IMAGE_LIMIT = 2;    // 댓글 이미지 최대 개수

	// Spring Security ROLE
	public static final String ROLE_USER = "ROLE_USER";
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ANONYMOUS_USER = "anonymousUser";

	// Controller ModelAndVIew
	public static final String MAV_ALERT = "common/alert";

	public static final String MAV_ADMIN = "admin/admin";
	public static final String MAV_ADMIN_LOGIN = "admin/adminLogin";
	public static final String MAV_ADMIN_USER_MANAGEMENT = "admin/adminUserManagement";
	public static final String MAV_ADMIN_BOARD_MANAGEMENT = "admin/adminBoardManagement";
	public static final String MAV_ADMIN_POST_MANAGEMENT = "admin/adminPostManagement";
	public static final String MAV_ADMIN_COMMENT_MANAGEMENT = "admin/adminCommentManagement";
	public static final String MAV_ADMIN_REPORT_MANAGEMENT = "admin/adminReportManagement";

	public static final String MAV_REQUEST_LOGIN_ADMIN = "LOGIN_ADMIN";
	public static final String MAV_REQUEST_ADD_ADMIN = "ADD_ADMIN";

	public static final String MAV_REQUEST_ADD_COMMENT = "ADD_COMMENT";
	public static final String MAV_REQUEST_MODIFY_COMMENT = "MODIFY_COMMENT";
	public static final String MAV_REQUEST_DELETE_COMMENT = "DELETE_COMMENT";
	public static final String MAV_REQUEST_ADOPT_COMMENT = "ADOPT_COMMENT";

	public static final String MAV_HOME = "home";
	public static final String REDIRECT_HOME = "redirect:/home";

	public static final String MAV_POST = "post/post";
	public static final String MAV_ADD_POST = "post/addPost";
	public static final String MAV_DETAIL_POST = "post/detailPost";
	public static final String MAV_EDIT_POST = "post/editPost";

	public static final String MAV_REQUEST_ADD_POST = "ADD_POST";
	public static final String MAV_REQUEST_MODIFY_POST = "MODIFY_POST";
	public static final String MAV_REQUEST_DELETE_POST = "DELETE_POST";

	public static final String MAV_TEMP_POST = "post/tempPost";
	public static final String MAV_EDIT_TEMP_POST = "post/editTempPost";

	public static final String MAV_REQUEST_ADD_TEMP = "ADD_TEMP";
	public static final String MAV_REQUEST_MODIFY_TEMP = "MODIFY_TEMP";
	public static final String MAV_REQUEST_PUBLISH_POST = "PUBLISH_POST";
	public static final String MAV_REQUEST_DELETE_TEMP = "DELETE_TEMP";

	public static final String MAV_USER_LOGIN = "user/login";
	public static final String MAV_USER_SIGNUP = "user/signup";

	public static final String MAV_REQUEST_SIGNUP = "SIGNUP";

	private Constants() {
	}
}
