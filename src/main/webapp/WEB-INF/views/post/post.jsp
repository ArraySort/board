<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Board</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <style>
        .gallery-card {
            width: 30%;
            margin-bottom: 20px;
        }

        .gallery-row {
            display: flex;
            justify-content: space-between;
        }

        .card-img-top {
            height: 200px;
            object-fit: cover;
        }
    </style>

</head>
<body>

<div style="text-align: center; overflow-y: auto">
    <c:choose>
        <c:when test="${boardDetail.boardType == 'GALLERY'}">
            <h1>갤러리 게시판 : ${boardDetail.boardName}</h1>
        </c:when>
        <c:otherwise>
            <h1>일반 게시판 : ${boardDetail.boardName}</h1>
        </c:otherwise>
    </c:choose>

    <%
        // 로그인 한 유저인지 확인하는 값
        boolean isAuthenticatedUser = UserUtil.isAuthenticatedUser();
        pageContext.setAttribute("isAuthenticatedUser", isAuthenticatedUser);
    %>

    <div class="m-3">
        <button type="button" onclick="location.href='/home'">홈 페이지로 이동</button>
        <button type="button" onclick="location.href='/${boardId}/post/add'">게시글 추가</button>
        <c:if test="${isAuthenticatedUser}">
            <button type="button" onclick="location.href='/${boardId}/post/temp'">임시저장 목록</button>
        </c:if>
    </div>

    <div>
        <!-- 검색기능 : 제목 입력 -->
        <form class="ms-auto d-inline-block m-2" method="get" action="/${boardId}/post">
            <div class="input-group">
                <select name="sortType" aria-label="sortType select">
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


    <c:if test="${boardDetail.boardType == 'GENERAL'}">
        <!-- 게시판 시작 -->
        <div class="container" style="height: 500px; overflow-y: auto">
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
                    <th>댓글수</th>
                    <th>채택댓글</th>
                    <th>좋아요수</th>
                    <th>비공개</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="post" items="${pagination.postList}" varStatus="status">
                    <c:set var="postNumber"
                           value="${pagination.totalPostCount - ((pagination.currentPage - 1) * 10) - status.index}"/>
                    <tr>
                        <td>${postNumber}</td>
                        <td>
                            <a href="/${boardId}/post/detail/${post.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                                <c:if test="${post.noticeFlag == 'Y'}">
                                    [공지사항]
                                </c:if>
                                    ${post.title}
                            </a>
                        </td>
                        <c:choose>
                            <c:when test="${post.adminId != null}">
                                <td>관리자</td>
                            </c:when>
                            <c:otherwise>
                                <td>${post.userName}</td>
                            </c:otherwise>
                        </c:choose>
                        <td><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td><fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        <td>${post.categoryName}</td>
                        <td>${post.views}</td>
                        <td>${post.commentCount}</td>
                        <td>${post.adoptedCommentFlag}</td>
                        <td>${post.likeCount}</td>
                        <td>${post.privateFlag}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
    <!-- 게시판 끝 -->

    <!-- 갤러리 게시판 시작 -->
    <c:if test="${boardDetail.boardType == 'GALLERY'}">
        <div class="container" style="max-width: 1000px">
            <c:forEach var="post" items="${pagination.postList}" varStatus="status">
                <c:if test="${status.index % 3 == 0}">
                    <div class="gallery-row">
                </c:if>
                <div class="card gallery-card">
                    <img src="/image/${post.imageId}" class="card-img-top" alt="${post.title}">
                    <div class="card-body">
                        <h5 class="card-title">
                            <a href="/${boardId}/post/detail/${post.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                                    ${post.title}
                            </a>
                        </h5>
                        <p class="card-text">카테고리 : ${post.categoryName}</p>
                        <p class="card-text">조회수 : ${post.views} views</p>
                        <div class="card-text">생성 날짜 :
                            <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </div>
                        <div class="card-text">수정 날짜 :
                            <fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </div>
                    </div>
                </div>
                <c:if test="${(status.index + 1) % 3 == 0 || status.last}">
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </c:if>
    <!-- 갤러리 게시판 끝 -->


    <!-- 페이지 버튼 -->
    <nav>
        <ul class="pagination justify-content-center">
            <!-- 처음 페이지로 이동하는 버튼 -->
            <li class="page-item">
                <a class="page-link"
                   href="/${boardId}/post?page=1&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">&laquo</a>
            </li>
            <!-- 이전 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.prev}">
                <li class="page-item">
                    <a class="page-link"
                       href="/${boardId}/post?page=${pagination.startBlockPage - 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                        &lt;
                    </a>
                </li>
            </c:if>
            <!-- 페이지 번호 -->
            <c:forEach var="pageNum" begin="${pagination.startBlockPage}" end="${pagination.endBlockPage}">
                <li class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="/${boardId}/post?page=${pageNum}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                            ${pageNum}
                    </a>
                </li>
            </c:forEach>
            <!-- 다음 블록으로 이동하는 버튼 -->
            <c:if test="${pagination.next}">
                <li class="page-item">
                    <a class="page-link"
                       href="/${boardId}/post?page=${pagination.endBlockPage + 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                        &gt;
                    </a>
                </li>
            </c:if>
            <!-- 끝 페이지로 이동하는 버튼 -->
            <li class="page-item">
                <a class="page-link"
                   href="/${boardId}/post?page=${pagination.totalPageCount}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                    &raquo;
                </a>
            </li>
        </ul>
    </nav>
    <!-- 페이지 버튼 끝-->

</div>

</body>
</html>
