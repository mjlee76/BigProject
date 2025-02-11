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
<<<<<<< HEAD
 });

<<<<<<< HEAD
=======
  });
<<<<<<< HEAD

//// 상세주소 입력 시 주소 필드 업데이트
//document.getElementById("detailAddress").addEventListener("input", function () {
//    let baseAddress = document.getElementById("address").getAttribute("data-base-address") || document.getElementById("address").value;
//    let detail = this.value.trim();
//
//    // 주소 필드에 data-base-address 속성을 저장하여 원본 주소 유지
//    document.getElementById("address").setAttribute("data-base-address", baseAddress);
//
//    // 상세주소 포함한 전체 주소 업데이트
//    document.getElementById("address").value = detail ? `${baseAddress} ${detail}` : baseAddress;
//});
>>>>>>> e1cf1c9 (내 페이지 수정.)
=======
>>>>>>> a18053f (내 페이지 수정.)
=======
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
>>>>>>> f1d798452247949a8bf40fcbd756920b276ec629
