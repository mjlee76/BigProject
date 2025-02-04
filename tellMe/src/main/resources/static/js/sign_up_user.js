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
