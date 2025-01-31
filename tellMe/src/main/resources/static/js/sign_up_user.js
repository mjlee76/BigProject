document.getElementById("address").addEventListener("click", function () {
    new daum.Postcode({
      oncomplete: function (data) {
        // 주소 선택 후 입력
        const address = data.roadAddress || data.jibunAddress; // 도로명주소 또는 지번주소
        document.getElementById("address").value = address;
      },
    }).open();
  });