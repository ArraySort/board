<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Post Edit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">

    <style>
        #popupOverlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.7);
            z-index: 1000;
            justify-content: center;
            align-items: center;
        }

        #popup {
            position: relative;
            width: 80%;
            max-width: 800px;
            height: 80%;
            max-height: 600px;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
        }

        #popup img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }
    </style>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script type="text/javascript">
        $(() => {
            let removedImageIds = [];       // 기존 이미지에서 삭제된 이미지 ID : String
            let addedImages = [];       // 새로 추가된 이미지 : MultipartFile

            // 업로드 이미지 미리보기
            $('#thumbnailImage').on('change', function () {
                const file = this.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        $('#imagePreview').attr('src', e.target.result).show();
                    };
                    reader.readAsDataURL(file);
                } else {
                    $('#imagePreview').attr('src', '/image/${postDetail.imageId}').show();
                }
            });

            // 기존 이미지 삭제
            $(document).on('click', '.remove-image-btn', function () {
                const imageId = $(this).data('image-id');
                removedImageIds.push(imageId);
                $(this).closest('li').remove();
                $('#removedImagesInput').val(removedImageIds);
            });

            // 저장 버튼 눌렀을 때 검증
            $('#saveButton').on('click', function (e) {
                validateForm(e);
            });

            // 새로운 이미지 추가
            $('#imageInput').on('change', function () {
                const files = this.files;

                addedImages = [];

                for (let i = 0; i < files.length; i++) {
                    const file = files[i];
                    const imageUrl = URL.createObjectURL(file);

                    addedImages.push(file);

                    $('#addedImagesList').append(`
                        <li class="list-group-item d-flex justify-content-center align-items-center">
                            <a href="javascript:showImage('\${imageUrl}')" class="text-center mx-auto">
                                \${file.name}
                            </a>
                            <button type="button" class="btn btn-danger btn-sm remove-added-image-btn ml-auto">X</button>

                        </li>
                    `);
                }

                updateAddedImagesInput();
            });


            // 추가된 이미지 삭제
            $(document).on('click', '.remove-added-image-btn', function () {
                const index = $(this).closest('li').index();

                addedImages.splice(index, 1);
                $(this).closest('li').remove();

                updateAddedImagesInput();
            });

            // 이미지 추가 버튼 클릭 시 파일 입력 필드 클릭
            $('#addImageBtn').on('click', function () {
                $('#imageInput').click();
            });

            function updateAddedImagesInput() {
                let dataTransfer = new DataTransfer();
                addedImages.forEach(file => {
                    dataTransfer.items.add(file);
                });
                $('#imageInput')[0].files = dataTransfer.files;
            }

        });

        // 메세지 출력
        function alertMessage(e, message) {
            e.preventDefault();
            alert(message);
        }

        // 입력 폼 검증
        function validateForm(e) {
            const category = $('#category').val();
            const title = $('#title').val();
            const content = $('#content').val();

            if (!(category && title && content)) {
                alertMessage(e, "카테고리, 제목, 내용은 필수 입력사항입니다.");
            } else if (title.length < 1 || title.length > 50) {
                alertMessage(e, "제목은 최소 1글자, 최대 50글자이어야 합니다.");
            } else if (content.length < 1 || content.length > 500) {
                alertMessage(e, "내용은 최소 1글자, 최대 500글자이어야 합니다.");
            }
        }

        // 이미지 팝업
        function showImage(imageUrl) {
            const popupOverlay = document.getElementById('popupOverlay');
            const popupImage = document.getElementById('popupImage');
            popupImage.src = imageUrl;
            popupOverlay.style.display = 'flex';
        }

        // 팝업 닫기
        function closePopup() {
            const popupOverlay = document.getElementById('popupOverlay');
            popupOverlay.style.display = 'none';
        }

        // 팝업에 대한 폼 키보드 제어
        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') {
                closePopup();
            }
        });
    </script>
</head>
<body>

