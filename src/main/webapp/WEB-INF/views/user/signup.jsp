<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: an-yeongjun
  Date: 7/28/24
  Time: 17:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SignUp</title>
</head>
<body>

<div>
    <h1>회원가입 페이지</h1>

    <form method="post" action="<c:url value="/process-signup"/>" id="signupForm">
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
                <label for="userName">닉네임 : </label>
                <input placeholder="이름" type="text" name="userName" id="userName">
            </div>
        </div>
    </form>

    <br/>

    <div>
        <button type="button" onclick="location.href='../../..'">뒤로가기</button>
        <button type="submit" form="signupForm">회원가입</button>
    </div>

</div>

</body>
</html>
