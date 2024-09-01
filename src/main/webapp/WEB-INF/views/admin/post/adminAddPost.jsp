<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>관리자 게시글 추가</title>
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

            // 입력 폼 검증
            function validateAddForm(e) {
                const category = $('#category').val();
                const title = $('#title').val();
                const content = $('#content').val();

                if (!(category && title && content)) {
                    alertMessage(e, "카테고리, 제목, 내용은 필수 입력사항입니다.");
                } else if (title.length < 1 || title.length > 50) {
                    alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.");
                } else if (content.length < 1 || content.length > 500) {
                    alertMessage(e, "내용은 최소 1글자, 최대 500글자이어야 합니다.");
                }
            }

            function validateAddTempForm(e) {
                const title = $('#title').val();

                if (!title) {
                    alertMessage(e, "제목은 필수 입력사항입니다.")
                } else if (title.length < 1 || title.length > 50) {
                    alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.")
                }
            }

            // 업로드 이미지 미리보기

            $('#thumbnailImage').on('change', function () {
                const file = this.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        $('#imagePreview').attr('src', e.target.result).show();
                    };
                    reader.readAsDataURL(file);
                }
            });

            // 저장 버튼
            $('#addPost').on('click', function (e) {
                $('#postForm').attr('action', '<c:url value="/admin/post/${boardDetail.boardId}/add-admin-post"/>');
                validateAddForm(e);
            });
        })
        ;
    </script>

</head>
<body>

<div style="text-align: center">

    <h1>관리자 게시글 추가</h1>

    <form id="postForm" enctype="multipart/form-data" method="post">
        <sec:csrfInput/>

        <c:if test="${boardDetail.boardType == 'GALLERY'}">
            <div>
                <img src="" id="imagePreview" style="height: 200px; width: 20%"
                     alt="이미지 업로드 미리보기">
                <div>
                    <div>썸네일 이미지 업로드</div>
                    <input type="file" name="thumbnailImage" id="thumbnailImage">
                </div>
            </div>
        </c:if>

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

        <c:choose>
            <c:when test="${boardDetail.imageFlag == 'Y'}">
                <input type="file" name="images" multiple>
            </c:when>
            <c:otherwise>
                <p>이미지 업로드가 허용되지 않는 게시판입니다.</p>
            </c:otherwise>
        </c:choose>

        <div>
            <label for="privateFlag-Y">공개</label>
            <input type="radio" id="privateFlag-Y" name="privateFlag" value="N" checked/>

            <label for="privateFlag-N">비공개</label>
            <input type="radio" id="privateFlag-N" name="privateFlag" value="Y"/>
        </div>


        <div>
            <label for="noticeFlag-Y">일반 게시글</label>
            <input type="radio" id="noticeFlag-Y" name="noticeFlag" value="N" checked/>

            <label for="noticeFlag-N">공지사항 게시글</label>
            <input type="radio" id="noticeFlag-N" name="noticeFlag" value="Y"/>
        </div>

        <div>
            <button type="submit" id="addPost">저장</button>
            <button type="button" onclick="location.href='/admin/post/${boardDetail.boardId}'">목록</button>
        </div>
    </form>

</div>

</body>
</html>
