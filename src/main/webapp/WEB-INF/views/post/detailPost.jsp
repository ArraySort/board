<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Post Detail</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
</head>
<body>


<div style="text-align: center">
    <h1>게시글 세부 내용</h1>

    <div class="container" style="max-width: 850px;">

        <h3>작성자 : ${postDetail.userName}</h3>
        <h3>게시판 종류 : ${postDetail.type}</h3>
        <h3>카테고리 : ${postDetail.category}</h3>

        <div>
            <label for="title">제목 : </label>
            <input type="text"
                   name="title"
                   placeholder="제목"
                   value="${postDetail.title}"
                   id="title"
                   readonly/>
        </div>

        <br/>

        <div>
            <label for="content">내용 : </label>
            <textarea type="text"
                      name="content"
                      placeholder="내용"
                      id="content"
                      readonly>${postDetail.content}
            </textarea>
        </div>

        <h3>작성 시간 : <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
        <h3>수정 시간 : <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
        <h3>조회수 : ${postDetail.views}</h3>

        <div class="d-flex justify-content-center">
            <form method="get" action="/${boardId}}/post">
                <input type="hidden" name="search" value="${page.search}">
                <input type="hidden" name="searchType" value="${page.searchType}">
                <input type="hidden" name="sortType" value="${page.sortType}">
                <input type="hidden" name="page" value="${page.page}">
                <button type="submit">목록</button>
            </form>
            <%
                // 현재 로그인 한 유저의 값
                String currentUserId = UserUtil.getCurrentLoginUserId();
                pageContext.setAttribute("currentUserId", currentUserId);
            %>

            <c:if test="${postDetail.userId == currentUserId}">
                <button type="button" onclick="location.href='/${boardId}}/post/detail/${postDetail.postId}/edit'">수정
                </button>
                <form method="post" action="/${boardId}/post/detail/${postDetail.postId}/delete">
                    <sec:csrfInput/>
                    <button type="submit">삭제</button>
                </form>
            </c:if>
        </div>

    </div>
</div>

</body>
</html>
