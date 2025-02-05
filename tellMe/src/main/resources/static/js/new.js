function filterAPI(event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const userId = document.getElementById("userId").value;
    const csrfToken = document.querySelector('input[name="_csrf"]').value;

    const data = {
        title: title,
        content: content,
        userId: userId,
        csrfToken: csrfToken
    };

    fetch("/tellMe/api/check", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Network response was not ok: " + response.statusText);
        }
        return response.json();
    })
    .then(data => {
        if (data.valid) { // 서버에서 유효성 검사 결과가 true일 때만 제출
            alert("API 검증 완료. 등록됩니다.");
            event.target.submit();
        } else {
            alert("제출이 차단되었습니다: " + data.message);
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("API 검증 중 오류가 발생했습니다. 다시 시도해 주세요.");
    });
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

