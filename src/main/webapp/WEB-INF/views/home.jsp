<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home</title>
</head>
<body>

<div style="text-align: center">

    <%
        // 로그인 한 유저인지 확인하는 값
        boolean isAuthenticatedUser = UserUtil.isAuthenticatedUser();
        pageContext.setAttribute("isAuthenticatedUser", isAuthenticatedUser);
    %>

    <h1>${message}</h1>

    <c:if test="${!isAuthenticatedUser}">
        <button type="button" onclick="location.href='/loginPage'">로그인 페이지로 이동</button>
        <button type="button" onclick="location.href='/signupPage'">회원가입 페이지로 이동</button>
    </c:if>

    <c:if test="${isAuthenticatedUser}">
        <button type="button" onclick="location.href='/board'">게시판으로 이동</button>
        <form action="${pageContext.request.contextPath}/process-logout" method="post">
            <sec:csrfInput/>
            <button type="submit">로그아웃</button>
        </form>
    </c:if>

</div>

</body>
</html>