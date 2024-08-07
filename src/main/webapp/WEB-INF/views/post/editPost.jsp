<%@ page import="arraysort.project.board.app.post.domain.BoardType" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Post Edit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            $('#addPost').on('click', function (e) {
                const category = $('#category').val();
                const title = $('#title').val();
                const content = $('#content').val();

                if (!(category && title && content)) {
                    alertMessage(e, "카테고리, 제목, 내용은 필수 입력사항입니다.")
                } else if (title.length < 1 && title.length > 50) {
                    alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.");
                } else if (content.length < 1 && content.length > 500) {
                    alertMessage(e, "내용은 최소 1글자, 최대 500글자이어야 합니다.");
                }
            });
        });
    </script>

</head>
<body>

<div style="text-align: center">
    <h1>게시글 수정</h1>
    <div class="container" style="max-width: 850px;">
        <form method="post" action="/${boardId}/post/detail/${postDetail.postId}/edit">
            <sec:csrfInput/>

            <input type="hidden" name="search" value="${page.search}">
            <input type="hidden" name="searchType" value="${page.searchType}">
            <input type="hidden" name="sortType" value="${page.sortType}">
            <input type="hidden" name="page" value="${page.page}">

            <h3>작성자 : ${postDetail.userName}</h3>
            <h3>카테고리 : ${postDetail.categoryName}</h3>
            <h3>현재 게시판 : ${postDetail.boardName}</h3>

            <div>
                <select name="categoryId" aria-label="category select" id="category">
                    <option value="">카테고리 선택</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.categoryId}">${category.categoryName}</option>
                    </c:forEach>
                </select>
            </div>

            <div>
                <label for="title">제목 : </label>
                <input type="text"
                       name="title"
                       placeholder="제목"
                       value="${postDetail.title}"
                       id="title"/>
            </div>

            <br/>

            <div>
                <label for="content">내용 : </label>
                <textarea type="text"
                          name="content"
                          placeholder="내용"
                          id="content">${postDetail.content}</textarea>
            </div>

            <div>
                <label for="privateFlag-Y">공개</label>
                <input type="radio" id="privateFlag-Y" name="privateFlag" value="N" checked/>

                <label for="privateFlag-N">비공개</label>
                <input type="radio" id="privateFlag-N" name="privateFlag" value="Y"/>
            </div>

            <h3>작성 시간 : <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
            <h3>수정 시간 : <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></h3>

            <button type="submit">저장</button>
            <a href="/${boardId}/post/detail/${postDetail.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                <button type="button">취소</button>
            </a>
        </form>
    </div>

</div>

</body>
</html>
