<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Under Development</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body, html {
            height: 100%;
            margin: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #f8f9fa;
        }

        .development-card {
            border-radius: 15px;
            padding: 40px;
            text-align: center;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            background-color: #fff;
            max-width: 500px;
        }

        .development-card img {
            width: 150px;
            margin-bottom: 20px;
        }

        .development-card h1 {
            font-size: 2rem;
            color: #343a40;
        }

        .development-card p {
            font-size: 1.2rem;
            color: #6c757d;
        }

        .development-card .btn {
            margin-top: 20px;
            font-size: 1rem;
        }
    </style>
</head>
<body>
<div class="development-card">
    <img src="https://cdn-icons-png.flaticon.com/512/5037/5037244.png" alt="Under Development">
    <h1>Coming Soon!</h1>
    <p>This feature is currently under development.</p>
    <p>We are working hard to bring it to you soon. Stay tuned!</p>
    <a href="${pageContext.request.contextPath}/admin" class="btn btn-primary">Go to Homepage</a>
</div>
</body>
</html>
