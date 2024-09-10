<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login | 게시판 로그인</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/authButton.css">
    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            $('#loginButton').on('click', function (e) {
                const userId = $('#userId').val();
                const userPassword = $('#userPassword').val();

                if (!(userId && userPassword)) {
                    e.preventDefault();
                    alert("아이디와 비밀번호를 올바르게 입력해주세요.");
                }
            });
        });
    </script>
</head>

<body>
<div class="loader-bg">
    <div class="loader-track">
        <div class="loader-fill"></div>
    </div>
</div>

<div class="auth-main">
    <div class="auth-wrapper v3">
        <div class="auth-form">
            <div class="card my-5">
                <div class="card-body">
                    <div class="row">
                        <div class="d-flex justify-content-center">
                            <div class="auth-header">
                                <h2 class="text-secondary mt-5"><b>로그인</b></h2>
                                <p class="f-16 mt-2">로그인 방식을 선택해주세요</p>
                            </div>
                        </div>
                    </div>

                    <!-- OAuth2 로그인 -->
                    <div class="d-flex justify-content-center mb-4">
                        <button type="button"
                                class="btn btn-custom mx-2"
                                onclick="location.href='/oauth2/authorization/naver'">
                            <img src="${pageContext.request.contextPath}/resources/assets/images/auth/btn_naver.svg"
                                 alt="네이버 로그인"/>
                        </button>
                        <button type="button"
                                class="btn btn-custom mx-2"
                                onclick="location.href='/oauth2/authorization/kakao'">
                            <img src="${pageContext.request.contextPath}/resources/assets/images/auth/btn_kakao.svg"
                                 alt="카카오 로그인"/>
                        </button>
                        <button type="button"
                                class="btn btn-custom mx-2"
                                onclick="location.href='/oauth2/authorization/google'">
                            <img src="${pageContext.request.contextPath}/resources/assets/images/auth/btn_google.svg"
                                 alt="구글 로그인"/>
                        </button>
                    </div>

                    <div class="saprator mt-3">
                        <span>or</span>
                    </div>

                    <!-- 로그인 폼 -->
                    <form method="post" action="${pageContext.request.contextPath}/user/process-login" id="loginForm">
                        <sec:csrfInput/>

                        <h5 class="my-4 d-flex justify-content-center">아이디 / 비밀번호 입력</h5>

                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   name="userId" id="userId"
                                   placeholder="ID">
                            <label for="userId">ID</label>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="password"
                                   class="form-control"
                                   name="userPassword" id="userPassword"
                                   placeholder="PASSWORD">
                            <label for="userPassword">PASSWORD</label>
                        </div>

                        <div class="d-flex mt-1 justify-content-between">
                            <div class="form-check">
                                <input type="checkbox"
                                       class="form-check-input input-primary"
                                       name="remember-me" id="remember-me">
                                <label class="form-check-label text-muted" for="remember-me">로그인 상태 유지</label>
                            </div>
                        </div>
                    </form>

                    <!-- 로그인 버튼 -->
                    <div class="d-grid mt-4">
                        <button type="submit" id="loginButton" form="loginForm" class="btn btn-secondary">로그인</button>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-between">
                        <button class="d-flex btn justify-content-center" onclick="location.href='/home'">
                            홈페이지
                        </button>
                        <button class="d-flex btn justify-content-center" onclick="location.href='/user/signup'">
                            회원가입
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

</body>
</html>
