<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Temp Edit Page | 임시저장 게시글 수정 </title>

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

            // 새로운 이미지 추가
            $('#imageInput').on('change', function () {
                const files = this.files;

                addedImages = [];

                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const imageUrl = URL.createObjectURL(file);

                    addedImages.push(file);

                    $('#addedImagesList').append(`
                        <li class="list-group-item d-flex align-items-center rounded-3 p-2 shadow w-100" style="max-width: 30%;">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-decoration-none m-2 flex-grow-1">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm remove-image-btn ml-auto m-1">X</button>
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

            // 게시 버튼 클릭 시 액션 변경
            $('#publishButton').on('click', function (e) {
                if (validatePublishTempPostForm(e)) {
                    $('#editTempForm').attr('action', '<c:url value="/${boardId}/post/temp/${postDetail.tempPostId}/publish"/>');
                    $('#editTempForm').submit();
                }
            });

            // 저장 버튼
            $('#saveButton').on('click', function (e) {
                if (validateEditTempPostForm(e)) {
                    $('#editTempForm').attr('action', '<c:url value="/${boardId}/post/temp/${postDetail.tempPostId}/save"/>');
                    $('#editTempForm').submit();
                }
            });

            // 삭제 버튼 클릭 시 액션 변경
            $('#deleteButton').on('click', function () {
                $('#editTempForm').attr('action', '<c:url value="/${boardId}/post/temp/${postDetail.tempPostId}/delete"/>');
                $('#editTempForm').submit();
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
            alert(message);
        }

        // 임시저장 게시글 게시 시 입력 폼 검증
        function validatePublishTempPostForm(e) {
            const category = $('#category').val();
            const title = $('#title').val();
            const content = $('#content').val();

            if (!(category && title && content)) {
                alertMessage(e, "카테고리, 제목, 내용은 필수 입력사항입니다.");
                return false;
            } else if (title.length < 1 || title.length > 50) {
                alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.");
                return false;
            } else if (content.length < 1 || content.length > 500) {
                alertMessage(e, "내용은 최소 1글자, 최대 500글자이어야 합니다.");
                return false;
            }
            return true;
        }

        // 임시저장 게시글 수정 시 입력 폼 검증
        function validateEditTempPostForm(e) {
            const title = $('#title').val();

            if (!title) {
                alertMessage(e, "임시저장 시 제목은 필수 입력사항입니다.");
                return false;
            } else if (title.length < 1 || title.length > 50) {
                alertMessage(e, "내용은 최소 1글자, 최대 500글자이어야 합니다.");
                return false;
            }
            return true;
        }
    </script>

    <script type="text/javascript">
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
                    <a href="${pageContext.request.contextPath}/home" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-home"></i></span>
                        <span class="pc-mtext">홈페이지로 이동</span>
                    </a>
                </li>

                <!-- 로그인, 로그아웃 / 회원가입 -->
                <li class="pc-item pc-caption">
                    <label>계정</label>
                    <i class="ti ti-news"></i>
                </li>

                <c:if test="${!isAuthenticatedUser}">
                    <li class="pc-item">
                        <a href="${pageContext.request.contextPath}/user/login" class="pc-link">
                            <span class="pc-micon"><i class="ti ti-user-plus"></i></span>
                            <span class="pc-mtext">로그인</span>
                        </a>
                    </li>

                    <li class="pc-item">
                        <a href="${pageContext.request.contextPath}/user/signup" class="pc-link">
                            <span class="pc-micon"><i class="ti ti-user-plus"></i></span>
                            <span class="pc-mtext">회원가입</span>
                        </a>
                    </li>
                </c:if>
                <c:if test="${isAuthenticatedUser}">
                    <li class="pc-item">
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
                    </li>
                </c:if>

                <!-- 게시판 메뉴 타이틀 -->
                <li class="pc-item pc-caption">
                    <label>게시판</label>
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
                                <c:forEach var="board" items="${allBoards}">
                                    <c:if test="${board.boardType == 'GENERAL'}">
                                        <li class="pc-item">
                                            <a class="pc-link" href="/${board.boardId}/post">${board.boardName}</a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </li>

                        <!-- 갤러리 게시판 : 1뎁스 -->
                        <li class="pc-item pc-hasmenu">
                            <a href="javascript:void(0);" class="pc-link">갤러리 게시판
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                            <!-- 2뎁스 -->
                            <ul class="pc-submenu">
                                <c:forEach var="board" items="${allBoards}">
                                    <c:if test="${board.boardType == 'GALLERY'}">
                                        <li class="pc-item">
                                            <a class="pc-link" href="/${board.boardId}/post">${board.boardName}</a>
                                        </li>
                                    </c:if>
                                </c:forEach>
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
                    <div class="m-3">
                        <button type="submit" class="btn btn-light-secondary" id="publishButton">게시</button>
                        <button type="submit" class="btn btn-light-secondary" id="saveButton">저장</button>
                        <button type="submit" class="btn btn-light-secondary" id="deleteButton">삭제</button>
                        <a href="/${boardId}/post/temp?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                            <button type="button" class="btn btn-light-secondary">취소</button>
                        </a>
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
                            <h5>임시저장</h5>
                            <div class="mx-2">|</div> <!-- 구분선 -->
                            <div><strong>작성자:</strong> ${postDetail.userName}</div>
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
                        <form id="editTempForm" enctype="multipart/form-data" method="post"
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
                                            <li class="list-group-item d-flex align-items-center rounded-3 p-2 shadow w-100"
                                                style="max-width: 50%;"> <!-- 최대 크기 설정 -->
                                                <!-- 이미지 이름 -->
                                                <a href="javascript:showImage('/image/${image.imageId}')"
                                                   class="text-decoration-none m-2 flex-grow-1">
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

                            <div class="mb-3 form-group d-flex justify-content-center">
                                <div class="form-check m-3">
                                    <label class="form-check-label" for="privateFlag-Y">공개</label>
                                    <input type="radio"
                                           class="form-check-input" id="privateFlag-Y" name="privateFlag"
                                           value="N"
                                    ${postDetail.privateFlag == 'N' ? 'checked' : ''} />
                                </div>

                                <div class="form-check m-3">
                                    <label class="form-check-label" for="privateFlag-N">비공개</label>
                                    <input type="radio"
                                           class="form-check-input" id="privateFlag-N" name="privateFlag"
                                           value="Y"
                                    ${postDetail.privateFlag == 'Y' ? 'checked' : ''} />
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
