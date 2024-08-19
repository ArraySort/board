<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>SignUp</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <meta id="_csrf" name="_csrf" content="${_csrf.token}"/>
    <meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}"/>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }

            // CSRF
            $.ajaxSetup({
                beforeSend: function (xhr) {
                    xhr.setRequestHeader($('meta[name="_csrf_header"]').attr('content'), $('meta[name="_csrf"]').attr('content'));
                }
            });

            // 인증코드 전송 버튼
            $('#sendEmailVerificationButton').on('click', function (e) {
                const email = $('#email').val();

                if (!email) {
                    e.preventDefault();
                    alertMessage(e, "이메일을 올바르게 입력해주십시오.");
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: "${pageContext.request.contextPath}/user/send-email-verification",
                    data: {email: email},
                    success: function (response) {
                        alert(response);
                        $('#emailVerificationCodeForm').show();
                    },
                    error: function (xhr) {
                        alertMessage(e, "인증번호 전송 중 오류가 발생했습니다.");
                    }
                });
            });

            // 인증 버튼
            $('#verifyEmailCodeButton').on('click', function (e) {
                const inputVerificationCode = $('#inputVerificationCode').val();

                if (!inputVerificationCode) {
                    e.preventDefault();
                    alertMessage(e, "입력한 이메일로 전송된 인증코드를 입력하십시오.");
                    return;
                }

                $.ajax({
                    type: "POST",
                    url: "${pageContext.request.contextPath}/user/verify-email-code",
                    data: {inputVerificationCode: inputVerificationCode},
                    success: function (response) {
                        alert(response);

                        // 인증 완료 시 입력 태그 및 버튼 비활성화
                        $('#email').prop('disabled', true);
                        $('#sendEmailVerificationButton').prop('disabled', true);
                        $('#emailVerificationCode').prop('disabled', true);
                        $('#verifyEmailCodeButton').prop('disabled', true);

                        $('#signupForm').show();
                    },
                    error: function (xhr) {
                        alert("인증에 실패했습니다.");
                    }
                });
            });

            // 회원가입 버튼
            $('#signupButton').on('click', function (e) {
                const userId = $('#userId').val();
                const userPassword = $('#userPassword').val();
                const userName = $('#userName').val();
                const userPasswordCheck = $('#userPasswordCheck').val();

                if (!(userId && userPassword && userName && userPasswordCheck)) {
                    alertMessage(e, "아이디와 비밀번호, 비밀번호 확인, 이름 입력은 필수입니다.");
                } else if (userId.length < 4 || userId.length > 20) {
                    alertMessage(e, "아이디는 최소 4글자 이상, 최대 20글자 이하입니다.");
                } else if (userPassword.length < 4 || userPassword.length > 20) {
                    alertMessage(e, "패스워드는 최소 4글자 이상, 최대 20글자 이하입니다.");
                } else if (userName.length < 2 || userName.length > 10) {
                    alertMessage(e, "이름은 최소 2글자 이상, 최대 10글자 이하입니다.");
                } else if (userPassword !== userPasswordCheck) {
                    alertMessage(e, "입력하신 비밀번호와 확인 값이 일치하지 않습니다.");
                }
            });
        });
    </script>

    <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
    <script>
        function execDaumPostcode() {
            new daum.Postcode({
                oncomplete: function (data) {
                    const roadAddr = data.roadAddress;
                    let extraRoadAddr = '';

                    if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                        extraRoadAddr += data.bname;
                    }
                    if (data.buildingName !== '' && data.apartment === 'Y') {
                        extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    if (extraRoadAddr !== '') {
                        extraRoadAddr = ' (' + extraRoadAddr + ')';
                    }

                    document.getElementById('postcode').value = data.zonecode;
                    document.getElementById("roadAddress").value = roadAddr;

                    if (roadAddr !== '') {
                        document.getElementById("extraAddress").value = extraRoadAddr;
                    } else {
                        document.getElementById("extraAddress").value = '';
                    }

                    var guideTextBox = document.getElementById("guide");
                    if (data.autoRoadAddress) {
                        var expRoadAddr = data.autoRoadAddress + extraRoadAddr;
                        guideTextBox.innerHTML = '(예상 도로명 주소 : ' + expRoadAddr + ')';
                        guideTextBox.style.display = 'block';
                    } else {
                        guideTextBox.innerHTML = '';
                        guideTextBox.style.display = 'none';
                    }
                }
            }).open({autoClose: true});
        }
    </script>

</head>
<body class="bg-light">

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card mt-5">
                <div class="card-header text-center">
                    <h1>회원가입 페이지</h1>
                </div>
                <div class="card-body">

                    <!-- 이메일 인증 요청 폼 -->
                    <div class="input-group">
                        <input type="email" class="form-control" id="email" name="email" placeholder="이메일"
                               required>
                        <button type="button"
                                class="btn btn-primary"
                                id="sendEmailVerificationButton">인증 코드 전송
                        </button>
                    </div>

                    <br/>

                    <!-- 인증 코드 입력 폼 -->
                    <div class="input-group" id="emailVerificationCodeForm" style="display: none">
                        <input type="number" class="form-control" id="inputVerificationCode"
                               name="inputVerificationCode" placeholder="인증 코드를 입력하세요" required>
                        <button type="button"
                                class="btn btn-secondary"
                                id="verifyEmailCodeButton">인증 코드 확인
                        </button>
                    </div>

                    <!-- 회원가입 입력 폼 -->
                    <form method="post" action="${pageContext.request.contextPath}/user/process-signup" id="signupForm"
                          style="display:none;">
                        <sec:csrfInput/>

                        <div class="mb-3">
                            <label for="userId" class="form-label">아이디</label>
                            <input type="text" class="form-control" id="userId" name="userId" placeholder="아이디"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label for="userPassword" class="form-label">비밀번호</label>
                            <input type="password" class="form-control" id="userPassword" name="userPassword"
                                   placeholder="비밀번호" required>
                        </div>

                        <div class="mb-3">
                            <label for="userPasswordCheck" class="form-label">비밀번호 확인</label>
                            <input type="password" class="form-control" id="userPasswordCheck" name="userPasswordCheck"
                                   placeholder="비밀번호 확인" required>
                        </div>

                        <div class="mb-3">
                            <label for="userName" class="form-label">이름</label>
                            <input type="text" class="form-control" id="userName" name="userName" placeholder="이름"
                                   required>
                        </div>

                        <div class="mb-3">
                            <label for="postcode" class="form-label">우편번호</label>
                            <div class="input-group">
                                <input type="text" class="form-control" id="postcode" placeholder="우편번호" name="zipcode"
                                       readonly>
                                <button type="button" class="btn btn-secondary" onclick="execDaumPostcode()">
                                    우편번호 찾기
                                </button>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label for="roadAddress" class="form-label">도로명주소</label>
                            <input type="text" class="form-control" id="roadAddress" placeholder="도로명주소" name="address"
                                   readonly>
                        </div>

                        <div class="mb-3">
                            <label for="detailAddress" class="form-label">상세주소</label>
                            <input type="text" class="form-control" id="detailAddress" placeholder="상세주소"
                                   name="addressDetail">
                        </div>

                        <div id="guide" class="form-text text-muted mb-3" style="display:none;"></div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary" id="signupButton">회원가입</button>
                        </div>
                    </form>

                    <div class="d-grid mt-2">
                        <button type="button" class="btn btn-outline-primary" onclick="location.href='/home'">홈 페이지로
                            이동
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
