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

    <style>
        .custom-hover {
            transition: background-color 0.3s ease, box-shadow 0.3s ease;
        }

        .custom-hover:hover {
            background-color: #f8f9fa; /* 원하는 배경색 */
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* 원하는 그림자 효과 */
        }
    </style>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        // 게시판 선택
        function sendBoardIdToButton() {
            const boardId = $('#boardId').val();
            if (boardId) {
                window.location.href = '/admin/post/' + boardId;
            } else {
                alert('게시판을 선택하세요');
            }
        }
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
                <li class="pc-h-item d-none d-md-inline-flex mx-3">
                    <div class="input-group">
                        <select id="boardId" name="boardId" class="form-control" aria-label="게시판 선택">
                            <option value="" selected>게시판 선택</option>
                            <c:forEach var="board" items="${boardList}">
                                <option value="${board.boardId}">${board.boardName}</option>
                            </c:forEach>
                        </select>
                        <button class="btn btn-light-secondary" type="button" onclick="sendBoardIdToButton()">선택
                        </button>
                    </div>
                </li>

                <li class="pc-h-item d-none d-md-inline-flex">
                    <button type="button" class="btn btn-light-secondary mx-1"
                            onclick="location.href='/admin/post/${currentBoard.boardId}/add'">게시글 추가
                    </button>

                    <button type="button" class="btn btn-light-secondary"
                            onclick="location.href='/admin/post/${currentBoard.boardId}/deactivated'">비활성화 목록
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
                    <li class="breadcrumb-item" style="font-size: 1rem">${currentBoard.boardName}</li>
                    <li class="breadcrumb-item" style="font-size: 1rem">총 게시글 개수 : ${postPagination.totalPostCount}</li>
                </ul>
            </div>
        </div>
        <!-- 게시판 페이지 헤더 끝 -->

        <!-- 게시판 리스트 시작 -->
        <div class="row">
            <div class="list-group">
                <c:forEach var="post" items="${postPagination.postList}" varStatus="status">
                    <c:set var="postNumber"
                           value="${postPagination.totalPostCount - ((postPagination.currentPage - 1) * 10) - status.index}"/>
                    <div class="list-group-item mb-3 rounded-3 custom-hover"
                         onclick="location.href='/${currentBoard.boardId}/post/detail/${post.postId}'">
                        <!-- 제목, 작성자, 카테고리, 비활성화 버튼 -->
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <strong class="mr-2" style="font-size: 1.3rem;">
                                        ${postNumber} -
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
                                <span class="text-muted" style="font-size: 1rem;">| ${post.categoryName}</span>
                            </div>
                            <form action="/admin/post/${currentBoard.boardId}/${post.postId}/edit-activate-flag"
                                  method="post">
                                <sec:csrfInput/>
                                <button class="btn btn-sm btn-light-secondary">비활성화</button>
                            </form>
                        </div>
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
                                    생성일: <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/> |
                                    수정일: <fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </small>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
        <!-- 페이징 버튼 시작 -->
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/post/${boardId}?page=1">&laquo;</a>
                </li>
                <c:if test="${postPagination.prev}">
                    <li class="page-item">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.startBlockPage - 1}">&lt;</a>
                    </li>
                </c:if>
                <c:forEach var="pageNum" begin="${postPagination.startBlockPage}"
                           end="${postPagination.endBlockPage}">
                    <li class="page-item ${pageNum == postPagination.currentPage ? 'active' : ''}">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${pageNum}">${pageNum}</a>
                    </li>
                </c:forEach>
                <c:if test="${postPagination.next}">
                    <li class="page-item">
                        <a class="page-link"
                           href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.endBlockPage + 1}">&gt;</a>
                    </li>
                </c:if>
                <li class="page-item">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/admin/post/${boardId}?page=${postPagination.totalPageCount}">&raquo;</a>
                </li>
            </ul>
        </nav>
        <!-- 페이징 버튼 끝 -->
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
