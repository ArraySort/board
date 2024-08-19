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

    <style>
        #popupOverlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.7);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        #popup {
            position: relative;
            width: 80%;
            max-width: 800px;
            height: 80%;
            max-height: 600px;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
        }

        #popup img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }
    </style>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            $('#addCommentButton').on('click', function (e) {
                const commentContent = $('#commentContent').val();

                if (!commentContent) {
                    e.preventDefault();
                    alertMessage(e, "댓글을 등록하려면 내용을 입력하세요.");
                } else if (commentContent.length < 1 || commentContent.length > 200) {
                    e.preventDefault();
                    alertMessage(e, "댓글은 최소 1글자 이상, 200자 미만이어야 합니다.");
                }
            });
        });
    </script>

    <script>
        // 이미지 팝업
        function showImage(imageId) {
            const imageUrl = "/image/" + imageId;

            const popupOverlay = document.getElementById('popupOverlay');
            const popupImage = document.getElementById('popupImage');
            popupImage.src = imageUrl;

            popupOverlay.style.display = 'flex';
        }

        function closePopup() {
            const popupOverlay = document.getElementById('popupOverlay');
            popupOverlay.style.display = 'none';
        }

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                closePopup();
            }
        });
    </script>
</head>
<body>


<div style="text-align: center">
    <h1>게시글 세부 내용</h1>

    <div class="container" style="max-width: 850px;">

        <div>작성자 : ${postDetail.userName}</div>
        <div>카테고리 : ${postDetail.categoryName}</div>
        <div>현재 게시판 : ${postDetail.boardName}</div>

        <c:if test="${ boardDetail.boardType == 'GALLERY'}">
            <div>현재 썸네일 이미지</div>
            <img src="/image/${postDetail.imageId}" style="height: 200px; width: 30%" alt="${postDetail.title}">
        </c:if>

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

        <h4>이미지 목록</h4>

        <ul class="d-flex flex-column align-items-center">
            <c:forEach var="image" items="${images}">
                <li class="list-group-item d-flex justify-content-center align-items-center"
                    style="width: 50%">
                    <a href="javascript:showImage(${image.imageId})"
                       class="text-center mx-auto">
                            ${image.originalName}
                    </a>
                </li>
            </c:forEach>
        </ul>

        <div id="popupOverlay" onclick="closePopup()">
            <div id="popup" onclick="stopPropagation()">
                <img id="popupImage" src="" alt="이미지"/>
            </div>
        </div>

        <div>
            <label for="privateFlag-Y">공개</label>
            <input type="radio" id="privateFlag-Y" name="privateFlag" value="N"
            ${postDetail.privateFlag == 'N' ? 'checked' : ''} disabled/>

            <label for="privateFlag-N">비공개</label>
            <input type="radio" id="privateFlag-N" name="privateFlag" value="Y"
            ${postDetail.privateFlag == 'Y' ? 'checked' : ''} disabled/>
        </div>

        <h3>작성 시간 : <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
        <h3>수정 시간 : <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
        <h3>조회수 : ${postDetail.views}</h3>

        <div class="d-flex justify-content-center m-5">

            <form method="post" action="/${boardId}/post/detail/${postId}/comment/add">
                <sec:csrfInput/>
                <div class="input-group">
                    <input type="text" name="commentContent" id="commentContent" placeholder="댓글 입력 . . .">
                    <button type="submit" id="addCommentButton">입력</button>
                </div>
            </form>
        </div>

        <c:forEach var="comment" items="${commentPagination.postList}">
            <p>
                작성자 : ${comment.userName}
                내용 : ${comment.commentContent}

                작성시간 : <fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                수정시간 : <fmt:formatDate value="${comment.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
            </p>
        </c:forEach>

        <!-- 페이지 버튼 -->
        <nav>
            <ul class="pagination justify-content-center">
                <!-- 처음 페이지로 이동하는 버튼 -->
                <li class="page-item">
                    <a class="page-link"
                       href="/${boardId}/post/detail/${postId}?page=${page.page}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}&commentPage=1">&laquo</a>
                </li>
                <!-- 이전 블록으로 이동하는 버튼 -->
                <c:if test="${commentPagination.prev}">
                    <li class="page-item">
                        <a class="page-link"
                           href="/${boardId}/post/detail/${postId}?page=${page.page}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}&commentPage=${commentPagination.startBlockPage - 1}">
                            &lt;
                        </a>
                    </li>
                </c:if>
                <!-- 페이지 번호 -->
                <c:forEach var="pageNum" begin="${commentPagination.startBlockPage}"
                           end="${commentPagination.endBlockPage}">
                    <li class="page-item ${pageNum == commentPagination.currentPage ? 'active' : ''}">
                        <a class="page-link"
                           href="/${boardId}/post/detail/${postId}?page=${page.page}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}&commentPage=${pageNum}">
                                ${pageNum}
                        </a>
                    </li>
                </c:forEach>
                <!-- 다음 블록으로 이동하는 버튼 -->
                <c:if test="${commentPagination.next}">
                    <li class="page-item">
                        <a class="page-link"
                           href="/${boardId}/post/detail/${postId}?page=${page.page}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}&commentPage=${commentPagination.endBlockPage + 1}">
                            &gt;
                        </a>
                    </li>
                </c:if>
                <!-- 끝 페이지로 이동하는 버튼 -->
                <li class="page-item">
                    <a class="page-link"
                       href="/${boardId}/post/detail/${postId}?page=${page.page}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}&commentPage=${commentPagination.totalPageCount}">
                        &raquo;
                    </a>
                </li>
            </ul>
        </nav>

        <div class="d-flex justify-content-center">
            <form method="get" action="/${boardId}/post">
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
                <button type="button"
                        onclick="location.href='/${boardId}/post/detail/${postDetail.postId}/edit'">
                    수정
                </button>
                <form method="post"
                      action="/${boardId}/post/detail/${postDetail.postId}/delete">
                    <sec:csrfInput/>
                    <button type="submit">삭제</button>
                </form>
            </c:if>
        </div>

    </div>
</div>

</body>
</html>
