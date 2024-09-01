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

    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            // 최초 작성 댓글 이미지
            let commentImages = [];

            // 최초 작성 대댓글 이미지
            const replyImages = {};

            // 댓글(대댓글) 추가 및 삭제 이미지
            const addedCommentImages = {};
            const removedCommentImageIds = {};

            // 이미지 업로드 버튼 (+) 클릭 시 input 활성화
            $('#uploadCommentImageButton').click(function () {
                $('#commentImageInput').click();
            });

            // [댓글 추가] : 최초 작성 댓글 추가 시 리스트 생성, 댓글 이미지 배열에 저장
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

            // [댓글 추가] : 추가된 댓글 이미지 삭제(리스트 삭제, 이미지 배열 삭제)
            $('.remove-added-image-btn').on('click', function () {
                const index = $(this).closest('li').index();

                commentImages.splice(index, 1);
                $(this).closest('li').remove();
                updateAddedImagesInput();
            });

            // [댓글 수정] 댓글 수정 버튼 클릭 시
            $('.commentEditButton').on('click', function () {
                const commentId = $(this).data('id');

                $('#commentContent-' + commentId).hide();
                $('#editForm-' + commentId).show();
                $('#addedCommentImageInput-' + commentId).val('');

                addedCommentImages[commentId] = [];
                removedCommentImageIds[commentId] = [];
            });

            // [댓글 수정] 댓글 수정 취소 버튼 클릭 시
            $('.commentEditCancelButton').on('click', function () {
                const commentId = $(this).data('id');

                $('#editForm-' + commentId).hide();
                $('#commentContent-' + commentId).show();
                $('#addedCommentImageInput-' + commentId).val('');

                addedCommentImages[commentId] = [];
                removedCommentImageIds[commentId] = [];
            });

            // [댓글 수정] 댓글 이미지 추가 버튼 클릭 시
            $('.addCommentImageButton').click(function () {
                const commentId = $(this).data('comment-id');
                $('#addedCommentImageInput-' + commentId).click();
            });

            // [댓글 수정] : 이미 작성된 댓글(대댓글) 이미지 수정 시 리스트 생성, 추가된 이미지 배열 추가
            $('input[id^="addedCommentImageInput-"]').on('change', function () {
                const commentId = $(this).attr('id').split('-')[1];
                const files = Array.from(this.files);

                addedCommentImages[commentId] = files;

                files.forEach(file => {
                    const imageUrl = URL.createObjectURL(file);

                    $('#addedCommentImagesList-' + commentId).append(`
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-center mx-auto">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm remove-edit-added-image-btn ml-auto" data-comment-id="\${commentId}">X</button>
                        </li>
                    `);
                });

                updateEditAddedImagesInput(commentId);
            });

            // [댓글 수정] 댓글(대댓글) 기존 이미지 삭제 버튼 시
            $('.remove-existing-comment-image-btn').on('click', function () {
                const imageId = $(this).data('id');
                const commentId = $(this).data('comment-id');

                removedCommentImageIds[commentId].push(imageId);
                $(this).closest('li').remove();
                $('#removedCommentImagesInput-' + commentId).val(removedCommentImageIds[commentId].join(','));
            });

            // [댓글 수정] 수정 시 추가한 이미지 삭제 버튼 클릭 시
            $('.remove-edit-added-image-btn').on('click', function () {
                const commentId = $(this).data('comment-id');
                const index = $(this).closest('li').index();

                if (index >= 0 && index < addedCommentImages[commentId].length) {
                    addedCommentImages[commentId].splice(index, 1);
                    $(this).closest('li').remove();
                    updateEditAddedImagesInput(commentId);
                }
            });

            // [대댓글] 대댓글 보기
            $('.toggleReplyButton').on('click', function () {
                const replyList = $('#replyList-' + $(this).data('id'));
                const isVisible = replyList.is(':visible');
                replyList.toggle();
                $(this).text(isVisible ? '대댓글 보기' : '대댓글 숨기기');
            });

            // [대댓글 추가] 대댓글 작성 폼
            $('.showReplyFormButton').on('click', function () {
                const replyForm = $('#replyForm-' + $(this).data('id'));
                replyForm.toggle();
            });

            // [대댓글 추가] 답글 이미지 추가 버튼 클릭 시
            $('.addReplyImageButton').click(function () {
                const replyId = $(this).data('reply-id');
                $('#replyImageInput-' + replyId).click();
            });

            // [대댓글 추가] 최초 작성 대댓글 추가 시 리스트 생성, 대댓글 이미지 배열에 저장
            $(document).on('change', 'input[id^="replyImageInput-"]', function () {
                const replyId = $(this).attr('id').split('-')[1];
                const files = Array.from(this.files);

                replyImages[replyId] = files;

                files.forEach(file => {
                    const imageUrl = URL.createObjectURL(file);

                    $('#addedReplyImagesList-' + replyId).append(`
                        <li class="list-group-item d-flex justify-content-between align-items-center">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-center mx-auto">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm remove-reply-added-image-btn ml-auto" data-reply-id="\${replyId}">X</button>
                        </li>
                    `);
                });
                updateReplyAddedImagesInput(replyId);
            });

            // [대댓글 추가] 추가된 대댓글 이미지 삭제버튼 클릭 시
            $(document).on('click', '.remove-reply-added-image-btn', function () {
                const replyId = $(this).data('reply-id');
                const index = $(this).closest('li').index();

                replyImages[replyId].splice(index, 1);
                $(this).closest('li').remove();

                updateReplyAddedImagesInput(replyId);
            });

            // [저장] 댓글(대댓글) 추가 버튼 클릭 시 댓글 내용 검증
            $('#addCommentButton').click(function (e) {
                const commentContent = $('#commentContent').val();

                if (!commentContent) {
                    e.preventDefault();
                    alertMessage(e, "댓글을 등록하려면 내용을 입력하세요.");
                } else if (commentContent.length < 1 || commentContent.length > 200) {
                    e.preventDefault();
                    alertMessage(e, "댓글은 최소 1글자 이상, 200자 미만이어야 합니다.");
                }
            });

            // 메세지 출력
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            // [댓글 추가] 최초 댓글 추가 시 추가된 댓글 이미지 Input 에 업데이트
            function updateAddedImagesInput() {
                let dataTransfer = new DataTransfer();
                commentImages.forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#commentImageInput')[0].files = dataTransfer.files;
            }

            // [댓글(대댓글) 수정] 기존 추가 된 댓글 수정 시 추가된 댓글 이미지 Input 에 업데이트
            function updateEditAddedImagesInput(commentId) {
                let dataTransfer = new DataTransfer();
                addedCommentImages[commentId].forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#addedCommentImageInput-' + commentId)[0].files = dataTransfer.files;
            }

            // [대댓글 추가] 최초 대댓글 추가 시 추가된 대댓글 이미지 Input 에 업데이트
            function updateReplyAddedImagesInput(replyId) {
                let dataTransfer = new DataTransfer();
                replyImages[replyId].forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#replyImageInput-' + replyId)[0].files = dataTransfer.files;
            }
        });
    </script>

    <script type="text/javascript">
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

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            $('#postLikeButton').click(function () {
                handleLikeDislike($(this).data('post-id'), 'like');
            });

            $('#postDislikeButton').click(function () {
                handleLikeDislike($(this).data('post-id'), 'dislike');
            });

            // 댓글 좋아요 버튼 클릭 시
            $(document).on('click', '.commentLikeButton', function () {
                const commentId = $(this).data('comment-id');
                handleCommentLikeDislike(commentId, 'comment-like');
            });

            // 댓글 싫어요 버튼 클릭 시
            $(document).on('click', '.commentDislikeButton', function () {
                const commentId = $(this).data('comment-id');
                handleCommentLikeDislike(commentId, 'comment-dislike');
            });

            $.ajaxSetup({
                beforeSend: function (xhr) {
                    xhr.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
                }
            });

            function handleLikeDislike(postId, action) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/${boardDetail.boardId}/post/detail/${postDetail.postId}/' + action,
                    method: 'POST',
                    success: function (response) {
                        if (response) {
                            updatePostLikeButtonState(response);
                        }
                    },
                    error: function (error) {
                        console.error('요청 실패:', error);
                        alert("잘못된 요청입니다.");
                    }
                });
            }

            function handleCommentLikeDislike(commentId, action) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/${boardDetail.boardId}/post/detail/${postDetail.postId}/' + commentId + '/' + action,
                    method: 'POST',
                    success: function (response) {
                        updateCommentButtonState(commentId, response);
                    },
                    error: function (error) {
                        console.error('요청 실패:', error);
                        alert("잘못된 요청입니다.");
                    }
                });
            }

            // 게시글 좋아요/싫어요 버튼 상태 업데이트
            function updatePostLikeButtonState(response) {
                // 게시글 좋아요 버튼 상태 업데이트
                $('#postLikeButton').toggleClass('btn-primary', response.hasLiked)
                    .toggleClass('btn-outline-primary', !response.hasLiked);

                // 게시글 싫어요 버튼 상태 업데이트
                $('#postDislikeButton').toggleClass('btn-secondary', response.hasDisliked)
                    .toggleClass('btn-outline-secondary', !response.hasDisliked);

                // 게시글 좋아요, 싫어요 수 업데이트
                $('#likeCount').text(response.likeCount);
                $('#dislikeCount').text(response.dislikeCount);
            }

            // 댓글 좋아요/싫어요 버튼 상태 업데이트
            function updateCommentButtonState(commentId, response) {
                // 댓글 좋아요 버튼 상태 업데이트
                $('#commentLikeButton-' + commentId).toggleClass('btn-primary', response.commentHasLiked)
                    .toggleClass('btn-outline-primary', !response.commentHasLiked);

                // 댓글 싫어요 버튼 상태 업데이트
                $('#commentDislikeButton-' + commentId).toggleClass('btn-secondary', response.commentHasDisliked)
                    .toggleClass('btn-outline-secondary', !response.commentHasDisliked);

                // 댓글 좋아요, 싫어요 수 업데이트
                $('#commentLikeCount-' + commentId).text(response.commentLikeCount);
                $('#commentDislikeCount-' + commentId).text(response.commentDislikeCount);
            }
        });
    </script>
