<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>게시판 추가</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            $('input[name=imageFlag]').change(function () {
                toggleImageLimit();
            });

            // 게시판 추가 버튼 클릭 시
            $('#addBoardButton').click(function (e) {
                const boardName = $('#boardName').val();
                const boardType = $('#boardType').val();
                const boardOrder = $('#boardOrder').val();
                const categoryList = $('#categoryList').val();
                const noticeCount = $('#noticeCount').val();
                const accessLevel = $('#accessLevel').val();

                if (!boardName) {
                    alertMessage(e, "게시판 이름을 입력해주세요.")
                } else if (boardName.length < 2 || boardName.length > 20) {
                    alertMessage(e, "게시판 이름은 최소 4글자 이상, 최대 20글자 이하입니다.")
                } else if (!boardType) {
                    alertMessage(e, "게시판 종류를 선택해주세요.")
                } else if (!boardOrder) {
                    alertMessage(e, "게시글 노출 순서를 선택해주세요.")
                } else if (!categoryList) {
                    alertMessage(e, "카테고리를 1개 이상 입력해주세요.")
                } else if (!noticeCount) {
                    alertMessage(e, "공지사항 개수를 선택해주세요.")
                } else if (!accessLevel) {
                    alertMessage(e, "게시판 접근 허용 등급을 선택해주세요.")
                }
            });

            toggleImageLimit();

            function toggleImageLimit() {
                const imageFlagYes = $('#imageFlag-Y').is(':checked');
                if (imageFlagYes) {
                    $('#imageLimitSection').show();
                } else {
                    $('#imageLimitSection').hide();
                }
            }

            // 메세지 출력
            function alertMessage(e, message) {
                e.preventDefault();
                alert(message);
            }
        });
    </script>

    <script>
        $(() => {
            let categories = [];

            $('#categoryInput').on('keypress', function (e) {
                if (e.which === 13) { // Enter 키를 누르면
                    e.preventDefault();
                    const categoryName = $(this).val().trim();

                    if (categoryName !== '' && !categories.includes(categoryName)) {
                        categories.push(categoryName);
                        updateCategoryList();

                        const categoryTag = `
                        <span class="mr-2">
                            \${categoryName}
                            <button type="button" class="close ml-2" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </span>`;

                        $('#categoriesContainer').append(categoryTag);
                        $(this).val('');
                    }
                }
            });

            // 동적 생성된 말풍선 제거
            $('#categoriesContainer').on('click', '.close', function () {
                const index = $(this).closest('span').index();
                categories.splice(index, 1);
                $(this).closest('span').remove();
                updateCategoryList();
            });

            // 카테고리 리스트 업데이트
            function updateCategoryList() {
                $('#categoryList').val(categories.join(','));
            }
        });
    </script>
</head>
<body>
<div class="container d-flex justify-content-center align-items-center">
    <div class="col-md-8 col-lg-6">
        <h1 class="my-4 text-center">게시판 추가</h1>

        <form method="post" action="${pageContext.request.contextPath}/admin/board/process-add-board">
            <sec:csrfInput/>

            <div class="form-group mb-4">
                <label for="boardName">게시판 이름</label>
                <input type="text" id="boardName" name="boardName" class="form-control" placeholder="게시판 이름">
            </div>

            <div class="form-group mb-4">
                <label for="boardType">게시판 종류</label>
                <select id="boardType" name="boardType" class="form-control">
                    <option value="">게시판 종류 선택</option>
                    <option value="GALLERY">갤러리</option>
                    <option value="GENERAL">일반</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label for="boardOrder">메인페이지 게시판 순서</label>
                <select id="boardOrder" name="boardOrder" class="form-control">
                    <option value="">게시판 순서</option>
                    <c:forEach var="board" items="${boardList}">
                        <option value="${board.boardOrder}">${board.boardOrder}</option>
                    </c:forEach>
                    <option value="${boardList.size() + 1}">${boardList.size() + 1}</option>
                </select>
            </div>

            <div class="form-group">
                <input type="text" id="categoryInput" class="form-control"
                       placeholder="카테고리를 입력하고 엔터를 치세요.">
            </div>

            <div id="categoriesContainer" class="mt-3"></div>

            <input type="hidden" id="categoryList" name="categories">

            <div class="form-group mb-4">
                <label>이미지 허용</label>
                <div class="form-check form-check-inline">
                    <input type="radio" id="imageFlag-Y" name="imageFlag" value="Y" class="form-check-input" checked>
                    <label for="imageFlag-Y" class="form-check-label">허용</label>
                </div>
                <div class="form-check form-check-inline">
                    <input type="radio" id="imageFlag-N" name="imageFlag" value="N" class="form-check-input">
                    <label for="imageFlag-N" class="form-check-label">허용 안함</label>
                </div>
            </div>

            <div id="imageLimitSection" class="form-group mb-4">
                <label for="imageLimit">게시글 이미지 최대 개수</label>
                <select id="imageLimit" name="imageLimit" class="form-control">
                    <option value="">게시글 이미지 최대 개수 선택</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label for="noticeCount">공지사항 개수</label>
                <select id="noticeCount" name="noticeCount" class="form-control">
                    <option value="">공지사항 개수 선택</option>
                    <option value="0">0</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label>댓글 허용</label>
                <div class="form-check form-check-inline">
                    <input type="radio" id="commentFlag-Y" name="commentFlag" value="Y" class="form-check-input"
                           checked>
                    <label for="commentFlag-Y" class="form-check-label">허용</label>
                </div>
                <div class="form-check form-check-inline">
                    <input type="radio" id="commentFlag-N" name="commentFlag" value="N" class="form-check-input">
                    <label for="commentFlag-N" class="form-check-label">허용 안함</label>
                </div>
            </div>

            <div class="form-group mb-4">
                <label for="accessLevel">게시판 접근 허용 등급</label>
                <select id="accessLevel" name="accessLevel" class="form-control">
                    <option value="">접근 허용 등급</option>
                    <option value="0">전체</option>
                    <option value="1">LEVEL1</option>
                    <option value="2">LEVEL2</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label>활성화 여부</label>
                <div class="form-check form-check-inline">
                    <input type="radio" id="activateFlag-Y" name="activateFlag" value="Y" class="form-check-input"
                           checked>
                    <label for="activateFlag-Y" class="form-check-label">활성화</label>
                </div>
                <div class="form-check form-check-inline">
                    <input type="radio" id="activateFlag-N" name="activateFlag" value="N" class="form-check-input">
                    <label for="activateFlag-N" class="form-check-label">비활성화</label>
                </div>
            </div>

            <button type="submit" id="addBoardButton" class="btn btn-primary">추가</button>
            <button type="button" class="btn btn-secondary" onclick="location.href='/admin/board'">취소</button>
        </form>
    </div>
</div>
</body>
</html>