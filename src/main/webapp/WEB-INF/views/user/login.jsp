<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

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

<div style="text-align: center">

    <h1>로그인 페이지</h1>

    <div>
        <form method="post" action="${pageContext.request.contextPath}/user/process-login" id="loginForm">
            <sec:csrfInput/>
            <div>
                <label for="userId">아이디 : </label>
                <input placeholder="아이디" type="text" name="userId" id="userId"/>
            </div>

            <br/>

            <div>
                <label for="userPassword">비밀번호 : </label>
                <input placeholder="비밀번호" type="password" name="userPassword" id="userPassword"/>
            </div>
        </form>
    </div>

    <button type="submit" id="loginButton" form="loginForm">로그인</button>
    <button type="button" onclick="location.href='/home'">홈 페이지로 이동</button>

</div>

</body>
</html>
