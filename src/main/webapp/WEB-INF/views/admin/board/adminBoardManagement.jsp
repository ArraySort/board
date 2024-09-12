<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Admin Management Page | 게시판 관리 </title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>
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
                <!-- 사이드 바 버튼 -->
                <li class="pc-h-item header-mobile-collapse">
                    <a href="#" class="pc-head-link head-link-secondary ms-0" id="sidebar-hide">
                        <i class="ti ti-menu-2"></i>
                    </a>
                </li>
            </ul>
        </div>

        <!-- 우측 상단 기능 버튼 -->
        <div class="ms-auto pc-mob-drp">
            <ul class="list-unstyled">
                <li class="pc-h-item d-none d-md-inline-flex">
                    <button type="button" class="btn btn-light-secondary"
                            onclick="location.href='/admin/board/add'">게시판 추가
                    </button>
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
                <div class="page-header-title">
                    <h5 class="m-b-10">게시판 관리</h5>
                </div>
                <ul class="breadcrumb">
                    <li style="font-size: 1rem">총 게시판 개수 : ${boardPagination.totalPostCount}</li>
                </ul>
            </div>
        </div>
        <!-- 게시판 페이지 헤더 끝 -->

        <!-- 게시판 리스트 시작 -->
        <div class="container mt-4">
            <div class="row">
                <c:forEach var="board" items="${boardPagination.postList}" varStatus="status">
                    <div class="col-md-6 mb-3">
                        <div class="card">
                            <div class="card-body">
                                <!-- 번호 및 이름 강조 -->
                                <h3 class="card-title">${boardPagination.totalPostCount - ((boardPagination.currentPage - 1) * 10) - status.index}
                                    - ${board.boardName}</h3>

                                <!-- 게시판 정보 (종류, 순서, 이미지, 이미지 수, 댓글 허용 여부, 공지 수, 접근 등급, 활성화) -->
                                <ul class="list-unstyled">
                                    <li><strong>종류:</strong>
                                        <c:choose>
                                            <c:when test="${board.boardType == 'GENERAL'}"> 일반 게시판</c:when>
                                            <c:when test="${board.boardType == 'GALLERY'}"> 갤러리 게시판</c:when>
                                        </c:choose>
                                    </li>
                                    <li><strong>이미지 업로드 가능 여부:</strong>
                                        <c:choose>
                                            <c:when test="${board.imageFlag == 'Y'}"> O</c:when>
                                            <c:when test="${board.imageFlag == 'N'}"> X</c:when>
                                        </c:choose>
                                    </li>
                                    <li><strong>이미지 최대 업로드 수:</strong>
                                        <c:if test="${board.imageFlag == 'N'}">
                                            X
                                        </c:if>
                                            ${board.imageLimit}</li>
                                    <li><strong>댓글 허용:</strong>
                                        <c:choose>
                                            <c:when test="${board.commentFlag == 'Y'}"> O</c:when>
                                            <c:when test="${board.commentFlag == 'N'}"> X</c:when>
                                        </c:choose>
                                    </li>
                                    <li><strong>공지 수:</strong> ${board.noticeCount}</li>
                                    <li><strong>접근 등급:</strong> ${board.accessLevel}LEVEL</li>
                                    <li><strong>활성화:</strong>
                                        <c:choose>
                                            <c:when test="${board.activateFlag == 'Y'}"> O</c:when>
                                            <c:when test="${board.activateFlag == 'N'}"> X</c:when>
                                        </c:choose>
                                    </li>
                                </ul>

                                <!-- 생성 및 수정 날짜 -->
                                <p class="card-text">
                                    <small>생성일:
                                        <fmt:formatDate value="${board.createdAt}"
                                                        pattern="yyyy-MM-dd HH:mm"/></small><br>
                                    <small>수정일:
                                        <fmt:formatDate value="${board.updatedAt}"
                                                        pattern="yyyy-MM-dd HH:mm"/></small>
                                </p>

                                <!-- 수정 및 삭제 버튼 -->
                                <div class="d-flex justify-content-end">
                                    <button class="btn btn-primary me-2"
                                            onclick="location.href='/admin/board/${board.boardId}/edit'">수정
                                    </button>
                                    <form action="/admin/board/${board.boardId}/process-delete-board" method="post">
                                        <sec:csrfInput/>
                                        <button class="btn btn-danger" type="submit">삭제</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <!-- 페이지 시작 -->
            <nav>
                <ul class="pagination justify-content-center">
                    <li class="page-item">
                        <a class="page-link" href="${pageContext.request.contextPath}/admin/board?page=1">&laquo;</a>
                    </li>
                    <c:if test="${boardPagination.prev}">
                        <li class="page-item">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/admin/board?page=${pagination.startBlockPage - 1}">&lt;</a>
                        </li>
                    </c:if>
                    <c:forEach var="pageNum" begin="${boardPagination.startBlockPage}"
                               end="${boardPagination.endBlockPage}">
                        <li class="page-item ${pageNum == boardPagination.currentPage ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/admin/board?page=${pageNum}">${pageNum}</a>
                        </li>
                    </c:forEach>
                    <c:if test="${boardPagination.next}">
                        <li class="page-item">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/admin/board?page=${boardPagination.endBlockPage + 1}">&gt;</a>
                        </li>
                    </c:if>
                    <li class="page-item">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/admin/board?page=${boardPagination.totalPageCount}">&raquo;</a>
                    </li>
                </ul>
            </nav>
            <!-- 페이지 시작 -->
        </div>
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
