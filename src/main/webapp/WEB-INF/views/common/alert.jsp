<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script type="text/javascript">
    $(() => {
        const message = "${message}";
        const request = "${request}";

        if (message) {
            alert(message);
        }

        // POST 요청 경로
        const requestRoutes = {
            "SIGNUP": "/home",
            "ADD_POST": "/${boardId}/post",
            "MODIFY_POST": "/${boardId}/post/detail/${postId}",
            "DELETE_POST": "/${boardId}/post",
            "ADD_TEMP": "/${boardId}/post",
            "PUBLISH_POST": "/${boardId}/post/temp",
            "DELETE_TEMP": "/${boardId}/post/temp",
            "ADD_COMMENT": "/${boardId}/post/detail/${postId}",
            "MODIFY_COMMENT": "/${boardId}/post/detail/${postId}",
            "DELETE_COMMENT": "/${boardId}/post/detail/${postId}",
            "ADOPT_COMMENT": "/${boardId}/post/detail/${postId}",
            "LOGIN_ADMIN": "/admin",
            "ADD_BOARD": "/admin/board",
            "MODIFY_BOARD": "/admin/board",
            "DELETE_BOARD": "/admin/board"
        }

        if (requestRoutes[request]) {
            window.location.href = requestRoutes[request];
        } else {
            window.history.back();
        }
    });
</script>