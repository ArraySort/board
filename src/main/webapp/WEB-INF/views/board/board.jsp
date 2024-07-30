<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
</head>
<body>

<div style="text-align: center">

    <h1>게시판 페이지</h1>

    <div>
        <button type="button" onclick="location.href='/home'">홈 페이지로 이동</button>
        <button type="button" onclick="location.href='/board/post/add'">게시글 추가</button>
    </div>

    <!-- 게시판 시작 -->
    <div class="container" style="max-width: 850px; overflow-y: auto">
        <table class="table">
            <thead>
            <tr class="text-center">
                <th>번호</th>
                <th>제목</th>
                <th>유저아이디</th>
                <th>생성시간</th>
                <th>수정시간</th>
                <th>카테고리</th>
                <th>조회수</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="post" items="${postLists}">
                <tr>
                    <td>${post.postId}</td>
                    <td><a href="board/post/detail/${post.postId}">${post.title}</a></td>
                    <td>${post.userName}</td>
                    <td><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td><fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                    <td>${post.category}</td>
                    <td>${post.views}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <!-- 게시판 끝 -->

</div>

</body>
</html>
