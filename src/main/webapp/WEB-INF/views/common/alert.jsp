<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script type="text/javascript">
    $(() => {
        const message = "${message}";

        if (message) {
            alert(message);
        }

        window.history.back();
    });
</script>