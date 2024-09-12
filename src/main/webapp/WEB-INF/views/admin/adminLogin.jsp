<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Admin | 관리자 로그인</title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            // 로그인 생성 버튼 클릭 시
            $('#adminCreateButton').click(function (e) {
                const adminId = $('#adminCreateId').val();
                const adminPassword = $('#adminCreatePassword').val();

                if (!(adminId && adminPassword)) {
                    alertMessage(e, "아이디와 비밀번호를 올바르게 입력해주세요.")
                } else if (adminId.length < 4 || adminId.length > 20) {
                    alertMessage(e, "아이디는 최소 4글자 이상, 최대 20글자 이하입니다.")
                } else if (adminPassword.length < 4 || adminPassword.length > 20) {
                    alertMessage(e, "비밀번호는 최소 4글자 이상, 최대 20글자 이하입니다.")
                }
            });

            // 로그인 버튼 클릭 시
            $('#adminLoginButton').click(function (e) {
                const adminId = $('#adminId').val();
                const adminPassword = $('#adminPassword').val();

                if (!(adminId && adminPassword)) {
                    alertMessage(e, "아이디와 비밀번호를 올바르게 입력해주세요.")
                }
            });

            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }
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
            <!-- 카드 시작 -->
            <div class="card my-5">
                <!-- 컨텐츠 시작 -->
                <div class="card-body">
                    <!-- 관리자 로그인 헤더 -->
                    <div class="auth-header">
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/home" class="b-brand text-primary">
                                <img src="${pageContext.request.contextPath}/resources/assets/images/board-logo.png"
                                     alt="로고" class="logo" style="max-width: 100%">
                            </a>
                        </div>
                        <h2 class="text-secondary mt-5"><b>관리자 로그인</b></h2>
                    </div>
                    <!-- 관리자 로그인 헤더 종료 -->

                    <!-- 중앙 구분선 -->
                    <div class="saprator mt-3">
                        <span>중복 로그인이 불가능합니다.</span>
                    </div>

                    <!-- 로그인 입력 폼 -->
                    <form method="post" action="${pageContext.request.contextPath}/admin/process-login-admin"
                          id="adminLoginForm">
                        <sec:csrfInput/>

                        <h5 class="my-4 d-flex justify-content-center">아이디 / 비밀번호 입력</h5>

                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   name="adminId" id="adminId"
                                   placeholder="ID">
                            <label for="adminId">ID</label>
                        </div>
                        <div class="form-floating mb-3">
                            <input type="password"
                                   class="form-control"
                                   name="adminPassword" id="adminPassword"
                                   placeholder="PASSWORD">
                            <label for="adminPassword">PASSWORD</label>
                        </div>
                    </form>
                    <!-- 로그인 입력 폼 종료 -->

                    <!-- 로그인 버튼 -->
                    <div class="d-grid mt-4">
                        <button type="submit" id="loginButton" form="adminLoginForm" class="btn btn-secondary">로그인
                        </button>
                    </div>
                    <hr>

                    <!-- 하단 홈페이지 이동 -->
                    <div class="d-flex justify-content-between">
                        <button class="d-flex btn justify-content-center" onclick="location.href='/home'">
                            홈페이지
                        </button>
                    </div>
                </div>
                <!-- 컨텐츠 종료 -->
            </div>
            <!-- 카드 종료 -->
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

</body>
</html>
