<%@ page import="arraysort.project.board.app.utils.UserUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .comment-item {
        position: relative;
        padding-left: 10px;
    }

    .comment-item::before {
        content: '';
        position: absolute;
        left: 0;
        top: 40px;
        bottom: 30px;
        width: 1px;
        background-color: #d4d8de;
        border-left: 1px solid #d4d8de;
        border-top: none;
        border-bottom: none;
    }

    .comment-item:not(:first-child)::before {
        border-top: none;
    }

    .comment-item:not(:last-child)::before {
        border-bottom: none;
    }
</style>

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

<li class="list-group-item ${comment.depth > 0 ? 'comment-item' : ''}"
    style="margin-left: ${requestScope.comment.depth * 20}px">
    <!-- 댓글 상단: 작성자, 내용, 수정/삭제 버튼 -->
    <i class="ti ti-arrow-down-right"></i>
    <!-- 댓글 상단: 작성자, 작성 시간, 수정 시간, 좋아요/싫어요 버튼 -->
    <div class="d-flex justify-content-between">
        <div class="me-3">
            <strong>${comment.userName} | </strong>
            <small><fmt:formatDate value="${comment.updatedAt}"
                                   pattern="yyyy-MM-dd HH:mm"/></small>
        </div>
        <div>
            <!-- 좋아요 버튼 -->
            <button class="btn btn-sm commentLikeButton ${comment.hasLiked ? 'liked' : 'not-liked'}"
                    id="commentLikeButton-${comment.commentId}" type="button"
                    data-comment-id="${comment.commentId}">
                <i class="ti ti-thumb-up"></i><span
                    id="commentLikeCount-${comment.commentId}">${comment.likeCount}</span>
            </button>

            <!-- 싫어요 버튼 -->
            <button class="btn btn-sm commentDislikeButton ${comment.hasDisliked ? 'disliked' : 'not-liked'}"
                    id="commentDislikeButton-${comment.commentId}" type="button"
                    data-comment-id="${comment.commentId}">
                <i class="ti ti-thumb-down"></i><span
                    id="commentDislikeCount-${comment.commentId}">${comment.dislikeCount}</span>
            </button>
        </div>
    </div>

    <!-- 댓글 본문: 중앙 정렬 -->
    <div class="mt-3 mb-4" style="font-size: 1.1rem;">
        ${comment.commentContent}
    </div>

    <!-- 댓글 하단: 첨부 이미지, 대댓글 보기 및 답글 달기, 수정/삭제/채택 버튼 -->
    <div class="d-flex justify-content-between mt-2">
        <div class="d-flex justify-content-center align-items-center">

            <!-- 대댓글 보기 및 답글 달기 버튼 -->
            <button class="showReplyFormButton border-0 bg-transparent p-0 ms-2 me-3"
                    data-id="${comment.commentId}" aria-label="답글 달기">
                <i class="ti ti-pencil" style="font-size: 0.8rem;">답글달기</i>
            </button>
            <div>
                <!-- 첨부 이미지 -->
                <c:if test="${not empty comment.commentImages}">
                    <strong>첨부 이미지 :</strong>
                    <c:forEach var="image" items="${comment.commentImages}"
                               varStatus="status">
                        <a href="javascript:showImage('/image/${image.imageId}')">[${status.index + 1}]</a>
                    </c:forEach>
                </c:if>
            </div>
        </div>

        <div>
            <c:if test="${currentUserId == comment.userId}">
                <button class="btn btn-sm border-0 bg-transparent commentEditButton"
                        data-id="${comment.commentId}">수정
                </button>
                <form method="post"
                      action="/${boardId}/post/detail/${postId}/comment/delete"
                      class="d-inline">
                    <sec:csrfInput/>
                    <input type="hidden" name="commentId"
                           value="${comment.commentId}"/>
                    <button type="submit"
                            class="btn btn-sm border-0 bg-transparent">
                        삭제
                    </button>
                </form>
            </c:if>
            <c:if test="${currentUserId != comment.userId && comment.parentId == null && currentUserId == postDetail.userId}">
                <form method="post"
                      action="/${boardId}/post/detail/${postId}/comment/adopt"
                      class="d-inline">
                    <sec:csrfInput/>
                    <input type="hidden" name="commentId"
                           value="${comment.commentId}"/>
                    <button type="submit"
                            class="btn btn-sm btn-outline-success">채택
                    </button>
                </form>
            </c:if>
        </div>
    </div>

    <!-- 대댓글 작성 폼 -->
    <div class="mt-3">
        <form method="post" action="/${requestScope.boardId}/post/detail/${requestScope.postId}/comment/add"
              enctype="multipart/form-data"
              id="replyForm-${requestScope.comment.commentId}"
              class="mt-2 replyForm" style="display:none;">
            <sec:csrfInput/>
            <input type="hidden" name="parentId" value="${requestScope.comment.commentId}"/>
            <div class="input-group">
                <!-- 이미지 업로드 버튼 -->
                <button type="button" class="btn btn-outline-dark addReplyImageButton"
                        data-reply-id="${requestScope.comment.commentId}">+
                </button>
                <input type="file" name="commentImages"
                       id="replyImageInput-${requestScope.comment.commentId}"
                       class="d-none"
                       multiple>
                <input type="text" class="form-control"
                       name="commentContent"
                       placeholder="답글 입력 . . .">
                <button type="submit" class="btn btn-outline-dark">입력</button>
            </div>
            <!-- 추가된 이미지 리스트 -->
            <ul id="addedReplyImagesList-${requestScope.comment.commentId}"
                class="list-group"></ul>
        </form>
    </div>

    <!-- 댓글 수정 폼 (초기에는 숨김) -->
    <div id="editForm-${requestScope.comment.commentId}" class="mt-3" style="display:none;">
        <form method="post" action="/${requestScope.boardId}/post/detail/${requestScope.postId}/comment/edit"
              enctype="multipart/form-data">
            <sec:csrfInput/>
            <input type="hidden" name="commentId" value="${requestScope.comment.commentId}"/>
            <input type="hidden" id="removedCommentImagesInput-${requestScope.comment.commentId}"
                   name="removedCommentImageIds"
                   value="">
            <div class="input-group">
                <!-- 이미지 업로드 버튼 -->
                <button type="button" class="btn btn-outline-dark addCommentImageButton"
                        data-comment-id="${requestScope.comment.commentId}">
                    +
                </button>
                <input type="file" name="addedCommentImages"
                       id="addedCommentImageInput-${requestScope.comment.commentId}" class="d-none"
                       multiple>
                <input type="text" class="form-control" name="commentContent"
                       value="${requestScope.comment.commentContent}">
                <button type="submit" class="btn btn-outline-dark">저장</button>
                <button type="button" class="btn btn-outline-dark commentEditCancelButton"
                        data-id="${requestScope.comment.commentId}">취소
                </button>
            </div>

            <!-- 기존 이미지 목록 -->
            <div class="mt-2">
                <strong>기존 첨부 이미지:</strong>
                <ul id="existingCommentImagesList-${requestScope.comment.commentId}" class="list-group">
                    <c:forEach var="image" items="${requestScope.comment.commentImages}">
                        <li class="list-group-item d-flex justify-content-between align-items-center rounded-3 shadow-sm m-1">
                            <a href="javascript:showImage('/image/${image.imageId}')"
                               class="text-center mx-auto">
                                    ${image.originalName}
                            </a>
                            <button type="button"
                                    class="btn btn-danger btn-sm remove-existing-comment-image-btn"
                                    data-comment-id="${requestScope.comment.commentId}"
                                    data-id="${image.imageId}">
                                X
                            </button>
                        </li>
                    </c:forEach>
                </ul>
            </div>

            <!-- 추가된 이미지 목록 -->
            <div class="mt-2">
                <strong>추가된 첨부 이미지:</strong>
                <ul id="addedCommentImagesList-${requestScope.comment.commentId}" class="list-group"></ul>
            </div>
        </form>
    </div>
</li>

<!-- 대댓글 렌더링 -->
<c:if test="${not empty requestScope.comment.replies}">
    <c:forEach var="reply" items="${requestScope.comment.replies}">
        <c:set var="comment" value="${reply}" scope="request"/>
        <jsp:include page="commentReply.jsp"/>
    </c:forEach>
</c:if>
