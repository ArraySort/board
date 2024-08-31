<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>게시글 관리 페이지</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        // 게시판 선택
        function sendBoardIdToButton() {
            const boardId = $('#boardId').val();
            if (boardId) {
                window.location.href = '/admin/post/' + boardId;
            } else {
                alert('게시판을 선택하세요');
            }
        }
    </script>

</head>
<body>
<h1 class="text-center my-4">게시글 관리 페이지</h1>

<div class="container">
    <div class="d-flex align-items-center justify-content-between mb-2">
        <div>
            <h5>현재 게시판 : ${currentBoard.boardName}</h5>
            <h3 class="mb-0">게시글 목록</h3>
        </div>

        <h4 class="mb-0 position-absolute start-50 translate-middle-x">
            총 게시글 개수 : ${postPagination.totalPostCount}
        </h4>

        <!-- 게시판 선택 -->
        <div class="d-flex align-items-center">
            <div class="input-group me-3" style="max-width: 300px;">
                <select id="boardId" name="boardId" class="form-control" aria-label="게시판 선택">
                    <option value="" selected>게시판 선택</option>
                    <c:forEach var="board" items="${boardList}">
                        <option value="${board.boardId}">${board.boardName}</option>
                    </c:forEach>
                </select>
                <button class="btn btn-dark" type="button" onclick="sendBoardIdToButton()">선택</button>
            </div>

            <%-- TODO : 게시글 추가 기능 구현--%>
            <button type="button" class="btn btn-primary me-2">게시글 추가</button>
            <button type="button" class="btn btn-primary"
                    onclick="location.href='/admin/post/${currentBoard.boardId}/deactivated'">비활성화 목록
            </button>
        </div>
    </div>

    <!-- 게시판 선택 -->
    <div class=" table-responsive">
        <table class="table table-bordered table-hover text-center">
            <thead class="thead-light">
            <tr>
                <th scope="col">번호</th>
                <th scope="col">제목</th>
                <th scope="col">작성자</th>
                <th scope="col">생성날짜</th>
                <th scope="col">수정날짜</th>
                <th scope="col">카테고리</th>
                <th scope="col">조회수</th>
                <th scope="col">댓글수</th>
                <th scope="col">채택댓글</th>
                <th scope="col">좋아요수</th>
                <th scope="col">비공개</th>
                <th scope="col">비활성화</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="post" items="${postPagination.postList}" varStatus="status">
                <c:set var="postNumber"
                       value="${postPagination.totalPostCount - ((postPagination.currentPage - 1) * 10) - status.index}"/>
                <tr onclick="location.href='/${currentBoard.boardId}/post/detail/${post.postId}'">
                    <th scope="row">${postNumber}</th>
                    <td class="text-truncate" style="max-width: 150px;">${post.title}</td>
                    <td>${post.userName}</td>
                    <td class="text-truncate" style="max-width: 150px;">
                        <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                    <td class="text-truncate" style="max-width: 150px;">
                        <fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                    </td>
                    <td>${post.categoryName}</td>
                    <td>${post.views}</td>
                    <td>${post.commentCount}</td>
                    <td>${post.adoptedCommentFlag}</td>
                    <td>${post.likeCount}</td>
                    <td>${post.privateFlag}</td>
                    <td>
                        <form action="/admin/post/${currentBoard.boardId}/${post.postId}/process-edit-activate-flag-post"
                              method="post">
                            <sec:csrfInput/>
                            <button class="btn-sm btn-primary" type="submit">비활성화</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 페이징 버튼 시작 -->
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/post/${boardId}?page=1">&laquo;</a>
            </li>
            <c:if test="${postPagination.prev}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.startBlockPage - 1}">&lt;</a>
                </li>
            </c:if>
            <c:forEach var="pageNum" begin="${postPagination.startBlockPage}" end="${postPagination.endBlockPage}">
                <li class="page-item ${pageNum == postPagination.currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${pageNum}">${pageNum}</a>
                </li>
            </c:forEach>
            <c:if test="${postPagination.next}">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.endBlockPage + 1}">&gt;</a>
                </li>
            </c:if>
            <li class="page-item">
                <a class="page-link"
                   href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.totalPageCount}">&raquo;</a>
            </li>
        </ul>
    </nav>
    <!-- 페이징 버튼 끝 -->
</div>

</body>
</html>
