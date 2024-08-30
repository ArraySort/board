<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>게시판 관리 페이지</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
</head>
<body>
<h1 class="text-center my-4">게시판 관리 페이지</h1>
<div class="container">

    <div class="d-flex justify-content-between mb-2">
        <h3 class="mb-0">게시판 목록</h3>
        <h4 class="mb-0">총 게시판 개수 : ${boardPagination.totalPostCount}</h4>
        <button type="button" class="btn btn-primary" onclick="location.href='/admin/board/add'">게시판 추가</button>
    </div>

    <!-- 테이블 시작 -->
    <div class="table-responsive">
        <table class="table table-bordered table-hover text-center">
            <thead class="thead-light">
            <tr>
                <th scope="col">번호</th>
                <th scope="col">이름</th>
                <th scope="col">종류</th>
                <th scope="col">순서</th>
                <th scope="col">이미지 허용</th>
                <th scope="col">이미지 수</th>
                <th scope="col">댓글 허용</th>
                <th scope="col">공지사항 수</th>
                <th scope="col">접근 등급</th>
                <th scope="col">생성날짜</th>
                <th scope="col">수정날짜</th>
                <th scope="col">생성자</th>
                <th scope="col">수정자</th>
                <th scope="col">수정</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="board" items="${boardPagination.postList}" varStatus="status">
                <c:set var="postNumber"
                       value="${boardPagination.totalPostCount - ((boardPagination.currentPage - 1) * 10) - status.index}"/>
                <tr>
                    <th scope="row">${postNumber}</th>
                    <td class="text-truncate" style="max-width: 150px;">${board.boardName}</td>
                    <td class="text-truncate" style="max-width: 100px;">${board.boardType}</td>
                    <td>${board.boardOrder}</td>
                    <td>${board.imageFlag}</td>
                    <td>${board.imageLimit}</td>
                    <td>${board.commentFlag}</td>
                    <td>${board.noticeCount}</td>
                    <td>${board.accessLevel}</td>
                    <td class="text-truncate" style="max-width: 150px;">
                        <fmt:formatDate value="${board.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                    <td class="text-truncate" style="max-width: 150px;">
                        <fmt:formatDate value="${board.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                    <td class="text-truncate" style="max-width: 100px;">${board.createdBy}</td>
                    <td class="text-truncate" style="max-width: 100px;">${board.updatedBy}</td>
                    <td>
                        <button class="btn btn-outline-primary" type="button"
                                onclick="location.href='/admin/board/${board.boardId}/edit'">수정
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <!-- 테이블 끝 -->

    <!-- 페이지 시작 -->
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/board?page=1">&laquo;</a>
            </li>
            <c:if test="${boardPagination.prev}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/board?page=${pagination.startBlockPage - 1}">&lt;</a>
                </li>
            </c:if>
            <c:forEach var="pageNum" begin="${boardPagination.startBlockPage}" end="${boardPagination.endBlockPage}">
                <li class="page-item ${pageNum == boardPagination.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/board?page=${pageNum}">${pageNum}</a>
                </li>
            </c:forEach>
            <c:if test="${pagination.next}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/board?page=${boardPagination.endBlockPage + 1}">&gt;</a>
                </li>
            </c:if>
            <li class="page-item">
                <a class="page-link"
                   href="${pageContext.request.contextPath}/admin/board?page=${boardPagination.totalPageCount}">&raquo;</a>
            </li>
        </ul>
    </nav>
    <!-- 페이지 시작 -->

</div>
</body>
</html>