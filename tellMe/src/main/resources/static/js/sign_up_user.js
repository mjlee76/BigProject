document.addEventListener("DOMContentLoaded", function () {
    const addressInput = document.getElementById("address");
    if (addressInput) {
      addressInput.addEventListener("click", function () {
        new daum.Postcode({
          oncomplete: function (data) {
            // 주소 선택 후 입력
            const address = data.roadAddress || data.jibunAddress;
            addressInput.value = address;
          },
        }).open();
      });
    } else {
      console.error('id가 "address"인 요소를 찾을 수 없습니다.');
    }
 });


document.addEventListener("DOMContentLoaded", function () {
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const passwordMismatchMessage = document.getElementById("passwordMismatch");

    function checkPasswordMatch() {
        if (passwordInput.value !== confirmPasswordInput.value) {
            passwordMismatchMessage.style.display = "block";
            return false;
        } else {
            passwordMismatchMessage.style.display = "none";
            return true;
        }
    }

    passwordInput.addEventListener("input", checkPasswordMatch);
    confirmPasswordInput.addEventListener("input", checkPasswordMatch);

    document.querySelector("form").addEventListener("submit", function (event) {
        if (!checkPasswordMatch()) {
            event.preventDefault();
            alert("비밀번호가 일치하지 않습니다.");
        }
    });
});
