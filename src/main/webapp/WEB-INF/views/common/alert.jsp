<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script type="text/javascript">
    $(() => {
        const message = "${message}";
        const request = "${request}"

        if (message) {
            alert(message);
        }

        if (request === "SIGNUP") {
            window.location.href = "/home";
        } else if (request === "ADD_POST") {
            window.location.href = "/${boardId}/post";
        } else if (request === "MODIFY_POST") {
            window.location.href = "/post/detail/${postId}"
        } else if (request === "DELETE_POST") {
            window.location.href = "/post";
        } else {
            window.history.back();
        }
    });
</script>