<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="kr">
<head>
    <title>SignUp | 회원가입</title>

    <jsp:include page="/WEB-INF/views/common/head-css.jsp"/>
    <jsp:include page="/WEB-INF/views/common/head-page-meta.jsp"/>

    <meta id="_csrf" name="_csrf" content="${_csrf.token}"/>
    <meta id="_csrf_header" name="_csrf_header" content="${_csrf.headerName}"/>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            disableSignUpForm(true);

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
                        disableSignUpForm(false);
                        $('#email').prop('disabled', true);
                        $('#sendEmailVerificationButton').prop('disabled', true);
                        $('#emailVerificationCode').prop('disabled', true);
                        $('#verifyEmailCodeButton').prop('disabled', true);
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

            // 회원가입 폼 활성화 / 비활성화
            function disableSignUpForm(disable) {
                $('#signupForm input').prop('disabled', disable);
                $('#signupButton').prop('disabled', disable);
            }

            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }
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
<body>
<div class="loader-bg">
    <div class="loader-track">
        <div class="loader-fill"></div>
    </div>
</div>

<div class="auth-main">
    <div class="auth-wrapper v3">
        <div class="auth-form">
            <div class="card mt-5">
                <div class="card-body">
                    <%-- 로고 추가 --%>
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/home" class="b-brand text-primary">
                            <img src="${pageContext.request.contextPath}/resources/assets/images/board-logo.png"
                                 alt="로고"
                                 class="logo" style="max-width: 100%">
                        </a>
                    </div>
                    <div class="row">
                        <div class="d-flex justify-content-center">
                            <div class="auth-header">
                                <h2 class="text-secondary mt-5"><b>회원가입</b></h2>
                                <p class="f-16 mt-2">이메일 인증 하기</p>
                            </div>
                        </div>
                    </div>

                    <div class="form-floating mb-3 d-flex">
                        <input type="email"
                               class="form-control"
                               id="email"
                               name="email"
                               placeholder="이메일" required>
                        <label for="email">인증 메일 입력</label>
                        <button type="button"
                                class="btn btn-secondary m-1"
                                id="sendEmailVerificationButton">
                            <i class="ti ti-mail-forward"></i>
                        </button>
                    </div>

                    <div class="form-floating mb-3 d-flex">
                        <input type="number"
                               class="form-control"
                               id="inputVerificationCode"
                               name="inputVerificationCode"
                               placeholder="Verify Code">
                        <label for="inputVerificationCode">인증 코드 입력</label>
                        <button type="button"
                                class="btn btn-secondary m-1"
                                id="verifyEmailCodeButton">
                            <i class="ti ti-shield-check"></i>
                        </button>
                    </div>

                    <div class="saprator mt-3">
                        <span>이메일 인증 후 작성 가능합니다.</span>
                    </div>

                    <form method="post" action="${pageContext.request.contextPath}/user/process-signup" id="signupForm">
                        <sec:csrfInput/>
                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="userId"
                                   name="userId"
                                   placeholder="User Id">
                            <label for="userId">아이디</label>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="password"
                                           class="form-control"
                                           id="userPassword"
                                           name="userPassword"
                                           placeholder="User Password">
                                    <label for="userPassword">비밀번호</label>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="password"
                                           class="form-control"
                                           id="userPasswordCheck"
                                           name="userPasswordCheck"
                                           placeholder="User Password Check">
                                    <label for="userPasswordCheck">비밀번호 확인</label>
                                </div>
                            </div>
                        </div>

                        <div class="form-floating mb-3">
                            <input type="text"
                                   class="form-control"
                                   id="userName"
                                   name="userName"
                                   placeholder="Password">
                            <label for="userName">이름</label>
                        </div>

                        <div class="form-floating mb-3 d-flex">
                            <input type="text"
                                   class="form-control bg-gray-200"
                                   id="roadAddress"
                                   name="address"
                                   placeholder="roadAddress" readonly>
                            <label for="roadAddress">도로명 주소 찾기</label>

                            <button type="button" class="btn btn-secondary m-1" onclick="execDaumPostcode()">
                                <i class="ti ti-search"></i>
                            </button>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="text"
                                           class="form-control bg-gray-200"
                                           id="postcode"
                                           name="zipcode"
                                           placeholder="postcode" readonly>
                                    <label for="postcode">우편번호</label>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <div class="form-floating mb-3">
                                    <input type="text"
                                           class="form-control"
                                           id="detailAddress"
                                           name="detailAddress"
                                           placeholder="detailAddress">
                                    <label for="detailAddress">상세주소</label>
                                </div>
                            </div>
                        </div>
                    </form>

                    <div class="d-grid mt-4">
                        <button type="submit"
                                class="btn btn-secondary p-2"
                                id="signupButton"
                                form="signupForm">회원가입
                        </button>
                    </div>

                    <hr>

                    <div class="d-flex justify-content-center">
                        <button class="d-flex btn" onclick="location.href='/home'">
                            홈페이지 돌아가기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer-js.jsp"/>

</body>
</html>
