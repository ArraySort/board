<%@ page import="arraysort.project.board.app.post.domain.BoardType" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Post Add</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <style>
        div {
            margin-bottom: 30px;
        }
    </style>

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
                    alertMessage(e, "제목은 최소 한글자, 최대 50글자이어야 합니다.");
                } else if (content.length < 1 && content.length > 500) {
                    alertMessage(e, "내용은 최소 한글자, 최대 500글자이어야 합니다.");
                }
            });
        });
    </script>

</head>
<body>

<div style="text-align: center">

    <h1>게시글 추가</h1>

    <form method="post" action="<c:url value="/board/post/process-add-post"/>">
        <sec:csrfInput/>
        <div>
            <select name="category" aria-label="category select" id="category">
                <option value="">카테고리 선택</option>
                <option value="category1">카테고리 1번</option>
                <option value="category2">카테고리 2번</option>
                <option value="category3">카테고리 3번</option>
            </select>
        </div>

        <div>
            <label for="title">제목 : </label>
            <input type="text" name="title" placeholder="제목" id="title"/>
        </div>
        <div>
            <label for="content">내용 : </label>
            <textarea type="text" name="content" placeholder="내용" id="content"></textarea>
        </div>

        <%
            // 게시판 타입
            BoardType[] boardTypes = BoardType.values();
            pageContext.setAttribute("boardTypes", boardTypes);
        %>

        <div>
            <c:forEach var="boardType" items="${boardTypes}">
                <label for="type${boardType}">${boardType}</label>
                <input type="radio" id="type${boardType}" name="type"
                       value="${boardType}" ${boardType == 'GENERAL' ? 'checked' : ''}/>
            </c:forEach>
        </div>

        <div>
            <button type="submit" id="addPost">저장</button>
            <button type="button" onclick="location.href='/board'">목록</button>
        </div>
    </form>

</div>

</body>
</html>
