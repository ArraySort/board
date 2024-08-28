<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>관리자 로그인 페이지</title>
</head>
<body>
<h1>관리자 로그인 페이지</h1>

<form action="${pageContext.request.contextPath}/admin/process-add-admin" method="post">
    <sec:csrfInput/>
    <input type="text" name="adminId"/>
    <input type="password" name="adminPassword"/>
    <button type="submit">생성</button>
</form>


<form action="${pageContext.request.contextPath}/admin/process-login-admin" method="post">
    <sec:csrfInput/>
    <input type="text" name="adminId"/>
    <input type="password" name="adminPassword"/>
    <button type="submit">로그인</button>
</form>

</body>
</html>
