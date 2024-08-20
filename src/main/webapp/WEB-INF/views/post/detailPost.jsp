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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/popup.css">

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {

            let commentImages = [];

            // 새로운 댓글 이미지 추가
            $('#commentImageInput').on('change', function () {
                const files = this.files;

                commentImages = [];

                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const imageUrl = URL.createObjectURL(file);

                    commentImages.push(file);

                    $('#addedCommentImagesList').append(`
                        <li class="list-group-item d-flex justify-content-center align-items-center">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-center mx-auto">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm remove-added-image-btn ml-auto">X</button>
                        </li>
                    `);
                }

                updateAddedImagesInput();
            });

            // 추가된 이미지 삭제
            $(document).on('click', '.remove-added-image-btn', function () {
                const index = $(this).closest('li').index();

                commentImages.splice(index, 1);
                $(this).closest('li').remove();

                updateAddedImagesInput();
            });

            // 이미지 업로드 버튼 (+) 버튼 클릭 시 input 활성화
            $('#uploadCommentImageButton').click(function () {
                $('#commentImageInput').click();
            });


            // 댓글 추가 버튼 클릭 시 댓글 내용 검증
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

            // 댓글 수정 버튼 클릭 시
            $('#commentEditButton').click(function () {
                const commentId = $(this).data('id');
                $('#commentContent-' + commentId).hide();
                $('#editForm-' + commentId).show();
            });

            // 댓글 수정 취소 버튼 클릭 시
            $('#commentEditCancelButton').click(function () {
                const commentId = $(this).data('id');
                $('#editForm-' + commentId).hide();
                $('#commentContent-' + commentId).show();
            });

            // 메세지 출력
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            // 추가 이미지 업데이트
            function updateAddedImagesInput() {
                let dataTransfer = new DataTransfer();
                commentImages.forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#commentImageInput')[0].files = dataTransfer.files;
            }
        });
    </script>

    <script>
        // 이미지 팝업
        function showImage(imageUrl) {
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

<%
    // 현재 로그인 한 유저의 값
    String currentUserId = UserUtil.getCurrentLoginUserId();
    pageContext.setAttribute("currentUserId", currentUserId);
%>

<div style="text-align: center">
    <h1>게시글 세부 내용</h1>

    <div class="container" style="max-width: 850px;">

        <!-- 팝업 이미지 : 게시글 이미지, 댓글 이미지 -->
        <div id="popupOverlay" onclick="closePopup()">
            <div id="popup" onclick="stopPropagation()">
                <img id="popupImage" src="" alt="이미지"/>
            </div>
        </div>

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

        <!-- 이미지 목록 -->
        <ul class="d-flex flex-column align-items-center">
            <c:forEach var="image" items="${images}">
                <li class="list-group-item d-flex justify-content-center align-items-center"
                    style="width: 50%">
                    <a href="javascript:showImage(`/image/ + ${image.imageId}`)"
                       class="text-center mx-auto">
                            ${image.originalName}
                    </a>
                </li>
            </c:forEach>
        </ul>

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

        <!-- 댓글 추가 폼 시작 -->
        <div class="d-flex justify-content-center m-5">
            <form method="post" action="/${boardId}/post/detail/${postId}/comment/add" enctype="multipart/form-data">
                <sec:csrfInput/>
                <div class="input-group">

                    <!-- 이미지 업로드 버튼 -->
                    <button type="button" class="btn btn-outline-secondary" id="uploadCommentImageButton">+</button>
                    <input type="file" name="commentImages" id="commentImageInput" class="d-none" multiple>

                    <!-- 댓글 입력 -->
                    <input type="text"
                           name="commentContent"
                           id="commentContent"
                           class="form-control"
                           placeholder="댓글 입력 . . .">
                    <button type="submit" id="addCommentButton" class="btn btn-outline-dark">입력</button>
                </div>
                <!-- 추가된 이미지 리스트 -->
                <ul id="addedCommentImagesList" class="d-flex flex-column align-items-center"></ul>
            </form>
        </div>
        <!-- 댓글 추가 폼 끝 -->

        <!-- 댓글 리스트 시작 -->
        <div class="container mt-4">
            <ul class="list-group">
                <c:forEach var="comment" items="${commentPagination.postList}">
                    <li class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                            <div class="flex-grow-1">
                                <strong>작성자:</strong> ${comment.userName}
                                <br/>
                                <strong>내용:</strong>
                                <span id="commentContent-${comment.commentId}">${comment.commentContent}</span>
                                <div class="text-end">
                                    <small class="text-muted">작성시간:
                                        <fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                                    </small>
                                    <br/>
                                    <small class="text-muted">수정시간:
                                        <fmt:formatDate value="${comment.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                    </small>
                                </div>
                            </div>
                            <c:if test="${currentUserId == comment.userId}">
                                <div class="ms-3 d-flex align-items-center">
                                    <!-- 수정 버튼 -->
                                    <button class="btn btn-sm btn-outline-primary me-2"
                                            id="commentEditButton"
                                            data-id="${comment.commentId}">수정
                                    </button>
                                    <!-- 삭제 버튼 -->
                                    <form method="post" action="/${boardId}/post/detail/${postId}/comment/delete"
                                          class="m-0">
                                        <sec:csrfInput/>
                                        <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                        <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
                                    </form>
                                </div>
                            </c:if>
                        </div>

                        <!-- 댓글 수정 폼 (초기에는 숨김) -->
                        <div id="editForm-${comment.commentId}" class="mt-3" style="display:none;">
                            <form method="post" action="/${boardId}/post/detail/${postId}/comment/edit">
                                <sec:csrfInput/>
                                <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                <div class="input-group">
                                    <input type="text" class="form-control" name="commentContent"
                                           value="${comment.commentContent}">
                                    <button type="submit" class="btn btn-success">저장</button>
                                    <button type="button" class="btn btn-secondary"
                                            id="commentEditCancelButton"
                                            data-id="${comment.commentId}">취소
                                    </button>
                                </div>
                            </form>
                        </div>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <!-- 댓글 리스트 끝 -->

        <!-- 페이지 버튼 시작-->
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
        <!-- 페이지 버튼 끝-->

        <!-- 목록, 수정, 삭제 버튼 -->
        <div class="d-flex justify-content-center">
            <form method="get" action="/${boardId}/post">
                <input type="hidden" name="search" value="${page.search}">
                <input type="hidden" name="searchType" value="${page.searchType}">
                <input type="hidden" name="sortType" value="${page.sortType}">
                <input type="hidden" name="page" value="${page.page}">
                <button type="submit">목록</button>
            </form>

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
