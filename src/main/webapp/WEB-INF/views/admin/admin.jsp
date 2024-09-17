<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <title>Board Admin | 관리자 홈</title>
    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <style>
        .data-span {
            cursor: pointer;
            margin-right: 20px;
            color: #fff;
        }

        .data-span:hover {
            color: #eadb90;
        }

        .custom-hover {
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
        }

        .custom-hover:hover {
            background-color: #f8f9fa;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
        }

        .post-content {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
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
    </style>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            // 초기 값 설정
            const postCount = ${allPostsCount};
            const commentCount = ${allCommentsCount};
            const likeCount = ${allLikesCount};

            // 게시글 수 클릭 시
            $('#postBtn').on('click', function () {
                $('#displayCount').text(postCount);
                $('#displayText').text(' 모든 게시판의 게시글 수 입니다.');
            });

            // 댓글 수 클릭 시
            $('#commentBtn').on('click', function () {
                $('#displayCount').text(commentCount);
                $('#displayText').text(' 모든 게시판의 댓글 수 입니다.');
            });

            // 좋아요 수 클릭 시
            $('#likeBtn').on('click', function () {
                $('#displayCount').text(likeCount);
                $('#displayText').text(' 모든 게시판의 좋아요 수 입니다.');
            });
        });
    </script>

</head>

<body>

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
    </div>
</header>
<!-- [ Header ] end -->

<!-- 메인 -->
<div class="pc-container">
    <div class="pc-content col-10 mx-auto">
        <!-- 게시판 페이지 헤더 -->
        <div class="page-header">
            <div class="page-block">
                <div class="row align-items-center">
                    <div class="col-md-12">
                        <div class="page-header-title">
                            <h5>관리자 홈페이지</h5>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <!-- 총 방문자 수 -->
            <div class="col-xl-6 col-md-6">
                <div class="card bg-secondary-dark dashnum-card text-white overflow-hidden">
                    <span class="round small"></span>
                    <span class="round big"></span>
                    <div class="card-body">
                        <div class="row">
                            <div class="col">
                                <span>총 조회수</span>
                            </div>
                        </div>
                        <span class="text-white d-block f-34 f-w-500 my-2">${allViews}</span>
                        <i class="ti ti ti-eye-check opacity-50" style="font-size: 1rem"> 모든 게시판의 게시글 조회수 입니다.</i>
                    </div>
                </div>
            </div>
            <!-- 총 방문자 수 -->

            <!-- 게시글 수, 댓글 수, 좋아요 수 집계 -->
            <div class="col-xl-6 col-md-6">
                <div class="card bg-primary-dark dashnum-card text-white overflow-hidden">
                    <span class="round small"></span>
                    <span class="round big"></span>
                    <div class="card-body">
                        <div class="row">
                            <div class="col">
                                <!-- 버튼 그룹 -->
                                <span id="postBtn" class="data-span">게시글 수</span>
                                <span id="commentBtn" class="data-span">댓글 수</span>
                                <span id="likeBtn" class="data-span">좋아요 수</span>
                            </div>
                        </div>

                        <!-- 클릭한 수치 표시 영역 -->
                        <span class="text-white d-block f-34 f-w-500 my-2" id="displayCount">${allPostsCount}</span>
                        <i class="ti ti-eye-check opacity-50"
                           style="font-size: 1rem" id="displayText"> 모든 게시판의 게시글 수 입니다.</i>
                    </div>
                </div>
            </div>
            <!-- 게시글 수, 댓글 수, 좋아요 수 집계 -->

            <!-- 게시글 관리 -->
            <div class="col-xl-8 col-md-12">
                <div class="card">
                    <div class="card-body">
                        <h4 class="mb-3">최근 게시글</h4>
                        <div class="list-group" style="max-height: 400px; overflow-y: auto;">
                            <c:forEach var="post" items="${recentPosts}">
                                <div class="list-group-item mb-3 rounded-3 custom-hover"
                                     onclick="location.href='/${post.boardId}/post/detail/${post.postId}'">
                                    <!-- 제목, 작성자, 카테고리, 비활성화 버튼 -->
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <div>
                                            <strong class="mr-2" style="font-size: 1.3rem;">
                                                <c:if test="${post.noticeFlag == 'Y'}">
                                                    <span style="color: #5c62ce; font-weight: bold;">[공지사항]</span>
                                                </c:if>${post.title}
                                            </strong>
                                            <span class="mr-2" style="font-size: 1rem;">|
                                        <c:choose>
                                            <c:when test="${post.adminId != null}">
                                                <td>관리자</td>
                                            </c:when>
                                            <c:otherwise>
                                                <td>${post.userName}</td>
                                            </c:otherwise>
                                        </c:choose></span>
                                            <span class="text-muted"
                                                  style="font-size: 1rem;">| ${post.categoryName}</span>
                                        </div>
                                    </div>

                                    <p class="post-content">${post.content}</p>

                                    <!-- 생성일, 수정일, 조회수, 댓글수, 좋아요 -->
                                    <div class="d-flex justify-content-between mb-1">
                                        <div>
                                            <small style="font-size: 0.8rem;">
                                    <span class="me-3">
                                        채택댓글: <c:choose>
                                        <c:when test="${post.adoptedCommentFlag == 'Y'}"> O</c:when>
                                        <c:when test="${post.adoptedCommentFlag == 'N'}"> X</c:when>
                                    </c:choose> |
                                        비공개: <c:choose>
                                        <c:when test="${post.privateFlag == 'Y'}"> O</c:when>
                                        <c:when test="${post.privateFlag == 'N'}"> X</c:when>
                                    </c:choose></span>
                                            </small>

                                            <small style="font-size: 0.8rem;">
                                                <i class="ti ti-eye"> ${post.views}</i> | <i
                                                    class="ti ti-message-circle"> ${post.commentCount}</i> |
                                                <i class="ti ti-thumb-up"> ${post.likeCount}</i>
                                            </small>
                                        </div>

                                        <div class="me-3">
                                            <small style="font-size: 0.8rem;">
                                                <fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                            </small>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 게시글 관리 끝 -->

            <!-- 회원 랭킹 -->
            <div class="col-xl-4 col-md-12">
                <div class="card">
                    <div class="card-body">
                        <div class="row mb-3 align-items-center">
                            <div class="col">
                                <h4>회원 랭킹</h4>
                            </div>
                            <div class="col-auto"></div>
                        </div>
                        <ul class="list-group list-group-flush" style="max-height: 400px; overflow-y: auto;">
                            <c:forEach var="user" items="${userRanking}">
                                <li class="list-group-item px-0">
                                    <div class="row align-items-start">
                                        <div class="col">
                                            <h5 class="mb-0">${user.userName}</h5>
                                        </div>
                                        <div class="col-auto">
                                            <h4 class="mb-0">${user.point} Point</h4>
                                        </div>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                        <div class="text-center">
                            <a href="${pageContext.request.contextPath}/admin/user" class="b-b-primary text-primary">회원관리
                                <i class="ti ti-chevron-right"></i>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <!-- 회원 랭킹 끝 -->
        </div>
    </div>
</div>
<!-- 메인 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>

<script src="${pageContext.request.contextPath}/resources/assets/js/plugins/apexcharts.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/assets/js/pages/dashboard-default.js"></script>
</body>
</html>
