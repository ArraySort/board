<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
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

            // 게시판 수정 버튼 클릭 시
            $('#editBoardButton').click(function (e) {
                const boardName = $('#boardName').val();
                const boardType = $('#boardType').val();
                const boardOrder = $('#boardOrder').val();
                const categoryCount = $('#categoriesContainer span').length;
                const noticeCount = $('#noticeCount').val();
                const accessLevel = $('#accessLevel').val();

                console.log(categoryCount);

                if (!boardName) {
                    alertMessage(e, "게시판 이름을 입력해주세요.")
                } else if (boardName.length < 2 || boardName.length > 20) {
                    alertMessage(e, "게시판 이름은 최소 4글자 이상, 최대 20글자 이하입니다.")
                } else if (!boardType) {
                    alertMessage(e, "게시판 종류를 선택해주세요.")
                } else if (categoryCount < 1) {
                    alertMessage(e, "카테고리는 1개 이상 입력되어 있어야 합니다.");
                } else if (!boardOrder) {
                    alertMessage(e, "게시글 노출 순서를 선택해주세요.")
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
            let addedCategoryList = [];    // 추가된 카테고리
            let removedCategoryIds = [];   // 삭제된 카테고리 ID

            $('#categoryInput').on('keypress', function (e) {
                if (e.which === 13) { // Enter 키를 누르면
                    e.preventDefault();
                    const categoryName = $(this).val().trim();

                    if (categoryName !== '' && !addedCategoryList.includes(categoryName)) {
                        addedCategoryList.push(categoryName);
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

                addedCategoryList.splice(index, 1);
                $(this).closest('span').remove();
                updateCategoryList();
                console.log(removedCategoryIds);
            });

            $('.remove-existing-category-button').on('click', function () {
                const index = $(this).closest('span').index();
                const categoryId = $(this).data('category-id');

                removedCategoryIds.push(categoryId);
                $(this).closest('span').remove();
                updateRemovedCategoryIds();
                console.log(removedCategoryIds);
            });

            // 카테고리 리스트 업데이트
            function updateCategoryList() {
                $('#addedCategoryList').val(addedCategoryList.join(','));
                $('#categoryList').val(addedCategoryList.join(','));
            }

            // 삭제 리스트 업데이트
            function updateRemovedCategoryIds() {
                $('#removedCategoryIds').val(removedCategoryIds.join(','));
            }
        });
    </script>
</head>
<body>
<div class="container d-flex justify-content-center align-items-center">
    <div class="col-md-8 col-lg-6">
        <h1 class="my-4 text-center">게시판 추가</h1>

        <form method="post"
              action="${pageContext.request.contextPath}/admin/board/${boardDetail.boardId}/process-edit-board">
            <sec:csrfInput/>

            <div class="form-group mb-4">
                <label for="boardName">게시판 이름</label>
                <input type="text"
                       id="boardName"
                       name="boardName"
                       class="form-control"
                       value="${boardDetail.boardName}"
                       placeholder="게시판 이름">
            </div>

            <div class="form-group mb-4">
                <label for="boardType">게시판 종류</label>
                <select id="boardType" name="boardType" class="form-control">
                    <option value="">게시판 종류 선택</option>
                    <option value="GALLERY" ${boardDetail.boardType == 'GALLERY' ? 'selected' : ''}>갤러리</option>
                    <option value="GENERAL" ${boardDetail.boardType == 'GENERAL' ? 'selected' : ''}>일반</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label for="boardOrder">메인페이지 게시글 노출 순서</label>
                <select id="boardOrder" name="boardOrder" class="form-control">
                    <option value="">메인페이지 게시글 노출 순서</option>
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                </select>
            </div>

            <div class="form-group">
                <input type="text" id="categoryInput" class="form-control"
                       placeholder="카테고리를 입력하고 엔터를 치세요.">
            </div>

            <div id="categoriesContainer" class="mt-3">
                <c:forEach var="category" items="${categoryList}">
                    <span class="mr-2">${category.categoryName}
                       <button type="button" class="close ml-2 remove-existing-category-button" aria-label="Close"
                               data-category-id="${category.categoryId}">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </span>
                </c:forEach>
            </div>

            <input type="hidden" id="categoryList" name="categoryList"
                   value="<c:forEach var='category' items='${categoryList}'>${category.categoryName},</c:forEach>">
            <input type="hidden" id="addedCategoryList" name="addedCategoryList">
            <input type="hidden" id="removedCategoryIds" name="removedCategoryIds">


            <div class="form-group mb-4">
                <label>이미지 허용</label>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="imageFlag-Y" name="imageFlag" value="Y"
                    ${boardDetail.imageFlag == 'Y' ? 'checked' : ''}>
                    <label for="imageFlag-Y" class="form-check-label">허용</label>
                </div>
                <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" id="imageFlag-N" name="imageFlag" value="N"
                    ${boardDetail.imageFlag == 'N' ? 'checked' : ''}>
                    <label for="imageFlag-N" class="form-check-label">허용 안함</label>
                </div>
            </div>

            <div id="imageLimitSection" class="form-group mb-4">
                <label for="imageLimit">게시글 이미지 최대 개수</label>
                <select id="imageLimit" name="imageLimit" class="form-control">
                    <option value="">게시글 이미지 최대 개수 선택</option>
                    <option value="1" ${boardDetail.imageLimit == '1' ? 'selected' : ''}>1</option>
                    <option value="2" ${boardDetail.imageLimit == '2' ? 'selected' : ''}>2</option>
                    <option value="3" ${boardDetail.imageLimit == '3' ? 'selected' : ''}>3</option>
                    <option value="4" ${boardDetail.imageLimit == '4' ? 'selected' : ''}>4</option>
                    <option value="5" ${boardDetail.imageLimit == '5' ? 'selected' : ''}>5</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label for="noticeCount">공지사항 개수</label>
                <select id="noticeCount" name="noticeCount" class="form-control">
                    <option value="">공지사항 개수 선택</option>
                    <option value="0" ${boardDetail.noticeCount == '0' ? 'selected' : ''}>0</option>
                    <option value="1" ${boardDetail.noticeCount == '1' ? 'selected' : ''}>1</option>
                    <option value="2" ${boardDetail.noticeCount == '2' ? 'selected' : ''}>2</option>
                    <option value="3" ${boardDetail.noticeCount == '3' ? 'selected' : ''}>3</option>
                </select>
            </div>

            <div class="form-group mb-4">
                <label>댓글 허용</label>
                <div class="form-check form-check-inline">
                    <input type="radio" id="commentFlag-Y" name="commentFlag" value="Y" class="form-check-input"
                    ${boardDetail.commentFlag == 'Y' ? 'checked' : ''}>
                    <label for="commentFlag-Y" class="form-check-label">허용</label>
                </div>
                <div class="form-check form-check-inline">
                    <input type="radio" id="commentFlag-N" name="commentFlag" value="N" class="form-check-input"
                    ${boardDetail.commentFlag == 'N' ? 'checked' : ''}>
                    <label for="commentFlag-N" class="form-check-label">허용 안함</label>
                </div>
            </div>

            <div class="form-group mb-4">
                <label for="accessLevel">게시판 접근 허용 등급</label>
                <select id="accessLevel" name="accessLevel" class="form-control">
                    <option value="">접근 허용 등급</option>
                    <option value="0" ${boardDetail.accessLevel == '0' ? 'selected' : ''}>전체</option>
                    <option value="1" ${boardDetail.accessLevel == '1' ? 'selected' : ''}>LEVEL1</option>
                    <option value="2" ${boardDetail.accessLevel == '2' ? 'selected' : ''}>LEVEL2</option>
                </select>
            </div>

            <button type="submit" id="editBoardButton" class="btn btn-primary">수정</button>
            <button type="button" class="btn btn-secondary" onclick="location.href='/admin/board'">취소</button>
        </form>
    </div>
</div>
</body>
</html>