<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Detail Page | 게시글 조회 </title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/popup.css">

    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">

    <style>
        input[readonly] {
            color: #000000 !important;
            background-color: #ffffff !important;
            opacity: 1 !important;
        }

        textarea[readonly] {
            color: #000000 !important;
            background-color: #ffffff !important;
            opacity: 1 !important;
        }
    </style>

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
                        <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
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
            $('#addedCommentImagesList').on('click', '.remove-added-image-btn', function () {
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
                        <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
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
            $(document).on('click', '.remove-edit-added-image-btn', function () {
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
                        <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
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

<body data-pc-preset="preset-1" data-pc-sidebar-theme="light" data-pc-sidebar-caption="true" data-pc-direction="ltr"
      data-pc-theme="light">

<div class="loader-bg">
    <div class="loader-track">
        <div class="loader-fill"></div>
    </div>
</div>

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

<!-- 사이드 메뉴 시작 -->
<nav class="pc-sidebar pc-sidebar-hide">
    <div class="navbar-wrapper">
        <div class="m-header">
            <a href="${pageContext.request.contextPath}/home" class="b-brand text-primary">
                <img src="${pageContext.request.contextPath}/resources/assets/images/board-logo.png" alt="로고"
                     class="logo" style="max-width: 100%">
            </a>
        </div>
        <div class="navbar-content">
            <ul class="pc-navbar">
                <!-- 홈페이지 이동 -->
                <li class="pc-item pc-caption">
                    <label>홈페이지</label>
                    <i class="ti ti-dashboard"></i>
                </li>
                <li class="pc-item">
                    <a href="${pageContext.request.contextPath}/home" class="pc-link"><span class="pc-micon"><i
                            class="ti ti-dashboard"></i></span><span
                            class="pc-mtext">홈페이지로 이동</span></a>
                </li>

                <!-- 로그인, 로그아웃 / 회원가입 -->
                <li class="pc-item pc-caption">
                    <label>계정</label>
                    <i class="ti ti-news"></i>
                </li>

                <li class="pc-item">
                    <a href="${pageContext.request.contextPath}/user/signup" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-user-plus"></i></span>
                        <span class="pc-mtext">회원가입</span>
                    </a>
                </li>

                <li class="pc-item">
                    <c:if test="${isAuthenticatedUser}">
                        <a href="javascript:void(0);" class="pc-link"
                           onclick="document.getElementById('logout-form').submit(); return false;">
                            <span class="pc-micon"><i class="ti ti-lock"></i></span>
                            <span class="pc-mtext">로그아웃</span>
                        </a>

                        <form id="logout-form" action="${pageContext.request.contextPath}/process-logout" method="post"
                              class="d-none">
                            <sec:csrfInput/>
                            <button type="submit"></button>
                        </form>
                    </c:if>
                </li>

                <!-- 게시판 메뉴 타이틀 -->
                <li class="pc-item pc-caption">
                    <label>게시판</label>
                    <i class="ti ti-brand-chrome"></i>
                </li>

                <!-- 게시판 메뉴 시작 -->
                <li class="pc-item pc-hasmenu">
                    <a href="javascript:void(0);" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-menu"></i></span>
                        <span class="pc-mtext">게시판 선택</span>
                        <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                    </a>
                    <ul class="pc-submenu">
                        <!-- 일반게시판 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a href="javascript:void(0);" class="pc-link">일반 게시판
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                            <!-- 2뎁스 -->
                            <ul class="pc-submenu">
                                <li class="pc-item"><a class="pc-link" href="#!">Level 3.1</a></li>
                                <li class="pc-item"><a class="pc-link" href="#!">Level 3.2</a></li>
                            </ul>
                        </li>

                        <!-- 갤러리 게시판 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a href="javascript:void(0);" class="pc-link">갤러리 게시판
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                            <!-- 2뎁스 -->
                            <ul class="pc-submenu">
                                <li class="pc-item"><a class="pc-link" href="#!">Level 3.1</a></li>
                                <li class="pc-item"><a class="pc-link" href="#!">Level 3.2</a></li>
                            </ul>
                        </li>
                    </ul>
                </li>
                <!-- 게시판 메뉴 끝 -->
            </ul>
        </div>
    </div>
</nav>
<!-- 사이드 메뉴 끝 -->

<!-- 상단 바 시작 -->
<header class="pc-header">
    <div class="header-wrapper">
        <div class="me-auto pc-mob-drp">
            <ul class="list-unstyled">
                <!-- 사이드 바 버튼 시작 -->
                <li class="pc-h-item header-mobile-collapse">
                    <a href="#" class="pc-head-link head-link-secondary ms-0" id="sidebar-hide">
                        <i class="ti ti-menu-2"></i>
                    </a>
                </li>
            </ul>
        </div>

        <div class="ms-auto pc-mob-drp">
            <ul class="list-unstyled">
                <li class="pc-h-item d-none d-md-inline-flex">
                    <div class="m-3 d-flex">
                        <c:if test="${isUser || isAnonymous}">
                            <form method="get" action="/${boardId}/post">
                                <input type="hidden" name="search" value="${page.search}">
                                <input type="hidden" name="searchType" value="${page.searchType}">
                                <input type="hidden" name="sortType" value="${page.sortType}">
                                <input type="hidden" name="page" value="${page.page}">
                                <button type="submit" class="btn btn-light-secondary">목록</button>
                            </form>
                        </c:if>

                        <c:if test="${isAdmin && postDetail.adminId == currentUserId}">
                            <button type="button" class="btn btn-light-secondary"
                                    onclick="location.href='/admin/post/${boardId}'">관리 페이지 돌아가기
                            </button>
                            <button type="button" class="btn btn-light-secondary mx-1"
                                    onclick="location.href='/admin/post/${boardId}/${postDetail.postId}/edit'">수정
                            </button>
                            <form method="post"
                                  action="/admin/post/${boardId}/${postDetail.postId}/delete-admin-post">
                                <sec:csrfInput/>
                                <button type="submit" class="btn btn-light-secondary">삭제</button>
                            </form>
                        </c:if>

                        <c:if test="${postDetail.userId == currentUserId}">
                            <button type="button" class="btn btn-light-secondary mx-1"
                                    onclick="location.href='/${boardId}/post/detail/${postDetail.postId}/edit'">수정
                            </button>
                            <form method="post" action="/${boardId}/post/detail/${postDetail.postId}/delete">
                                <sec:csrfInput/>
                                <button type="submit" class="btn btn-light-secondary">삭제</button>
                            </form>
                        </c:if>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</header>
<!-- 상단 바 끝 -->

<!-- 메인 페이지 시작 -->
<div class="pc-container">
    <div class="pc-content">
        <!-- 게시판 페이지 헤더 -->
        <div class="page-header">
            <div class="page-block">
                <div class="row align-items-center">
                    <div class="col-md-12">
                        <div class="page-header-title">
                            <c:choose>
                                <c:when test="${boardDetail.boardType == 'GALLERY'}">
                                    <h5 class="m-b-10">갤러리 게시판</h5>
                                </c:when>
                                <c:otherwise>
                                    <h5 class="m-b-10">일반 게시판</h5>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <ul class="breadcrumb">
                            <li class="breadcrumb-item">${boardDetail.boardName}</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <!-- 게시판 페이지 헤더 끝 -->

        <div class="row">
            <div class="col-sm-12">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-start">
                        <!-- 좌측 정보 -->
                        <div class="d-flex align-items-center">
                            <h5>게시글 조회</h5>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>작성자:</strong> ${postDetail.userName}</div>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>카테고리:</strong> ${postDetail.categoryName}</div>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>조회수:</strong> ${postDetail.views}</div>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>댓글수:</strong> ${postDetail.commentCount}</div>
                        </div>

                        <!-- 우측 정보 (작성/수정 시간) -->
                        <div class="text-end d-flex">
                            <div><strong>작성:</strong>
                                <span class="text-muted">
                                    <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                            </div>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>수정:</strong><span class="text-muted">
                                <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></span>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="p-4 border rounded shadow-sm">
                            <c:if test="${boardDetail.boardType == 'GALLERY'}">
                                <div>현재 썸네일 이미지</div>
                                <img src="/image/${postDetail.imageId}" id="imagePreview"
                                     style="height: 30%; width: 30%;" alt="${postDetail.title}">
                            </c:if>

                            <div class="mb-3">
                                <label for="title" class="form-label hljs-strong">제목</label>
                                <input type="text"
                                       class="form-control"
                                       name="title"
                                       value="${postDetail.title}"
                                       placeholder="제목"
                                       id="title" readonly/>
                            </div>

                            <div class="mb-3">
                                <label for="content" class="form-label hljs-strong">내용</label>
                                <textarea class="form-control"
                                          name="content"
                                          placeholder="내용"
                                          id="content"
                                          rows="10" readonly>${postDetail.content}</textarea>
                            </div>

                            <c:choose>
                                <c:when test="${boardDetail.imageFlag == 'Y'}">
                                    <!-- 이미지 목록 -->
                                    <div class="mt-2">
                                        <strong>첨부 이미지</strong>
                                        <ul class="list-group">
                                            <c:forEach var="image" items="${images}">
                                                <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
                                                    <a href="javascript:showImage('/image/${image.imageId}')"
                                                       class="text-center mx-auto"
                                                       style="font-size: 1rem">${image.originalName}</a>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </div>

                                    <!-- 팝업 오버레이 및 팝업 내용 -->
                                    <div id="popupOverlay" onclick="closePopup()">
                                        <div id="popup" onclick="stopPropagation()">
                                            <img id="popupImage" src="" alt="이미지"/>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p>이미지 업로드가 허용되지 않는 게시판입니다.</p>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <!-- 좋아요/싫어요 버튼-->
                        <div class="text-center m-3">
                            <button class="mx-2 btn ${postDetail.hasLiked ? 'btn-primary' : 'btn-outline-primary'}"
                                    type="button" id="postLikeButton"
                                    data-post-id="${postDetail.postId}">
                                <i class="ti ti-thumb-up"></i>
                                <span id="likeCount">${postDetail.likeCount}</span>
                            </button>

                            <button class="mx-2 btn ${postDetail.hasDisliked ? 'btn-secondary' : 'btn-outline-secondary'}"
                                    type="button" id="postDislikeButton"
                                    data-post-id="${postDetail.postId}">
                                <i class="ti ti-thumb-down"></i>
                                <span id="dislikeCount">${postDetail.dislikeCount}</span>
                            </button>
                        </div>
                    </div>
                    <!-- 게시글 내용 끝 -->
                </div>
                <div class="card">
                    <div class="card-header">
                        <!-- 댓글 추가 폼 시작 -->
                        <div class="d-flex justify-content-center m-3">
                            <form method="post" action="/${boardId}/post/detail/${postId}/comment/add"
                                  enctype="multipart/form-data" class="w-100">
                                <sec:csrfInput/>
                                <!-- 댓글 입력 창 -->
                                <div class="mb-3">
                                    <textarea class="form-control"
                                              name="commentContent"
                                              aria-label="댓글"
                                              placeholder="댓글 입력 . . ."
                                              id="commentContent"
                                              rows="5"></textarea>
                                </div>
                                <!-- 이미지 업로드 버튼과 댓글 작성 버튼 -->
                                <div class="d-flex justify-content-end">
                                    <button type="button" class="btn btn-light-secondary me-2"
                                            id="uploadCommentImageButton">이미지 추가
                                    </button>

                                    <input type="file" name="commentImages" id="commentImageInput" class="d-none"
                                           multiple>

                                    <button type="submit" id="addCommentButton" class="btn btn-light-secondary">댓글 추가
                                    </button>
                                </div>

                                <!-- 추가된 이미지 리스트 -->
                                <ul id="addedCommentImagesList" class="list-group m-1"></ul>
                            </form>
                        </div>
                        <!-- 댓글 추가 폼 끝 -->
                    </div>

                    <!-- 댓글 시작 -->
                    <div class="card-body">
                        <!-- 댓글 리스트 시작 -->
                        <div class="container mt-4" style="max-width: 90%">
                            <ul class="list-group">
                                <c:forEach var="comment" items="${commentPagination.postList}">
                                    <c:if test="${comment.parentId == null}">
                                        <li class="list-group-item position-relative">
                                            <c:if test="${comment.adoptedFlag == 'Y'}">
                                                <i class="ti ti-circle-check position-absolute"
                                                   style="font-size: 2rem; color: #0BA969; left: -3rem; top: 0;"></i>
                                            </c:if>
                                            <!-- 댓글 상단: 작성자, 작성 시간, 수정 시간, 좋아요/싫어요 버튼 -->
                                            <div class="d-flex align-items-start position-relative">
                                                <div class="me-3 position-absolute top-0 start-0">
                                                    <strong>${comment.userName}</strong>
                                                    <div class="text-muted mt-2">
                                                        <small>작성시간: <fmt:formatDate value="${comment.createdAt}"
                                                                                     pattern="yyyy-MM-dd HH:mm"/></small><br/>
                                                        <small>수정시간: <fmt:formatDate value="${comment.updatedAt}"
                                                                                     pattern="yyyy-MM-dd HH:mm"/></small>
                                                    </div>
                                                </div>
                                                <div class="flex-grow-1 text-center m-4" style="font-size: 1rem;">
                                                        ${comment.commentContent}
                                                </div>
                                                <div class="flex-column align-items-end position-absolute top-0 end-0">
                                                    <button class="btn btn-sm mx-1 ${comment.hasLiked ? 'btn-primary' : 'btn-outline-primary'} commentLikeButton"
                                                            id="commentLikeButton-${comment.commentId}" type="button"
                                                            data-comment-id="${comment.commentId}">
                                                        <i class="ti ti-thumb-up"></i><span
                                                            id="commentLikeCount-${comment.commentId}">${comment.likeCount}</span>
                                                    </button>
                                                    <button class="btn btn-sm ${comment.hasDisliked ? 'btn-secondary' : 'btn-outline-secondary'} commentDislikeButton"
                                                            id="commentDislikeButton-${comment.commentId}" type="button"
                                                            data-comment-id="${comment.commentId}">
                                                        <i class="ti ti-thumb-down"></i><span
                                                            id="commentDislikeCount-${comment.commentId}">${comment.dislikeCount}</span>
                                                    </button>
                                                </div>
                                            </div>

                                            <!-- 댓글 하단: 첨부 이미지, 수정/삭제 버튼, 대댓글 보기, 답글 달기 -->
                                            <div class="mt-3 position-relative">
                                                <!-- 첨부 이미지 -->
                                                <c:if test="${not empty comment.commentImages}">
                                                    <div class="position-absolute top-0 start-0">
                                                        <strong>[첨부 이미지]</strong>
                                                        <c:forEach var="image" items="${comment.commentImages}"
                                                                   varStatus="status">
                                                            <a href="javascript:showImage('/image/${image.imageId}')">[${status.index + 1}]</a>
                                                        </c:forEach>
                                                    </div>
                                                </c:if>

                                                <!-- 대댓글 보기 및 답글 달기 버튼 -->
                                                <div class="text-center">
                                                    <button class="btn btn-sm btn-outline-secondary toggleReplyButton"
                                                            data-id="${comment.commentId}">
                                                        대댓글 보기
                                                    </button>
                                                    <button class="btn btn-sm btn-outline-secondary showReplyFormButton"
                                                            data-id="${comment.commentId}">
                                                        답글 달기
                                                    </button>
                                                </div>

                                                <!-- 수정/삭제 버튼 -->
                                                <div class="position-absolute bottom-0 end-0">
                                                    <c:if test="${currentUserId == comment.userId}">
                                                        <button class="btn btn-sm btn-outline-primary commentEditButton"
                                                                data-id="${comment.commentId}">수정
                                                        </button>
                                                        <form method="post"
                                                              action="/${boardId}/post/detail/${postId}/comment/delete"
                                                              class="m-0 d-inline">
                                                            <sec:csrfInput/>
                                                            <input type="hidden" name="commentId"
                                                                   value="${comment.commentId}"/>
                                                            <button type="submit" class="btn btn-sm btn-outline-danger">
                                                                삭제
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${currentUserId != comment.userId && comment.parentId == null && currentUserId == postDetail.userId}">
                                                        <form method="post"
                                                              action="/${boardId}/post/detail/${postId}/comment/adopt"
                                                              class="m-0 d-inline">
                                                            <sec:csrfInput/>
                                                            <input type="hidden" name="commentId"
                                                                   value="${comment.commentId}"/>
                                                            <button type="submit"
                                                                    class="btn btn-sm btn-outline-success">
                                                                채택
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </div>
                                            </div>

                                            <!-- 대댓글 작성 폼 -->
                                            <div class="mt-3">
                                                <form method="post"
                                                      action="/${boardId}/post/detail/${postId}/comment/add"
                                                      enctype="multipart/form-data" id="replyForm-${comment.commentId}"
                                                      class="mt-2 replyForm" style="display:none;">
                                                    <sec:csrfInput/>
                                                    <input type="hidden" name="depth" value="0"/>
                                                    <input type="hidden" name="parentId" value="${comment.commentId}"/>
                                                    <div class="input-group">
                                                        <button type="button"
                                                                class="btn btn-outline-secondary addReplyImageButton"
                                                                data-reply-id="${comment.commentId}">+
                                                        </button>
                                                        <input type="file" name="commentImages"
                                                               id="replyImageInput-${comment.commentId}" class="d-none"
                                                               multiple>
                                                        <input type="text" class="form-control" name="commentContent"
                                                               placeholder="답글 입력 . . .">
                                                        <button type="submit" class="btn btn-outline-dark">입력</button>
                                                    </div>
                                                    <ul id="addedReplyImagesList-${comment.commentId}"
                                                        class="list-group">
                                                    </ul>
                                                </form>
                                            </div>

                                            <!-- 댓글 수정 폼 (초기에는 숨김) -->
                                            <div id="editForm-${comment.commentId}" class="mt-3" style="display:none;">
                                                <form method="post"
                                                      action="/${boardId}/post/detail/${postId}/comment/edit"
                                                      enctype="multipart/form-data">
                                                    <sec:csrfInput/>
                                                    <input type="hidden" name="commentId" value="${comment.commentId}"/>
                                                    <input type="hidden"
                                                           id="removedCommentImagesInput-${comment.commentId}"
                                                           name="removedCommentImageIds" value="">
                                                    <div class="input-group">
                                                        <button type="button"
                                                                class="btn btn-outline-dark addCommentImageButton"
                                                                data-comment-id="${comment.commentId}">+
                                                        </button>
                                                        <input type="file" name="addedCommentImages"
                                                               id="addedCommentImageInput-${comment.commentId}"
                                                               class="d-none" multiple>
                                                        <input type="text" class="form-control" name="commentContent"
                                                               value="${comment.commentContent}">
                                                        <button type="submit" class="btn btn-outline-dark">저장
                                                        </button>
                                                        <button type="button"
                                                                class="btn btn-outline-dark commentEditCancelButton"
                                                                data-id="${comment.commentId}">취소
                                                        </button>
                                                    </div>
                                                    <!-- 기존 이미지 목록 -->
                                                    <div class="mt-2">
                                                        <strong>기존 첨부 이미지</strong>
                                                        <ul id="existingCommentImagesList-${comment.commentId}"
                                                            class="list-group">
                                                            <c:forEach var="image" items="${comment.commentImages}">
                                                                <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
                                                                    <a href="javascript:showImage('/image/${image.imageId}')"
                                                                       class="text-center mx-auto">${image.originalName}</a>
                                                                    <button type="button"
                                                                            class="btn btn-danger btn-sm remove-existing-comment-image-btn"
                                                                            data-comment-id="${comment.commentId}"
                                                                            data-id="${image.imageId}">X
                                                                    </button>
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </div>
                                                    <!-- 추가된 이미지 목록 -->
                                                    <div class="mt-2">
                                                        <strong>추가된 첨부 이미지</strong>
                                                        <ul id="addedCommentImagesList-${comment.commentId}"
                                                            class="list-group"></ul>
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
                        <!-- 댓글 리스트 끝 -->
                    </div>
                    <!-- 댓글 끝 -->

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
                </div>
            </div>
        </div>
        <!-- 게시글 내용 끝 -->
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
