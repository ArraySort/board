<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>관리자 로그인 페이지</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

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

<div class="container text-center">

    <h1 class="mb-5">관리자 로그인 페이지</h1>

    <!-- 관리자 추가 폼 -> TODO : 기능 완성 후 제거 -->
    <span>관리자 생성</span>
    <div class="row justify-content-center mb-5">
        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/admin/process-add-admin" method="post">
                <sec:csrfInput/>

                <div class="form-group row">
                    <label for="adminCreateId" class="col-sm-3 col-form-label">관리자 ID</label>
                    <div class="col-sm-7">
                        <input type="text" class="form-control" name="adminId" id="adminCreateId" placeholder="아이디"/>
                    </div>
                </div>

                <div class="form-group row mt-3">
                    <label for="adminCreatePassword" class="col-sm-3 col-form-label">비밀번호</label>
                    <div class="col-sm-7">
                        <input type="password" class="form-control" name="adminPassword" id="adminCreatePassword"
                               placeholder="비밀번호"/>
                    </div>
                </div>

                <div class="form-group row mt-3">
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary" id="adminCreateButton">생성</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <span>관리자 로그인</span>
    <!-- 관리자 로그인 폼 -->
    <div class="row justify-content-center">

        <div class="col-md-6">
            <form action="${pageContext.request.contextPath}/admin/process-login-admin" method="post">
                <sec:csrfInput/>

                <div class="form-group row">
                    <label for="adminId" class="col-sm-3 col-form-label">관리자 ID</label>
                    <div class="col-sm-7">
                        <input type="text" class="form-control" name="adminId" id="adminId" placeholder="아이디"/>
                    </div>
                </div>

                <div class="form-group row mt-3">
                    <label for="adminPassword" class="col-sm-3 col-form-label">비밀번호</label>
                    <div class="col-sm-7">
                        <input type="password" class="form-control" name="adminPassword" id="adminPassword"
                               placeholder="비밀번호"/>
                    </div>
                </div>

                <div class="form-group row mt-3">
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary" id="adminLoginButton">로그인</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>