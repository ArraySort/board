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
        <button type="button" onclick="location.href='/post/add'">게시글 추가</button>
    </div>

    <!-- 게시판 시작 -->
    <div class="container" style="max-width: 850px; height: 500px; overflow-y: auto">
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
            <c:forEach var="post" items="${pagination.postList}">
                <tr>
                    <td>${post.postId}</td>
                    <td><a href="/post/detail/${post.postId}">${post.title}</a></td>
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

    <!-- 페이지 버튼 -->
    <nav>
        <ul class="pagination justify-content-center">
            <!-- 처음 페이지로 이동하는 버튼 -->
            <li class="page-item">
                <a class="page-link" href="/post?page=1">&laquo</a>
            </li>
            <!-- 이전 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.prev}">
                <li class="page-item">
                    <a class="page-link"
                       href="/post?page=${pagination.startBlockPage - 1}">
                        &lt;
                    </a>
                </li>
            </c:if>
            <!-- 페이지 번호 -->
            <c:forEach var="pageNum" begin="${pagination.startBlockPage}" end="${pagination.endBlockPage}">
                <li class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                    <a class="page-link" href="/post?page=${pageNum}">
                            ${pageNum}
                    </a>
                </li>
            </c:forEach>
            <!-- 다음 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.next}">
                <li class="page-item">
                    <a class="page-link"
                       href="/post?page=${pagination.endBlockPage + 1}">
                        &gt;
                    </a>
                </li>
            </c:if>
            <!-- 끝 페이지로 이동하는 버튼 -->
            <li class="page-item">
                <a class="page-link"
                   href="/post?page=${pagination.totalPageCount}">
                    &raquo;
                </a>
            </li>
        </ul>
    </nav>
    <!-- 페이지 버튼 끝-->

</div>

</body>
</html>
