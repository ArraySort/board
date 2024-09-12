<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Admin Management Page | 게시글 추가 </title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/popup.css">

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            let removedImageIds = [];       // 기존 이미지에서 삭제된 이미지 ID : String
            let addedImages = [];       // 새로 추가된 이미지 : MultipartFile

            // 업로드 이미지 미리보기
            $('#thumbnailImage').on('change', function () {
                const file = this.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        $('#imagePreview').attr('src', e.target.result).show();
                    };
                    reader.readAsDataURL(file);
                } else {
                    $('#imagePreview').attr('src', '/image/${postDetail.imageId}').show();
                }
            });

            // 기존 이미지 삭제
            $(document).on('click', '.remove-image-btn', function () {
                const imageId = $(this).data('image-id');
                removedImageIds.push(imageId);
                $(this).closest('li').remove();
                $('#removedImagesInput').val(removedImageIds);
            });

            // 저장 버튼 눌렀을 때 검증
            $('#saveButton').on('click', function (e) {
                validateForm(e);
            });

            // 새로운 이미지 추가
            $('#imageInput').on('change', function () {
                const files = this.files;

                addedImages = [];

                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const imageUrl = URL.createObjectURL(file);

                    addedImages.push(file);

                    $('#addedImagesList').append(`
                        <li class="list-group-item d-flex align-items-center rounded-3 p-2 shadow-sm w-100" style="max-width: 50%">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-center mx-auto">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm ml-auto m-1 remove-added-image-btn">X</button>
                        </li>
                    `);
                }

                updateAddedImagesInput();
            });


            // 추가된 이미지 삭제
            $(document).on('click', '.remove-added-image-btn', function () {
                const index = $(this).closest('li').index();

                addedImages.splice(index, 1);
                $(this).closest('li').remove();

                updateAddedImagesInput();
            });

            // 이미지 추가 버튼 클릭 시 파일 입력 필드 클릭
            $('#addImageBtn').on('click', function () {
                $('#imageInput').click();
            });

            function updateAddedImagesInput() {
                let dataTransfer = new DataTransfer();
                addedImages.forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#imageInput')[0].files = dataTransfer.files;
            }

        });

        // 메세지 출력
        function alertMessage(e, message) {
            e.preventDefault();
            alert(message);
        }

        // 입력 폼 검증
        function validateForm(e) {
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

        // 이미지 팝업
        function showImage(imageUrl) {
            const popupOverlay = document.getElementById('popupOverlay');
            const popupImage = document.getElementById('popupImage');
            popupImage.src = imageUrl;
            popupOverlay.style.display = 'flex';
        }

        // 팝업 닫기
        function closePopup() {
            const popupOverlay = document.getElementById('popupOverlay');
            popupOverlay.style.display = 'none';
        }

        // 팝업에 대한 폼 키보드 제어
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                closePopup();
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
    // 로그인 한 유저인지 확인하는 값
    boolean isAuthenticatedUser = UserUtil.isAuthenticatedUser();
    pageContext.setAttribute("isAuthenticatedUser", isAuthenticatedUser);
%>
<!-- 사이드 메뉴 시작 -->
<nav class="pc-sidebar">
    <div class="navbar-wrapper">
        <!-- 사이드 바 로고 -->
        <div class="m-header">
            <a href="${pageContext.request.contextPath}/home" class="b-brand text-primary">
                <img src="${pageContext.request.contextPath}/resources/assets/images/board-logo.png" alt="로고"
                     class="logo" style="max-width: 100%">
            </a>
        </div>

        <!-- 사이드 바 컨텐츠 -->
        <div class="navbar-content">
            <ul class="pc-navbar">
                <!-- 홈페이지 이동 -->
                <li class="pc-item pc-caption">
                    <label>홈페이지</label>
                </li>
                <li class="pc-item">
                    <a href="${pageContext.request.contextPath}/admin" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-home"></i></span>
                        <span class="pc-mtext">관리자 홈으로 이동</span></a>
                </li>

                <!-- 로그인, 로그아웃 / 회원가입 -->
                <li class="pc-item pc-caption">
                    <label>계정</label>
                </li>

                <li class="pc-item">
                    <c:if test="${isAuthenticatedUser}">
                        <a href="javascript:void(0);" class="pc-link"
                           onclick="document.getElementById('admin-logout-form').submit(); return false;">
                            <span class="pc-micon"><i class="ti ti-lock"></i></span>
                            <span class="pc-mtext">로그아웃</span>
                        </a>

                        <form id="admin-logout-form" action="${pageContext.request.contextPath}/admin/process-logout"
                              method="post" class="d-none"><sec:csrfInput/>
                        </form>
                    </c:if>
                </li>

                <!-- 게시판 메뉴 타이틀 -->
                <li class="pc-item pc-caption">
                    <label>메뉴</label>
                </li>

                <!-- 게시판 메뉴 시작 -->
                <li class="pc-item pc-hasmenu">
                    <a href="javascript:void(0);" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-menu"></i></span>
                        <span class="pc-mtext">관리 메뉴 선택</span>
                        <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                    </a>

                    <ul class="pc-submenu">
                        <!-- 회원관리 : 1뎁스 -->
                        <li class="pc-item">
                            <a class="pc-link" href="${pageContext.request.contextPath}/admin/user">회원 관리
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                        </li>
                        <!-- 게시판 관리 : 1뎁스 -->
                        <li class="pc-item">
                            <a class="pc-link" href="${pageContext.request.contextPath}/admin/board">게시판 관리
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                        </li>
                        <!-- 게시판 관리 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a class="pc-link" href="${pageContext.request.contextPath}/admin/post/1">게시글 관리
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                        </li>
                        <!-- 게시판 관리 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a class="pc-link" href="${pageContext.request.contextPath}/admin/comment">댓글 관리
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                        </li>
                        <!-- 댓글 관리 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a class="pc-link" href="${pageContext.request.contextPath}/admin/admin/report">신고 관리
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                        </li>
                    </ul>
                </li>
                <!-- 게시판 메뉴 끝 -->
            </ul>
        </div>
        <!-- 사이드 바 컨텐츠 끝-->
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
                    <div class="m-3">
                        <button type="submit" class="btn btn-light-secondary" form="editForm" id="saveButton">저장
                        </button>
                        <button type="button" class="btn btn-light-secondary"
                                onclick="location.href='/admin/post/${boardDetail.boardId}'">목록
                        </button>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</header>
<!-- 상단 바 끝 -->

<!-- 메인 페이지 시작 -->
<div class="pc-container">
    <div class="pc-content col-8 mx-auto">
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
                            <h5>게시글 수정</h5>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>작성자:</strong> 관리자</div>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>카테고리:</strong> ${postDetail.categoryName}</div>
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
                        <form id="editForm" enctype="multipart/form-data" method="post"
                              action="/admin/post/${boardId}/${postId}/edit-admin-post"
                              class="p-4 border rounded shadow-sm">
                            <sec:csrfInput/>

                            <input type="hidden" name="search" value="${page.search}">
                            <input type="hidden" name="searchType" value="${page.searchType}">
                            <input type="hidden" name="sortType" value="${page.sortType}">
                            <input type="hidden" name="page" value="${page.page}">

                            <!-- 삭제된 이미지 ID -->
                            <input type="hidden" id="removedImagesInput" name="removedImageIds" value="">

                            <c:if test="${boardDetail.boardType == 'GALLERY'}">
                                <div>현재 썸네일 이미지</div>
                                <img src="/image/${postDetail.imageId}" id="imagePreview"
                                     style="height: 30%; width: 30%"
                                     alt="${postDetail.title}">
                                <div>
                                    <div>썸네일 이미지 수정</div>
                                    <input type="file" name="thumbnailImage" id="thumbnailImage">
                                </div>
                            </c:if>

                            <div class="mb-3">
                                <select class="form-select" id="category" name="categoryId"
                                        aria-label="category select">
                                    <option value="">카테고리 선택</option>
                                    <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryId}"
                                    <c:if test="${category.categoryName == postDetail.categoryName}">
                                            selected
                                    </c:if>>
                                            ${category.categoryName}
                                        </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="title" class="form-label">제목</label>
                                <input type="text"
                                       class="form-control"
                                       name="title"
                                       value="${postDetail.title}"
                                       placeholder="제목"
                                       id="title"/>
                            </div>

                            <div class="mb-3">
                                <label for="content" class="form-label">내용</label>
                                <textarea class="form-control"
                                          name="content"
                                          placeholder="내용"
                                          id="content"
                                          rows="10">${postDetail.content}</textarea>
                            </div>

                            <c:choose>
                                <c:when test="${boardDetail.imageFlag == 'Y'}">
                                    <!-- 기존 이미지 리스트 -->
                                    <ul id="existingImagesList" class="d-flex flex-column align-items-center w-100">
                                        <c:forEach var="image" items="${images}">
                                            <li class="list-group-item d-flex align-items-center rounded-3  shadow-sm p-2 w-100"
                                                style="max-width: 50%;"> <!-- 최대 크기 설정 -->
                                                <!-- 이미지 이름 -->
                                                <a href="javascript:showImage('/image/${image.imageId}')"
                                                   class="text-center mx-auto">
                                                        ${image.originalName}
                                                </a>
                                                <!-- 삭제 버튼 -->
                                                <button type="button"
                                                        class="btn btn-danger btn-sm remove-image-btn ml-auto m-1"
                                                        data-image-id="${image.imageId}">X
                                                </button>
                                            </li>
                                        </c:forEach>
                                    </ul>

                                    <!-- 추가된 이미지 리스트 -->
                                    <ul id="addedImagesList" class="d-flex flex-column align-items-center w-100"></ul>

                                    <!-- 파일 입력 필드 -->
                                    <input type="file" id="imageInput" name="addedImages" multiple
                                           style="display:none;">

                                    <!-- 파일 입력 버튼 -->
                                    <div class="text-center m-3">
                                        <button type="button" class="btn btn-light-secondary" id="addImageBtn">이미지 추가
                                        </button>
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

                            <div class="form-group d-flex justify-content-center">
                                <div class="form-check m-2">
                                    <input type="radio"
                                           class="form-check-input" id="privateFlag-Y" name="privateFlag"
                                           value="N"
                                    ${postDetail.privateFlag == 'N' ? 'checked' : ''} />
                                    <label class="form-check-label" for="privateFlag-Y">공개</label>
                                </div>
                                <div class="form-check m-2">
                                    <input type="radio"
                                           class="form-check-input" id="privateFlag-N" name="privateFlag"
                                           value="Y"
                                    ${postDetail.privateFlag == 'Y' ? 'checked' : ''} />
                                    <label class="form-check-label" for="privateFlag-N">비공개</label>
                                </div>
                            </div>
                            <div class="form-group d-flex justify-content-center">
                                <div class="form-check m-2">
                                    <input class="form-check-input" type="radio" id="noticeFlag-Y" name="noticeFlag"
                                           value="N" ${postDetail.noticeFlag == 'N' ? 'checked' : ''} />
                                    <label for="noticeFlag-Y">일반 게시글</label>
                                </div>

                                <div class="form-check m-2">
                                    <input class="form-check-input" type="radio" id="noticeFlag-N" name="noticeFlag"
                                           value="Y" ${postDetail.noticeFlag == 'Y' ? 'checked' : ''}/>
                                    <label for="noticeFlag-N">공지사항 게시글</label>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
