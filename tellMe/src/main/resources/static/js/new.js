function spamAPI(event) {
    event.preventDefault();

//    여기 팝업창 기능
    const submitButton = document.querySelector("button[type='submit']");
    submitButton.disabled = true; // 중복 클릭 방지

    document.getElementById("loading-overlay").style.display = "flex";

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

    fetch("/tellMe/api/spam", {
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
            console.log(data);
            console.log(data.spam.trim());
            alert("API 검증 결과 : " + data.message);
            // 버튼 다시 활성화 (폼 제출 후)
            if (submitButton) {
                submitButton.disabled = false;
            }
            if(data.spam.trim() === "도배") {
                resetForm();
            }
            event.target.submit();
        } else {
            alert("제출이 차단되었습니다: " + data.message);
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("API 검증 중 오류가 발생했습니다. 다시 시도해 주세요.");
    })
    .finally(() => {
        document.getElementById("loading-overlay").style.display = "none";
        submitButton.disabled = false; // 응답이 끝나면 다시 버튼 활성화
    });
}

function resetForm() {
    const form = document.querySelector("form");

    if (form) {
        form.reset(); // ✅ 일반 입력 필드 초기화
    }

    // ✅ 파일 입력 필드 초기화 (form.reset()만으로는 초기화되지 않음)
    document.querySelectorAll("input[type='file']").forEach(input => {
        input.value = ""; // 파일 입력 필드 비우기
    });

    // ✅ Thymeleaf 바인딩된 DTO 필드도 명시적으로 초기화
    document.getElementById("title").value = "";
    document.getElementById("content").value = "";
    document.getElementById("userId").value = "";

    // ✅ 라디오 버튼도 초기화
    document.querySelectorAll("input[type='radio']").forEach(radio => {
        radio.checked = false;
    });

    console.log("폼 및 DTO 데이터 초기화 완료!");
}

$(document).ready(function () {
    $(".file-input-field").change(function () {
        const fileInput = $(this);
        const file = fileInput[0].files[0];
        const fileIndex = fileInput.attr("id").replace("fileImage", "");

        if (file) {
            if (file.size > 10485760) { // 10MB 제한
                fileInput[0].setCustomValidity("파일 크기는 10MB 이하로 선택해 주세요.");
                fileInput[0].reportValidity();
            } else {
                fileInput[0].setCustomValidity("");
            }

            // 파일을 API로 업로드
            uploadFile(file, fileIndex)
        }
    });
});

// 파일 업로드 함수
function uploadFile(file, index) {
    let formData = new FormData();
    formData.append("file", file);

    $.ajax({
        url: "/api/uploadFile",  // 백엔드 API 엔드포인트 설정
        type: "POST",
        data: formData,
        contentType: false,
        processData: false,
        beforeSend: function () {
            $("#uploadStatus" + index).text("업로드 중..."); // 상태 표시
        },
        success: function (response) {
            $("#uploadStatus" + index).text("✅ 업로드 성공");
        },
        error: function (xhr) {
            $("#uploadStatus" + index).text("❌ 업로드 실패");
            console.error("업로드 실패: ", xhr.responseText);
        }
    });
}

// 파일 입력 초기화 (취소 버튼 기능)
function resetFileInput(index) {
    const fileInput = $("#fileImage" + index);
    fileInput.val(""); // 파일 입력 초기화
}

