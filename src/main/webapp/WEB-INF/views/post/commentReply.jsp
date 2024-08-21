<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<li class="list-group-item">
    <!-- 댓글 상단: 작성자, 내용, 수정/삭제 버튼 -->
    <div class="d-flex justify-content-between">
        <!-- 작성자 -->
        <div class="comment-author">
            <strong>${requestScope.comment.userName}</strong>
        </div>

        <!-- 내용 -->
        <div class="flex-grow-1 text-center">
            ${comment.commentContent}
        </div>

        <!-- 수정/삭제 버튼 -->
        <c:if test="${requestScope.currentUserId == requestScope.comment.userId}">
            <div class="ms-3 d-flex align-items-center">
                <button class="btn btn-sm btn-outline-primary me-2 commentEditButton"
                        data-id="${requestScope.comment.commentId}">수정
                </button>
                <form method="post" action="/${requestScope.boardId}/post/detail/${requestScope.postId}/comment/delete"
                      class="m-0">
                    <sec:csrfInput/>
                    <input type="hidden" name="commentId" value="${requestScope.comment.commentId}"/>
                    <button type="submit" class="btn btn-sm btn-outline-danger">삭제</button>
                </form>
            </div>
        </c:if>
    </div>

    <!-- 댓글 하단: 첨부 이미지, 작성 시간, 수정 시간 -->
    <div class="d-flex justify-content-between mt-2">
        <!-- 첨부 이미지 -->
        <div class="comment-images">
            <c:if test="${not empty requestScope.comment.commentImages}">
                <strong>첨부 이미지:</strong>
                <c:forEach var="image" items="${requestScope.comment.commentImages}">
                    <a href="javascript:showImage('/image/${image.imageId}')">
                        [${image.originalName}]
                    </a>
                </c:forEach>
            </c:if>
        </div>

        <!-- 작성 시간 및 수정 시간 -->
        <div class="text-end text-muted">
            <small>작성시간:
                <fmt:formatDate value="${requestScope.comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/></small>
            <br/>
            <small>수정시간:
                <fmt:formatDate value="${requestScope.comment.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></small>
        </div>
    </div>


    <!-- 대댓글 작성 폼 -->
    <div class="mt-3">
        <button class="btn btn-sm btn-outline-secondary showReplyFormButton"
                data-id="${requestScope.comment.commentId}">
            답글 달기
        </button>
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
                <input type="file" name="replyImages"
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
                class="d-flex flex-column align-items-center"></ul>
        </form>
    </div>

    <!-- 대댓글 렌더링 -->
    <c:if test="${not empty requestScope.comment.replies}">
        <ul class="list-group mt-3">
            <c:forEach var="reply" items="${requestScope.comment.replies}">
                <c:set var="comment" value="${reply}" scope="request"/>
                <jsp:include page="commentReply.jsp"/>
            </c:forEach>
        </ul>
    </c:if>
</li>
