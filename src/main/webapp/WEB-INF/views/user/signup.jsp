<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SignUp</title>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            $('#signupButton').on('click', function (e) {

                const userId = $('#userId').val();
                const userPassword = $('#userPassword').val();
                const userName = $('#userName').val();

                if (!(userId && userPassword && userName)) {
                    alertMessage(e, "아이디와 비밀번호, 이름 입력은 필수입니다.");
                } else if (userId.length < 4 || userId.length > 20) {
                    alertMessage(e, "아이디는 최소 4글자 이상, 최대 20글자 이하입니다.");
                } else if (userPassword.length < 4 || userPassword.length > 20) {
                    alertMessage(e, "패스워드는 최소 4글자 이상, 최대 20글자 이하입니다.");
                } else if (userName.length < 2 || userName.length > 10) {
                    alertMessage(e, "이름은 최소 2글자 이상, 최대 10글자 이하입니다.");
                }
            });
        });
    </script>

</head>
<body>

<div style="text-align: center">
    <h1>회원가입 페이지</h1>

    <form method="post" action="${pageContext.request.contextPath}/user/process-signup" id="signupForm">
        <sec:csrfInput/>
        <div>
            <div>
                <label for="userId">아이디 : </label>
                <input placeholder="아이디" type="text" name="userId" id="userId"/>
            </div>

            <br/>

            <div>
                <label for="userPassword">비밀번호 : </label>
                <input placeholder="비밀번호" type="password" name="userPassword" id="userPassword"/>
            </div>

            <br/>

            <div>
                <label for="userName">이름 : </label>
                <input placeholder="이름" type="text" name="userName" id="userName">
            </div>
        </div>
    </form>

    <br/>

    <div>
        <button type="submit" id="signupButton" form="signupForm">회원가입</button>
        <button type="button" onclick="location.href='../../..'">뒤로가기</button>
    </div>

</div>

</body>
</html>
