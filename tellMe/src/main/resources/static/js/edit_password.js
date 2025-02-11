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
    event.preventDefault();

    const currentPassword = document.getElementById("current-password").value;
    const newPassword = document.getElementById("new-password").value;
    const confirmPassword = document.getElementById("confirm-password").value;

    // 새 비밀번호 확인
    if (newPassword !== confirmPassword) {
      alert("새 비밀번호가 일치하지 않습니다.");
      return;
    }

    // AJAX 요청으로 현재 비밀번호 확인 및 변경 요청
    fetch("/myPage/updatePassword", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": document.querySelector("input[name='_csrf']").value
      },
      body: JSON.stringify({
        currentPassword: currentPassword,
        newPassword: newPassword
      })
    })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        alert("비밀번호가 변경되었습니다.");
        modal.style.display = "none";
        form.reset();
      } else {
        alert(data.message || "현재 비밀번호가 올바르지 않습니다.");
      }
    })
    .catch(error => console.error("Error:", error));
  });
});
