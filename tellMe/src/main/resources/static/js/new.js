function filterAPI(form) {
    form.preventDefault();

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const data = {
        title: title,
        content: content
    };

    fetch("/api/check", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(data => {
        alert("API 응답: " + JSON.stringify(data));
        form.submit();
    })
    .catch(error => console.error("Error:", error));
}

$(document).ready(function () {
    $(".file-input-field").change(function () {
        const fileInput = $(this);
        const file = fileInput[0].files[0];

        if (file) {
            if (file.size > 10485760) { // 10MB 제한
                fileInput[0].setCustomValidity("파일 크기는 10MB 이하로 선택해 주세요.");
                fileInput[0].reportValidity();
            } else {
                fileInput[0].setCustomValidity("");
            }
        }
    });
});

// 파일 입력 초기화 (취소 버튼 기능)
function resetFileInput(index) {
    const fileInput = $("#fileImage" + index);
    fileInput.val(""); // 파일 입력 초기화
}

