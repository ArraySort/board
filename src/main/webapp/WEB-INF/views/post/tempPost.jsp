<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="kr">
<head>
    <title>Board Temp Page | 임시저장 </title>

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

<!-- 사이드 메뉴 시작 -->
<nav class="pc-sidebar">
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

                <!-- 검색 Input 시작 -->
                <li class="pc-h-item d-none d-md-inline-flex">
                    <form class="d-md-inline-flex header-search" method="get" action="/${boardId}/post/temp">
                        <select class="form-select m-1" name="sortType" aria-label="sortType select">
                            <option value="LATEST" ${page.sortType == 'LATEST' ? 'selected' : ''}>최신순</option>
                            <option value="OLDEST" ${page.sortType == 'OLDEST' ? 'selected' : ''}>오래된순</option>
                            <option value="VIEWS" ${page.sortType == 'VIEWS' ? 'selected' : ''}>조회순</option>
                        </select>

                        <select class="form-select m-1" name="searchType" aria-label="searchType select">
                            <option value="ALL" ${page.searchType == 'ALL' ? 'selected' : ''}>전체</option>
                            <option value="TITLE" ${page.searchType == 'TITLE' ? 'selected' : ''}>제목</option>
                            <option value="CONTENT" ${page.searchType == 'CONTENT' ? 'selected' : ''}>내용</option>
                        </select>

                        <input type="text" class="form-control" name="search" placeholder="검색">
                        <button type="submit" class="btn btn-light-secondary btn-search">
                            <i class="ti ti-list-search"></i>
                        </button>
                    </form>
                </li>
                <!-- 검색 Input 끝 -->
            </ul>
        </div>

        <div class="ms-auto pc-mob-drp">
            <ul class="list-unstyled">
                <li class="pc-h-item d-none d-md-inline-flex">
                    <div class="m-3">
                        <button type="button" class="btn btn-light-secondary"
                                onclick="location.href='/${boardId}/post/add'">게시글 추가
                        </button>
                        <button type="button" class="btn btn-light-secondary"
                                onclick="location.href='/${boardId}/post'">게시글 목록
                        </button>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</header>

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

        <!-- 게시판 리스트 시작 -->
        <div class="row">
            <div class="card">
                <c:if test="${boardDetail.boardType == 'GENERAL'}">
                    <div class="table-active table-responsive table" style="margin-top: 2%">
                        <table class="table table-bordered table-hover text-center">
                            <thead class="table-dark table-header">
                            <tr>
                                <th scope="col">번호</th>
                                <th scope="col">제목</th>
                                <th scope="col">유저아이디</th>
                                <th scope="col">생성시간</th>
                                <th scope="col">수정시간</th>
                                <th scope="col">카테고리</th>
                                <th scope="col">조회수</th>
                                <th scope="col">비공개</th>
                            </tr>
                            </thead>
                            <tbody class="table-body">
                            <c:forEach var="post" items="${pagination.postList}" varStatus="status">
                                <c:set var="postNumber"
                                       value="${pagination.totalPostCount - ((pagination.currentPage - 1) * 10) - status.index}"/>
                                <tr onclick="location.href='/${boardId}/post/temp/${post.postId}/edit'">
                                    <td>${postNumber}</td>
                                    <td>${post.title}</td>
                                    <td>${post.userName}</td>
                                    <td><fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                                    <td><fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                                    <td>${post.categoryName}</td>
                                    <td>${post.views}</td>
                                    <td>${post.privateFlag}</td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- 페이지 버튼 -->
                    <nav>
                        <ul class="pagination justify-content-center">
                            <!-- 처음 페이지로 이동하는 버튼 -->
                            <li class="page-item">
                                <a class="page-link"
                                   href="/${boardId}/post?page=1&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">&laquo</a>
                            </li>
                            <!-- 이전 블록으로 이동하는 버튼 -->
                            <c:if test="${pagination.prev}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="/${boardId}/post?page=${pagination.startBlockPage - 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                        &lt;
                                    </a>
                                </li>
                            </c:if>
                            <!-- 페이지 번호 -->
                            <c:forEach var="pageNum" begin="${pagination.startBlockPage}"
                                       end="${pagination.endBlockPage}">
                                <li class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                                    <a class="page-link"
                                       href="/${boardId}/post?page=${pageNum}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                            ${pageNum}
                                    </a>
                                </li>
                            </c:forEach>
                            <!-- 다음 블록으로 이동하는 버튼 -->
                            <c:if test="${pagination.next}">
                                <li class="page-item">
                                    <a class="page-link"
                                       href="/${boardId}/post?page=${pagination.endBlockPage + 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                        &gt;
                                    </a>
                                </li>
                            </c:if>
                            <!-- 끝 페이지로 이동하는 버튼 -->
                            <li class="page-item">
                                <a class="page-link"
                                   href="/${boardId}/post?page=${pagination.totalPageCount}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                    &raquo;
                                </a>
                            </li>
                        </ul>
                    </nav>
                    <!-- 페이지 버튼 끝-->
                </c:if>
            </div>
        </div>
        <!-- 게시판 리스트 끝 -->

        <!-- 갤러리 게시판 시작 -->
        <c:if test="${boardDetail.boardType == 'GALLERY'}">
            <div class="row">
                <c:forEach var="post" items="${pagination.postList}" varStatus="status">
                    <div class="col-md-6 col-lg-4">
                        <div class="card card-">
                            <div class="card gallery-card">
                                <img src="/image/${post.imageId}" class="card-img-top" alt="${post.title}">
                            </div>
                            <div class="card-body">
                                <h5 class="card-title">
                                    <a href="/${boardId}/post/detail/${post.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                                            ${post.title}
                                    </a>
                                </h5>
                                <p class="card-text">카테고리 : ${post.categoryName}</p>
                                <p class="card-text">조회수 : ${post.views} views</p>
                                <div class="card-text">생성 날짜 :
                                    <fmt:formatDate value="${post.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </div>
                                <div class="card-text">수정 날짜 :
                                    <fmt:formatDate value="${post.updatedAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <!-- 페이지 버튼 시작 -->
                <nav>
                    <ul class="pagination justify-content-center">
                        <!-- 처음 페이지로 이동하는 버튼 -->
                        <li class="page-item">
                            <a class="page-link"
                               href="/${boardId}/post?page=1&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">&laquo</a>
                        </li>
                        <!-- 이전 블록으로 이동하는 버튼 -->
                        <c:if test="${pagination.prev}">
                            <li class="page-item">
                                <a class="page-link"
                                   href="/${boardId}/post?page=${pagination.startBlockPage - 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                    &lt;
                                </a>
                            </li>
                        </c:if>
                        <!-- 페이지 번호 -->
                        <c:forEach var="pageNum" begin="${pagination.startBlockPage}"
                                   end="${pagination.endBlockPage}">
                            <li class="page-item ${pageNum == pagination.currentPage ? 'active' : ''}">
                                <a class="page-link"
                                   href="/${boardId}/post?page=${pageNum}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                        ${pageNum}
                                </a>
                            </li>
                        </c:forEach>
                        <!-- 다음 블록으로 이동하는 버튼 -->
                        <c:if test="${pagination.next}">
                            <li class="page-item">
                                <a class="page-link"
                                   href="/${boardId}/post?page=${pagination.endBlockPage + 1}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                    &gt;
                                </a>
                            </li>
                        </c:if>
                        <!-- 끝 페이지로 이동하는 버튼 -->
                        <li class="page-item">
                            <a class="page-link"
                               href="/${boardId}/post?page=${pagination.totalPageCount}&searchType=${page.searchType}&search=${page.search}&sortType=${page.sortType}">
                                &raquo;
                            </a>
                        </li>
                    </ul>
                </nav>
                <!-- 페이지 버튼 끝-->
            </div>
        </c:if>
        <!-- 갤러리 게시판 끝 -->
    </div>
</div>
<!-- 메인 페이지 끝 -->

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>
</body>

</html>