<div style="text-align: center">
    <h1>게시글 수정</h1>
    <div class="container" style="max-width: 850px;">
        <form enctype="multipart/form-data" method="post"
              action="/${boardId}/post/detail/${postDetail.postId}/edit">
            <sec:csrfInput/>

            <input type="hidden" name="search" value="${page.search}">
            <input type="hidden" name="searchType" value="${page.searchType}">
            <input type="hidden" name="sortType" value="${page.sortType}">
            <input type="hidden" name="page" value="${page.page}">

            <!-- 삭제된 이미지 ID -->
            <input type="hidden" id="removedImagesInput" name="removedImageIds" value="">

            <div>작성자 : ${postDetail.userName}</div>
            <div>카테고리 : ${postDetail.categoryName}</div>
            <div>현재 게시판 : ${postDetail.boardName}</div>

            <c:if test="${boardDetail.boardType == 'GALLERY'}">
                <div>현재 썸네일 이미지</div>
                <img src="/image/${postDetail.imageId}" id="imagePreview" style="height: 200px; width: 30%"
                     alt="${postDetail.title}">
                <div>
                    <div>썸네일 이미지 수정</div>
                    <input type="file" name="thumbnailImage" id="thumbnailImage">
                </div>
            </c:if>

            <div>
                <select name="categoryId" aria-label="category select" id="category">
                    <option value="">카테고리 선택</option>
                    <c:forEach var="category" items="${categories}">
                    <option value="${category.categoryId}"
                    <c:if test="${category.categoryName == postDetail.categoryName}">
                            selected
                    </c:if>>
                            ${category.categoryName}
                        </c:forEach>
                </select>
            </div>

            <div>
                <label for="title">제목 : </label>
                <input type="text"
                       name="title"
                       placeholder="제목"
                       value="${postDetail.title}"
                       id="title"/>
            </div>

            <br/>

            <div>
                <label for="content">내용 : </label>
                <textarea type="text"
                          name="content"
                          placeholder="내용"
                          id="content">${postDetail.content}</textarea>
            </div>

            <h3>이미지 목록</h3>

            <c:choose>
                <c:when test="${boardDetail.imageFlag == 'Y'}">
                    <!-- 기존 이미지 리스트 -->
                    <ul id="existingImagesList" class="d-flex flex-column align-items-center">
                        <c:forEach var="image" items="${images}">
                            <li class="list-group-item d-flex justify-content-center align-items-center"
                                style="width: 50%">
                                <a href="javascript:showImage(`/image/ + ${image.imageId}`)"
                                   class="text-center mx-auto">
                                        ${image.originalName}
                                </a>
                                <button type="button" class="btn btn-danger btn-sm remove-image-btn ml-auto"
                                        data-image-id="${image.imageId}">X
                                </button>
                            </li>
                        </c:forEach>
                    </ul>

                    <!-- 추가된 이미지 리스트 -->
                    <ul id="addedImagesList" class="d-flex flex-column align-items-center"></ul>

                    <!-- 파일 입력 필드 -->
                    <input type="file" id="imageInput" name="addedImages" multiple style="display:none;">

                    <!-- 파일 입력 버튼 -->
                    <button type="button" id="addImageBtn" class="btn btn-primary">이미지 추가</button>

                    <!-- 팝업 오버레이 및 팝업 내용 -->
                    <div id="popupOverlay" onclick="closePopup()">
                        <div id="popup" onclick="stopPropagation()">
                            <img id="popupImage" src="" alt="이미지"/>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <p>이미지 업로드가 허용되지 않는 게시판입니다.</p>
                </c:otherwise>
            </c:choose>

            <div>
                <label for="privateFlag-Y">공개</label>
                <input type="radio" id="privateFlag-Y" name="privateFlag" value="N"
                ${postDetail.privateFlag == 'N' ? 'checked' : ''} />

                <label for="privateFlag-N">비공개</label>
                <input type="radio" id="privateFlag-N" name="privateFlag" value="Y"
                ${postDetail.privateFlag == 'Y' ? 'checked' : ''} />
            </div>

            <h3>작성 시간 : <fmt:formatDate value="${postDetail.createdAt}" pattern="yyyy-MM-dd HH:mm"/></h3>
            <h3>수정 시간 : <fmt:formatDate value="${postDetail.updatedAt}" pattern="yyyy-MM-dd HH:mm"/></h3>

            <button type="submit" id="saveButton">저장</button>
            <a href="/${boardId}/post/detail/${postDetail.postId}?search=${page.search}&searchType=${page.searchType}&sortType=${page.sortType}&page=${page.page}">
                <button type="button">취소</button>
            </a>
        </form>
    </div>

</div>

</body>
</html>
