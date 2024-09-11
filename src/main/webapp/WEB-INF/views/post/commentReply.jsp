<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<li class="list-group-item" style="margin-left: ${requestScope.comment.depth * 20}px">
    <!-- 댓글 상단: 작성자, 내용, 수정/삭제 버튼 -->
    <i class="ti ti-arrow-down-right"></i>
    <div class="d-flex align-items-start position-relative">
        <div class="me-3 position-absolute top-0 start-0">
            <strong>${comment.userName}</strong>
            <div class="text-muted mt-2">
                <small>작성시간: <fmt:formatDate value="${comment.createdAt}"
                                             pattern="yyyy-MM-dd HH:mm"/></small><br/>
                <small>수정시간: <fmt:formatDate value="${comment.updatedAt}"
                                             pattern="yyyy-MM-dd HH:mm"/></small>
            </div>
        </div>
        <div class="flex-grow-1 text-center m-4" style="font-size: 1rem;">
            ${comment.commentContent}
        </div>
        <div class="flex-column align-items-end position-absolute top-0 end-0">
            <button class="btn btn-sm mx-1 ${comment.hasLiked ? 'btn-primary' : 'btn-outline-primary'} commentLikeButton"
                    id="commentLikeButton-${comment.commentId}" type="button"
                    data-comment-id="${comment.commentId}">
                <i class="ti ti-thumb-up"></i><span
                    id="commentLikeCount-${comment.commentId}">${comment.likeCount}</span>
            </button>
            <button class="btn btn-sm ${comment.hasDisliked ? 'btn-secondary' : 'btn-outline-secondary'} commentDislikeButton"
                    id="commentDislikeButton-${comment.commentId}" type="button"
                    data-comment-id="${comment.commentId}">
                <i class="ti ti-thumb-down"></i><span
                    id="commentDislikeCount-${comment.commentId}">${comment.dislikeCount}</span>
            </button>
        </div>
    </div>

    <!-- 댓글 하단: 첨부 이미지, 수정/삭제 버튼, 대댓글 보기, 답글 달기 -->
    <div class="mt-3 position-relative">
        <!-- 첨부 이미지 -->
        <c:if test="${not empty comment.commentImages}">
            <div class="position-absolute top-0 start-0">
                <strong>[첨부 이미지]</strong>
                <c:forEach var="image" items="${comment.commentImages}"
                           varStatus="status">
                    <a href="javascript:showImage('/image/${image.imageId}')">[${status.index + 1}]</a>
                </c:forEach>
            </div>
        </c:if>

        <!-- 대댓글 보기 및 답글 달기 버튼 -->
        <div class="text-center">
            <button class="btn btn-sm btn-outline-secondary showReplyFormButton"
                    data-id="${comment.commentId}">
                답글 달기
            </button>
        </div>

        <!-- 수정/삭제 버튼 -->
        <div class="position-absolute bottom-0 end-0">
            <c:if test="${currentUserId == comment.userId}">
                <button class="btn btn-sm btn-outline-primary commentEditButton"
                        data-id="${comment.commentId}">수정
                </button>
                <form method="post"
                      action="/${boardId}/post/detail/${postId}/comment/delete"
                      class="m-0 d-inline">
                    <sec:csrfInput/>
                    <input type="hidden" name="commentId"
                           value="${comment.commentId}"/>
                    <button type="submit" class="btn btn-sm btn-outline-danger">
                        삭제
                    </button>
                </form>
            </c:if>
            <c:if test="${currentUserId != comment.userId && comment.parentId == null && currentUserId == postDetail.userId}">
                <form method="post"
                      action="/${boardId}/post/detail/${postId}/comment/adopt"
                      class="m-0 d-inline">
                    <sec:csrfInput/>
                    <input type="hidden" name="commentId"
                           value="${comment.commentId}"/>
                    <button type="submit" class="btn btn-sm btn-outline-dark">
                        <i class="ti ti-circle-check"></i>
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
                <button type="button" class="btn btn-outline-secondary addReplyImageButton"
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
                <button type="button" class="btn btn-outline-secondary addCommentImageButton"
                        data-comment-id="${requestScope.comment.commentId}">
                    +
                </button>
                <input type="file" name="addedCommentImages"
                       id="addedCommentImageInput-${requestScope.comment.commentId}" class="d-none"
                       multiple>
                <input type="text" class="form-control" name="commentContent"
                       value="${requestScope.comment.commentContent}">
                <button type="submit" class="btn btn-success">저장</button>
                <button type="button" class="btn btn-secondary commentEditCancelButton"
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
