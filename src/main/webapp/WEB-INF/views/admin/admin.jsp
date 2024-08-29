<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>관리자 메인 페이지</title>
</head>
<body>
<h1>관리자 메인 페이지</h1>
<ul>
    <li><a href="${pageContext.request.contextPath}/admin/user">회원 관리</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/board">게시판 관리</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/post">게시글 관리</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/comment">댓글 관리</a></li>
    <li><a href="${pageContext.request.contextPath}/admin/report">신고내역 관리</a></li>
</ul>

<form action="${pageContext.request.contextPath}/process-logout" method="post">
    <sec:csrfInput/>
    <button type="submit">로그아웃</button>
</form>

</body>
</html>
