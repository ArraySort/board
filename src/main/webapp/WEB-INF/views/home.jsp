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
    <style>
        .card-custom {
            border: 1px solid #dee2e6;
            border-radius: 0.5rem;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            margin-bottom: 1.5rem;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .card-body-custom {
            padding: 1.25rem;
        }

        .card-custom:hover {
            transform: scale(1.02);
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
            cursor: pointer;
        }

        .card-header-custom {
            background-color: #d6cfe7;
            padding: 0.75rem 1.25rem;
            border-bottom: 1px solid #d6cfe7;
            transition: background-color 0.3s ease;
        }

        .card-header-custom:hover {
            background-color: #b5a3d6;
            cursor: pointer;
        }

        .list-group-item {
            border: 0;
            border-bottom: 1px solid #dee2e6;
            padding: 0.75rem 1.25rem;
            transition: background-color 0.3s ease;
        }

        .list-group-item:hover {
            background-color: #f8f9fa;
            cursor: pointer;
        }

        .post-content {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
    </style>

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
                </li>
                <li class="pc-item">
                    <a href="${pageContext.request.contextPath}/home" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-home"></i></span>
                        <span class="pc-mtext">홈페이지로 이동</span></a>
                </li>

                <!-- 로그인, 로그아웃 / 회원가입 -->
                <li class="pc-item pc-caption">
                    <label>계정</label>
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

                <!-- 랭킹 메뉴 타이틀 -->
                <li class="pc-item pc-caption">
                    <label>랭킹</label>
                </li>

                <!-- 랭킹 메뉴 시작 -->
                <li class="pc-item pc-hasmenu">
                    <a href="javascript:void(0);" class="pc-link">
                        <span class="pc-micon"><i class="ti ti-menu"></i></span>
                        <span class="pc-mtext">유저 랭킹</span>
                    </a>
                    <ul class="pc-submenu">
                        <!-- 오늘의 유저 랭킹 표시 -->
                        <li class="pc-item pc-hasmenu">
                            <a href="javascript:void(0);" class="pc-link"> 오늘의 유저
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                            <ul class="pc-submenu">
                                <c:forEach var="user" items="${userDailyRanking}">
                                    <li class="pc-item">
                                        <a class="pc-link" href="${pageContext.request.contextPath}/home">
                                                ${user.userName}
                                            <br>
                                                ${user.dailyPoint}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </li>

                        <!-- 유저 TOP 10 랭킹 표시 -->
                        <li class="pc-item pc-hasmenu">
                            <a href="javascript:void(0);" class="pc-link"> TOP 10
                                <span class="pc-arrow"><i data-feather="chevron-right"></i></span>
                            </a>
                            <ul class="pc-submenu">
                                <c:forEach var="user" items="${userRanking}">
                                    <li class="pc-item">
                                        <a class="pc-link" href="javascript:void(0);">
                                                ${user.userName}
                                            <br>
                                                ${user.point}
                                        </a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </li>
                    </ul>
                </li>
                <!-- 랭킹 메뉴 끝 -->
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
    </div>
</header>
<!-- [ Header ] end -->


<!-- 메인 페이지 시작 -->
<div class="pc-container">
    <div class="pc-content col-10 mx-auto">
        <!-- 게시판 페이지 헤더 -->
        <div class="page-header">
            <div class="page-block">
                <div class="row align-items-center">
                    <div class="col-md-12">
                        <div class="page-header-title">
                            <h5>게시판 홈페이지</h5>
                        </div>
                        <ul class="breadcrumb">
                            <li class="breadcrumb-item">자신에게 어울리는 게시판을 선택해보세요.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        <!-- 게시판 페이지 헤더 끝 -->

        <div class="page-block mt-3">
            <div class="row">
                <!-- boards 반복문을 돌면서 각 게시판 상위 5개 게시물을 출력 -->
                <c:forEach var="board" items="${boards}">
                    <div class="col-lg-6 col-md-6">
                        <!-- 카드뷰로 게시판 제목과 게시물 리스트 표시 -->
                        <div class="card card-custom">
                            <div class="card-header card-header-custom"
                                 onclick="location.href='/${board.boardId}/post'">
                                <h5>${board.boardName}</h5>
                            </div>
                            <div class="card-body card-body-custom">
                                <ul class="list-group">
                                    <!-- 각 게시판의 TopPosts를 동적으로 참조 -->
                                    <c:forEach var="post" items="${recentPosts[board.boardName]}">
                                        <li class="list-group-item mb-3"
                                            onclick="location.href='/${board.boardId}/post/detail/${post.postId}'">
                                            <div class="post-header d-flex justify-content-between">
                                                <span>
                                                    <strong>${post.title}</strong>
                                                    <small class="text-muted"> 카테고리 : ${post.categoryName}</small>
                                                </span>
                                                <small class="text-muted">${post.userName}</small>
                                            </div>
                                            <p class="post-content mt-2">${post.content}</p>
                                            <div class="post-footer d-flex justify-content-between text-muted">
                                                <span>
                                                    <small><i class="ti ti-eye">${post.views}</i>
                                                        | <i class="ti ti-message">${post.commentCount}</i>
                                                        |<i class="ti ti-thumb-up">${post.likeCount}</i>
                                                    </small>
                                                </span>
                                                <span>
                                                    <small><fmt:formatDate value="${post.createdAt}"
                                                                           pattern="yyyy-MM-dd"/>
                                                    </small>
                                                </span>
                                            </div>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
