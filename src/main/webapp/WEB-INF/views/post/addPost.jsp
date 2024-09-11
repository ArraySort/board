<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Page | 게시판 </title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            function alertMessage(e, message) {
                alert(message);
            }

            // 입력 폼 검증
            function validateAddForm(e) {
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

            function validateAddTempForm(e) {
                const title = $('#title').val();

                if (!title) {
                    alertMessage(e, "제목은 필수 입력사항입니다.")
                    return false;
                } else if (title.length < 1 || title.length > 50) {
                    alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.")
                    return false;
                }

                return true;
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
                if (validateAddForm(e)) {
                    $('#postForm').attr('action', '<c:url value="/${boardId}/post/process-add-post"/>');
                    $('#postForm').submit();
                }
            });

            // 임시저장 버튼
            $('#saveTempPost').on('click', function (e) {
                if (validateAddTempForm(e)) {
                    $('#postForm').attr('action', '<c:url value="/${boardId}/post/process-save-temp-post"/>');
                    $('#postForm').submit();
                }
            });
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
            <a href="../dashboard/index.html" class="b-brand text-primary">
                <!-- ========   TODO : 로고 변경   ============ -->
                <img src="../assets/images/logo-dark.svg" alt="" class="logo logo-lg">
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
                    <div class="m-3">
                        <button type="submit" class="btn btn-light-secondary" id="saveTempPost">임시저장</button>
                        <button type="submit" class="btn btn-light-secondary" id="addPost">저장</button>
                        <button type="button" class="btn btn-light-secondary"
                                onclick="location.href='/${boardId}/post'">목록
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
                    <div class="card-header">
                        <h5>게시글 추가</h5>
                    </div>
                    <div class="card-body">
                        <form id="postForm" enctype="multipart/form-data" method="post"
                              class="p-4 border rounded shadow-sm">
                            <sec:csrfInput/>

                            <c:if test="${boardDetail.boardType == 'GALLERY'}">
                                <div class="mb-3 text-center">
                                    <img src="" id="imagePreview" class="img-thumbnail"
                                         style="height: 30%; width: 30%"
                                         alt="이미지 업로드 미리보기">
                                    <div class="mt-2">
                                        <label for="thumbnailImage" class="form-label">썸네일 이미지 업로드</label>
                                        <input type="file" class="form-control" name="thumbnailImage"
                                               id="thumbnailImage">
                                    </div>
                                </div>
                            </c:if>

                            <div class="mb-3">
                                <label for="category" class="form-label">카테고리 선택</label>
                                <select name="categoryId" id="category" class="form-select"
                                        aria-label="category select">
                                    <option value="">카테고리 선택</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.categoryId}">${category.categoryName}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="title" class="form-label">제목</label>
                                <input type="text" class="form-control" name="title" placeholder="제목" id="title"/>
                            </div>

                            <div class="mb-3">
                                <label for="content" class="form-label">내용</label>
                                <textarea class="form-control" name="content" placeholder="내용" id="content"
                                          rows="10"></textarea>
                            </div>

                            <c:choose>
                                <c:when test="${boardDetail.imageFlag == 'Y'}">
                                    <div class="mb-3">
                                        <input type="file" class="form-control" name="images" multiple>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-danger">이미지 업로드가 허용되지 않는 게시판입니다.</p>
                                </c:otherwise>
                            </c:choose>

                            <div class="mb-3 form-group d-flex justify-content-center">
                                <div class="form-check m-3">
                                    <input class="form-check-input" type="radio" id="privateFlag-Y" name="privateFlag"
                                           value="N" checked>
                                    <label class="form-check-label" for="privateFlag-Y">공개</label>
                                </div>
                                <div class="form-check m-3">
                                    <input class="form-check-input" type="radio" id="privateFlag-N" name="privateFlag"
                                           value="Y">
                                    <label class="form-check-label" for="privateFlag-N">비공개</label>
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
