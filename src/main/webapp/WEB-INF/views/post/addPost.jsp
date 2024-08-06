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

    <h1>게시글 추가</h1>

    <form method="post" action="<c:url value="/${boardId}/post/process-add-post"/>">
        <sec:csrfInput/>
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
            <input type="text" name="title" placeholder="제목" id="title"/>
        </div>
        <div>
            <label for="content">내용 : </label>
            <textarea type="text" name="content" placeholder="내용" id="content"></textarea>
        </div>

        <div>
            <label for="privateFlag-Y">공개</label>
            <input type="radio" id="privateFlag-Y" name="privateFlag" value="Y" checked/>

            <label for="privateFlag-N">비공개</label>
            <input type="radio" id="privateFlag-N" name="privateFlag" value="N"/>
        </div>

        <div>
            <button type="submit" id="addPost">저장</button>
            <button type="button" onclick="location.href='/${boardId}/post'">목록</button>
        </div>
    </form>

</div>

</body>
</html>
