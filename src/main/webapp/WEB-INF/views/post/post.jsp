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

    <h1>일반 게시판 페이지</h1>

    <div class="m-3">
        <button type="button" onclick="location.href='/home'">홈 페이지로 이동</button>
        <button type="button" onclick="location.href='/${boardId}/post/add'">게시글 추가</button>
    </div>

    <div>
        <!-- 검색기능 : 제목 입력 -->
        <form class="ms-auto d-inline-block m-2" method="get" action="/post">
            <div class="input-group">
                <select name="sortType" aria-label="sortType select">
                    <option value="ID" ${page.sortType == 'ID' ? 'selected' : ''}>번호순</option>
                    <option value="LATEST" ${page.sortType == 'LATEST' ? 'selected' : ''}>최신순</option>
                    <option value="OLDEST" ${page.sortType == 'OLDEST' ? 'selected' : ''}>오래된순</option>
                    <option value="VIEWS" ${page.sortType == 'VIEWS' ? 'selected' : ''}>조회순</option>
                </select>
                <select name="searchType" aria-label="searchType select">
                    <option value="ALL" ${page.searchType == 'ALL' ? 'selected' : ''}>전체</option>
                    <option value="TITLE" ${page.searchType == 'TITLE' ? 'selected' : ''}>제목</option>
                    <option value="CONTENT" ${page.searchType == 'CONTENT' ? 'selected' : ''}>내용</option>
                </select>
                <input name="search" value="${page.search}" type="text">
                <button type="submit">검색</button>
            </div>
        </form>
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
                    <td>
                        <a href="/post/detail/${post.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">${post.title}</a>
                    </td>
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
                <a class="page-link"
                   href="/post?page=1&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">&laquo</a>
            </li>
            <!-- 이전 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.prev}">
                <li class="page-item">
                    <a class="page-link"
                       href="/post?page=${pagination.startBlockPage - 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                        &lt;
                    </a>
                </li>
            </c:if>
            <!-- 페이지 번호 -->
            <c:forEach var="pageNum" begin="${pagination.startBlockPage}" end="${pagination.endBlockPage}">
                <li class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="/post?page=${pageNum}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                            ${pageNum}
                    </a>
                </li>
            </c:forEach>
            <!-- 다음 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.next}">
                <li class="page-item">
                    <a class="page-link"
                       href="/post?page=${pagination.endBlockPage + 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                        &gt;
                    </a>
                </li>
            </c:if>
            <!-- 끝 페이지로 이동하는 버튼 -->
            <li class="page-item">
                <a class="page-link"
                   href="/post?page=${pagination.totalPageCount}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                    &raquo;
                </a>
            </li>
        </ul>
    </nav>
    <!-- 페이지 버튼 끝-->

</div>

</body>
</html>
