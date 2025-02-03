// ✅ 공지사항 목록으로 이동하는 함수
const writeListReq = () => {
    console.log("목록 요청");
    location.href = "/tellMe/customer/notice?page=1"; // 1페이지로 이동
};

$(document).ready(function () {
    $("#fileImage").change(function () {
        fileSize = this.files[0].size;

        if (fileSize > 10485760) {
            this.setCustomValidity("이미지 크기는 10MB 이하로 선택해 주세요.");
            this.reportValidity();
        } else {
            this.setCustomValidity("");
            showImageThumbnail(this);
        }
    });
});

function showImageThumbnail(fileInput) {
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        $("#thumbnail").attr("src", e.target.result);
    };
    reader.readAsDataURL(file);
}

// 파일 입력 초기화 (취소 버튼 기능)
function resetFileInput() {
    $("#fileImage").val("");  // 파일 입력 초기화
}