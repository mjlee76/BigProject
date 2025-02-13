document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("passwordModal");
    const btn = document.querySelector(".edit-btn");
    const closeBtn = document.querySelector(".close");
    const form = document.getElementById("passwordForm");

    // "수정" 버튼 클릭 시 모달 표시
    btn.addEventListener("click", function () {
        modal.style.display = "block";
    });

    // 닫기 버튼 클릭 시 모달 닫기
    closeBtn.addEventListener("click", function () {
        modal.style.display = "none";
    });

    // 비밀번호 변경 요청 처리
    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const currentPassword = document.getElementById("current-password").value;
        const newPassword = document.getElementById("new-password").value;
        const confirmPassword = document.getElementById("confirm-password").value;
        const csrfToken = document.querySelector("input[name='_csrf']").value;

        // 새 비밀번호 확인
        if (newPassword !== confirmPassword) {
            alert("새 비밀번호가 일치하지 않습니다.");
            return;
        }

        fetch(window.location.origin + "/tellMe/password/update", { // ✅ URL 확인
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },
            body: JSON.stringify({
                currentPassword: currentPassword,
                newPassword: newPassword
            })
        })
        .then(response => {
            console.log("🔹 응답 상태 코드:", response.status);
            return response.text();
        })
        .then(text => {
            console.log("🔹 서버 응답:", text);
            return JSON.parse(text);
        })
        .then(data => {
            if (data.success === "true") {
                alert("비밀번호가 변경되었습니다. 다시 로그인하세요.");

                // ✅ 로그아웃 폼 직접 제출 → 자동 로그아웃
                document.logoutForm.submit();

            } else {
                alert(data.message || "현재 비밀번호가 올바르지 않습니다.");
            }
        })
        .catch(error => {
            console.error("Error 발생:", error);
            alert("비밀번호 변경 중 오류가 발생했습니다.");
        });
    });
});
