<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Admin Management Page | 게시판 수정 </title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <!-- 카테고리 항목의 스타일 -->
    <style>
        .category-tag {
            display: inline-flex;
            align-items: center;
            background-color: #d6cfe7;
            color: #6439af;
            border-radius: 0.25rem;
            padding: 0.25rem 0.5rem;
            margin-right: 0.5rem;
            margin-bottom: 0.5rem;
            font-size: 0.875rem;
        }

        .category-tag .close {
            cursor: pointer;
            background: none;
            border: none;
            color: #dc3545;
            font-size: 1rem;
            margin-left: 0.5rem;
        }
    </style>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            $('input[name=imageFlag]').change(function () {
                toggleImageLimit();
            });

            // 게시판 수정 버튼 클릭 시
            $('#editBoardButton').click(function (e) {
                const boardName = $('#boardName').val();
                const boardType = $('#boardType').val();
                const boardOrder = $('#boardOrder').val();
                const categoryCount = $('#categoriesContainer span').length;
                const noticeCount = $('#noticeCount').val();
                const accessLevel = $('#accessLevel').val();

                console.log(categoryCount);

                if (!boardName) {
                    alertMessage(e, "게시판 이름을 입력해주세요.")
                } else if (boardName.length < 2 || boardName.length > 20) {
                    alertMessage(e, "게시판 이름은 최소 4글자 이상, 최대 20글자 이하입니다.")
                } else if (!boardType) {
                    alertMessage(e, "게시판 종류를 선택해주세요.")
                } else if (categoryCount < 1) {
                    alertMessage(e, "카테고리는 1개 이상 입력되어 있어야 합니다.");
                } else if (!boardOrder) {
                    alertMessage(e, "게시글 노출 순서를 선택해주세요.")
                } else if (!noticeCount) {
                    alertMessage(e, "공지사항 개수를 선택해주세요.")
                } else if (!accessLevel) {
                    alertMessage(e, "게시판 접근 허용 등급을 선택해주세요.")
                }
            });

            toggleImageLimit();

            function toggleImageLimit() {
                const imageFlagYes = $('#imageFlag-Y').is(':checked');
                if (imageFlagYes) {
                    $('#imageLimitSection').show();
                } else {
                    $('#imageLimitSection').hide();
                }
            }

            // 메세지 출력
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }
        });
    </script>

    <script>
        $(() => {
            let addedCategoryList = [];    // 추가된 카테고리
            let removedCategoryIds = [];   // 삭제된 카테고리 ID

            $('#categoryInput').on('keypress', function (e) {
                if (e.which === 13) { // Enter 키를 누르면
                    e.preventDefault();
                    const categoryName = $(this).val().trim();

                    if (categoryName !== '' && !addedCategoryList.includes(categoryName)) {
                        addedCategoryList.push(categoryName);
                        updateCategoryList();

                        const categoryTag = `
                        <span class="category-tag">
                            \${categoryName}
                            <button type="button" class="close ml-2" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </span>`;

                        $('#categoriesContainer').append(categoryTag);
                        $(this).val('');
                    } else {
                        alertMessage(e, "카테고리는 공백이나 중복입력이 불가합니다.");
                    }
                }
            });

            // 동적 생성된 말풍선 제거
            $('#categoriesContainer').on('click', '.close', function () {
                const index = $(this).closest('span').index();

                addedCategoryList.splice(index, 1);
                $(this).closest('span').remove();
                updateCategoryList();
                console.log(removedCategoryIds);
            });

            $('.remove-existing-category-button').on('click', function () {
                const index = $(this).closest('span').index();
                const categoryId = $(this).data('category-id');

                removedCategoryIds.push(categoryId);
                $(this).closest('span').remove();
                updateRemovedCategoryIds();
                console.log(removedCategoryIds);
            });

            // 카테고리 리스트 업데이트
            function updateCategoryList() {
                $('#addedCategoryList').val(addedCategoryList.join(','));
                $('#categoryList').val(addedCategoryList.join(','));
            }

            // 삭제 리스트 업데이트
            function updateRemovedCategoryIds() {
                $('#removedCategoryIds').val(removedCategoryIds.join(','));
            }

            // 메세지 출력
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
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
                        <%-- TODO : 버튼 post action 바꾸기 --%>
                        <button type="submit" class="btn btn-light-secondary"
                                id="editBoardButton" form="editBoardForm">수정
                        </button>
                        <button type="button" class="btn btn-light-secondary"
                                onclick="location.href='/admin/board'">취소
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
                            <h5 class="m-b-10">게시판 관리</h5>
                        </div>
                        <ul class="breadcrumb">
                            <li class="breadcrumb-item">게시판 수정</li>
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
                        <h5>게시판 수정</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-flex justify-content-center">
                            <form method="post"
                                  id="editBoardForm"
                                  action="${pageContext.request.contextPath}/admin/board/${boardDetail.boardId}/process-edit-board"
                                  style="max-width: 70%; width: 100%;">
                                <sec:csrfInput/>

                                <!-- 기본 정보 -->
                                <h4 class="mb-4">기본 정보</h4>
                                <div class="row mb-2">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="boardName">게시판 이름</label>
                                            <input type="text" id="boardName"
                                                   name="boardName"
                                                   class="form-control"
                                                   value="${boardDetail.boardName}"
                                                   placeholder="게시판 이름">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="boardType">게시판 종류</label>
                                            <select id="boardType" name="boardType" class="form-control">
                                                <option value="">게시판 종류 선택</option>
                                                <option value="GALLERY"${boardDetail.boardType == 'GALLERY' ? 'selected' : ''}>
                                                    갤러리
                                                </option>
                                                <option value="GENERAL" ${boardDetail.boardType == 'GENERAL' ? 'selected' : ''}>
                                                    일반
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-group mb-4">
                                    <label for="boardOrder">메인페이지 게시판 순서</label>
                                    <select id="boardOrder" name="boardOrder" class="form-control">
                                        <option value="${boardDetail.boardOrder}">${boardDetail.boardOrder}</option>
                                        <c:forEach var="board" items="${boardList}">
                                            <option value="${board.boardOrder}">${board.boardOrder}</option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <label>카테고리 입력</label>
                                <div class="form-group">
                                    <input type="text" id="categoryInput" class="form-control"
                                           placeholder="카테고리를 입력하고 엔터를 치세요.">
                                </div>

                                <div id="categoriesContainer" class="mt-3">
                                    <c:forEach var="category" items="${categoryList}">
                                    <span class="category-tag">${category.categoryName}
                                        <button type="button" class="close ml-2 remove-existing-category-button"
                                                aria-label="Close"
                                                data-category-id="${category.categoryId}">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </span>
                                    </c:forEach>
                                </div>

                                <input type="hidden" id="categoryList" name="categoryList"
                                       value="<c:forEach var='category' items='${categoryList}'>${category.categoryName},</c:forEach>">
                                <input type="hidden" id="addedCategoryList" name="addedCategoryList">
                                <input type="hidden" id="removedCategoryIds" name="removedCategoryIds">

                                <!-- 드롭다운 선택 필드 -->
                                <h4 class="mt-5 mb-4">설정</h4>
                                <div class="row">
                                    <div class="col-md-4">
                                        <div class="form-group mb-4">
                                            <label for="imageLimit">게시글 이미지 최대 개수</label>
                                            <select id="imageLimit" name="imageLimit" class="form-control">
                                                <option value="">선택</option>
                                                <option value="1" ${boardDetail.imageLimit == '1' ? 'selected' : ''}>1
                                                </option>
                                                <option value="2" ${boardDetail.imageLimit == '2' ? 'selected' : ''}>2
                                                </option>
                                                <option value="3" ${boardDetail.imageLimit == '3' ? 'selected' : ''}>3
                                                </option>
                                                <option value="4" ${boardDetail.imageLimit == '4' ? 'selected' : ''}>4
                                                </option>
                                                <option value="5" ${boardDetail.imageLimit == '5' ? 'selected' : ''}>5
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="form-group mb-4">
                                            <label for="noticeCount">공지사항 개수</label>
                                            <select id="noticeCount" name="noticeCount" class="form-control">
                                                <option value="">선택</option>
                                                <option value="0" ${boardDetail.noticeCount == '0' ? 'selected' : ''}>
                                                    0
                                                </option>
                                                <option value="1" ${boardDetail.noticeCount == '1' ? 'selected' : ''}>
                                                    1
                                                </option>
                                                <option value="2" ${boardDetail.noticeCount == '2' ? 'selected' : ''}>
                                                    2
                                                </option>
                                                <option value="3" ${boardDetail.noticeCount == '3' ? 'selected' : ''}>
                                                    3
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="col-md-4">
                                        <div class="form-group mb-4">
                                            <label for="accessLevel">게시판 접근 허용 등급</label>
                                            <select id="accessLevel" name="accessLevel" class="form-control">
                                                <option value="0" ${boardDetail.accessLevel == '0' ? 'selected' : ''}>
                                                    전체
                                                </option>
                                                <option value="1" ${boardDetail.accessLevel == '1' ? 'selected' : ''}>
                                                    LEVEL1
                                                </option>
                                                <option value="2" ${boardDetail.accessLevel == '2' ? 'selected' : ''}>
                                                    LEVEL2
                                                </option>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <!-- 허용 여부 -->
                                <div class="form-group mb-4">
                                    <div class="row">
                                        <div class="col-md-4">
                                            <label>이미지 허용</label>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" id="imageFlag-Y"
                                                       name="imageFlag" value="Y"
                                                ${boardDetail.imageFlag == 'Y' ? 'checked' : ''}>
                                                <label for="imageFlag-Y" class="form-check-label">허용</label>
                                            </div>
                                            <div class="form-check">
                                                <input class="form-check-input" type="radio" id="imageFlag-N"
                                                       name="imageFlag" value="N"
                                                ${boardDetail.imageFlag == 'N' ? 'checked' : ''}>
                                                <label for="imageFlag-N" class="form-check-label">허용 안함</label>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <label>댓글 허용</label>
                                            <div class="form-check">
                                                <input type="radio" id="commentFlag-Y" name="commentFlag" value="Y"
                                                       class="form-check-input"
                                                ${boardDetail.commentFlag == 'Y' ? 'checked' : ''}>
                                                <label for="commentFlag-Y" class="form-check-label">허용</label>
                                            </div>
                                            <div class="form-check">
                                                <input type="radio" id="commentFlag-N" name="commentFlag" value="N"
                                                       class="form-check-input"
                                                ${boardDetail.commentFlag == 'N' ? 'checked' : ''}>
                                                <label for="commentFlag-N" class="form-check-label">허용 안함</label>
                                            </div>
                                        </div>
                                        <div class="col-md-4">
                                            <label>활성화 여부</label>
                                            <div class="form-check">
                                                <input type="radio" id="activateFlag-Y" name="activateFlag" value="Y"
                                                       class="form-check-input"
                                                ${boardDetail.activateFlag == 'Y' ? 'checked' : ''}>
                                                <label for="commentFlag-Y" class="form-check-label">활성화</label>
                                            </div>
                                            <div class="form-check">
                                                <input type="radio" id="activateFlag-N" name="activateFlag" value="N"
                                                       class="form-check-input"
                                                ${boardDetail.activateFlag == 'N' ? 'checked' : ''}>
                                                <label for="commentFlag-N" class="form-check-label">비활성화</label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <!-- Body 컨텐츠 끝 -->
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