</head>
<body>

<%
    // 현재 로그인 한 유저의 값
    String currentUserId = UserUtil.getCurrentLoginUserId();
    pageContext.setAttribute("currentUserId", currentUserId);

    boolean isAdmin = UserUtil.isAdmin();
    boolean isUser = UserUtil.isUser();
    boolean isAnonymous = UserUtil.isAnonymous();

    pageContext.setAttribute("isAdmin", isAdmin);
    pageContext.setAttribute("isUser", isUser);
    pageContext.setAttribute("isAnonymous", isAnonymous);
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

        <div>작성자 :
            <c:choose>
                <c:when test="${postDetail.adminId != null}">
                    <td>관리자</td>
                </c:when>
                <c:otherwise>
                    <td>${postDetail.userName}</td>
                </c:otherwise>
            </c:choose>
        </div>
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

        <div>작성 시간 : <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></div>
        <div>수정 시간 : <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></div>
        <div>조회수 : ${postDetail.views}</div>
        <div>댓글수 : ${postDetail.commentCount}</div>

        <button class="btn ${postDetail.hasLiked ? 'btn-primary' : 'btn-outline-primary'}"
                type="button" id="postLikeButton"
                data-post-id="${postDetail.postId}">좋아요 <span id="likeCount">${postDetail.likeCount}</span>
        </button>

        <button class="btn ${postDetail.hasDisliked ? 'btn-secondary' : 'btn-outline-secondary'}"
                type="button" id="postDislikeButton"
                data-post-id="${postDetail.postId}">
            싫어요 <span id="dislikeCount">${postDetail.dislikeCount}</span>
        </button>

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
                    <c:if test="${comment.parentId == null}">
                        <li class="list-group-item">
                            <c:if test="${comment.adoptedFlag == 'Y'}">
                                채택 댓글
                            </c:if>
                            <!-- 댓글 상단: 작성자, 내용, 수정/삭제 버튼 -->
                            <div class="d-flex justify-content-between">
                                <div class="comment-author">
                                    <strong>${comment.userName}</strong>
                                </div>
                                <div class="flex-grow-1 text-center">
                                        ${comment.commentContent}
                                </div>

                                <!-- 수정/삭제 버튼 -->
                                <div class="ms-3 d-flex align-items-center">
                                    <c:if test="${currentUserId == comment.userId}">
                                        <button class="btn btn-sm btn-outline-primary me-2 commentEditButton"
                                                data-id="${comment.commentId}">수정
                                        </button>
                                        <form method="post" action="/${boardId}/post/detail/${postId}/comment/delete"
                                              class="m-0">
                                            <sec:csrfInput/>
                                            <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                            <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${currentUserId != comment.userId && comment.parentId == null && currentUserId == postDetail.userId}">
                                        <form method="post" action="/${boardId}/post/detail/${postId}/comment/adopt"
                                              class="m-0">
                                            <sec:csrfInput/>
                                            <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                            <button type="submit" class="btn btn-sm btn-outline-dark me-2">채택</button>
                                        </form>
                                    </c:if>
                                </div>

                                <button
                                        class="btn me-2 commentLikeButton ${comment.hasLiked ? 'btn-primary' : 'btn-outline-primary'}"
                                        id="commentLikeButton-${comment.commentId}"
                                        type="button" data-comment-id="${comment.commentId}">
                                    좋아요 <span id="commentLikeCount-${comment.commentId}">${comment.likeCount}</span>
                                </button>
                                <button class="btn commentDislikeButton ${comment.hasDisliked ? 'btn-secondary' : 'btn-outline-secondary'}"
                                        id="commentDislikeButton-${comment.commentId}"
                                        type="button" data-comment-id="${comment.commentId}">
                                    싫어요 <span
                                        id="commentDislikeCount-${comment.commentId}">${comment.dislikeCount}</span>
                                </button>
                            </div>

                            <!-- 댓글 하단: 첨부 이미지, 작성 시간, 수정 시간 -->
                            <div class="d-flex justify-content-between mt-2">
                                <!-- 첨부 이미지 -->
                                <div class="comment-images">
                                    <c:if test="${not empty comment.commentImages}">
                                        <strong>첨부 이미지:</strong>
                                        <c:forEach var="image" items="${comment.commentImages}">
                                            <a href="javascript:showImage('/image/${image.imageId}')">
                                                [${image.originalName}]
                                            </a>
                                        </c:forEach>
                                    </c:if>
                                </div>

                                <!-- 작성 시간 및 수정 시간 -->
                                <div class="text-end text-muted">
                                    <small>작성시간:
                                        <fmt:formatDate value="${comment.createdAt}"
                                                        pattern="yyyy-MM-dd HH:mm"/></small>
                                    <br/>
                                    <small>수정시간:
                                        <fmt:formatDate value="${comment.updatedAt}"
                                                        pattern="yyyy-MM-dd HH:mm"/></small>
                                </div>
                            </div>

                            <!-- 대댓글 토글 버튼 및 대댓글 리스트 -->
                            <div class="mt-3">
                                <button class="btn btn-sm btn-outline-secondary toggleReplyButton"
                                        data-id="${comment.commentId}">
                                    대댓글 보기
                                </button>
                            </div>

                            <!-- 대댓글 작성 폼 -->
                            <div class="mt-3">
                                <button class="btn btn-sm btn-outline-secondary showReplyFormButton"
                                        data-id="${comment.commentId}">
                                    답글 달기
                                </button>
                                <form method="post" action="/${boardId}/post/detail/${postId}/comment/add"
                                      enctype="multipart/form-data"
                                      id="replyForm-${comment.commentId}"
                                      class="mt-2 replyForm" style="display:none;">
                                    <sec:csrfInput/>
                                    <input type="hidden" name="depth" value="0"/>
                                    <input type="hidden" name="parentId" value="${comment.commentId}"/>
                                    <div class="input-group">
                                        <!-- 이미지 업로드 버튼 -->
                                        <button type="button" class="btn btn-outline-secondary addReplyImageButton"
                                                data-reply-id="${comment.commentId}">+
                                        </button>
                                        <input type="file" name="commentImages"
                                               id="replyImageInput-${comment.commentId}"
                                               class="d-none"
                                               multiple>
                                        <input type="text" class="form-control"
                                               name="commentContent"
                                               placeholder="답글 입력 . . .">
                                        <button type="submit" class="btn btn-outline-dark">입력</button>
                                    </div>
                                    <!-- 추가된 이미지 리스트 -->
                                    <ul id="addedReplyImagesList-${comment.commentId}"
                                        class="d-flex flex-column align-items-center"></ul>
                                </form>
                            </div>

                            <!-- 댓글 수정 폼 (초기에는 숨김) -->
                            <div id="editForm-${comment.commentId}" class="mt-3" style="display:none;">
                                <form method="post" action="/${boardId}/post/detail/${postId}/comment/edit"
                                      enctype="multipart/form-data">
                                    <sec:csrfInput/>
                                    <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                    <input type="hidden" id="removedCommentImagesInput-${comment.commentId}"
                                           name="removedCommentImageIds"
                                           value="">
                                    <div class="input-group">
                                        <!-- 이미지 업로드 버튼 -->
                                        <button type="button" class="btn btn-outline-secondary addCommentImageButton"
                                                data-comment-id="${comment.commentId}">
                                            +
                                        </button>
                                        <input type="file" name="addedCommentImages"
                                               id="addedCommentImageInput-${comment.commentId}" class="d-none"
                                               multiple>
                                        <input type="text" class="form-control" name="commentContent"
                                               value="${comment.commentContent}">
                                        <button type="submit" class="btn btn-success">저장</button>
                                        <button type="button" class="btn btn-secondary commentEditCancelButton"
                                                data-id="${comment.commentId}">취소
                                        </button>
                                    </div>

                                    <!-- 기존 이미지 목록 -->
                                    <div class="mt-2">
                                        <strong>기존 첨부 이미지:</strong>
                                        <ul id="existingCommentImagesList-${comment.commentId}" class="list-group">
                                            <c:forEach var="image" items="${comment.commentImages}">
                                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                                    <a href="javascript:showImage('/image/${image.imageId}')"
                                                       class="text-center mx-auto">
                                                            ${image.originalName}
                                                    </a>
                                                    <button type="button"
                                                            class="btn btn-danger btn-sm remove-existing-comment-image-btn"
                                                            data-comment-id="${comment.commentId}"
                                                            data-id="${image.imageId}">
                                                        X
                                                    </button>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>

                                    <!-- 추가된 이미지 목록 -->
                                    <div class="mt-2">
                                        <strong>추가된 첨부 이미지:</strong>
                                        <ul id="addedCommentImagesList-${comment.commentId}" class="list-group"></ul>
                                    </div>
                                </form>
                            </div>
                        </li>
                    </c:if>

                    <div id="replyList-${comment.commentId}" style="display: none;">
                        <ul class="list-group">
                            <c:forEach var="reply" items="${comment.replies}">
                                <c:set var="comment" value="${reply}" scope="request"/>
                                <c:set var="currentUserId" value="${currentUserId}" scope="request"/>
                                <c:set var="boardId" value="${boardId}" scope="request"/>
                                <c:set var="postId" value="${postId}" scope="request"/>
                                <jsp:include page="commentReply.jsp"/>
                            </c:forEach>
                        </ul>
                    </div>
                </c:forEach>
            </ul>
        </div>

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
            <c:if test="${isUser || isAnonymous}">
                <form method="get" action="/${boardId}/post">
                    <input type="hidden" name="search" value="${page.search}">
                    <input type="hidden" name="searchType" value="${page.searchType}">
                    <input type="hidden" name="sortType" value="${page.sortType}">
                    <input type="hidden" name="page" value="${page.page}">
                    <button type="submit">목록</button>
                </form>
            </c:if>

            <c:if test="${isAdmin && postDetail.adminId == currentUserId}">
                <button onclick="location.href='/admin/post/${boardId}'">관리 페이지 돌아가기</button>
                <button type="button"
                        onclick="location.href='/admin/post/${boardId}/${postDetail.postId}/edit'">
                    수정
                </button>
                <form method="post"
                      action="/admin/post/${boardId}/${postDetail.postId}/delete-admin-post">
                    <sec:csrfInput/>
                    <button type="submit">삭제</button>
                </form>
            </c:if>

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
