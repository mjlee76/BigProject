document.getElementById("address").addEventListener("click", function () {
    new daum.Postcode({
        oncomplete: function (data) {
            const address = data.roadAddress || data.jibunAddress; // 도로명주소 또는 지번주소
            document.getElementById("address").value = address;

            // 상세주소 입력 필드 초기화 후 포커스 이동
            document.getElementById("detailAddress").value = "";
            document.getElementById("detailAddress").focus();
        }
    }).open();
});

// 상세주소 입력 시 주소 필드 업데이트
document.getElementById("detailAddress").addEventListener("input", function () {
    let baseAddress = document.getElementById("address").getAttribute("data-base-address") || document.getElementById("address").value;
    let detail = this.value.trim();

    // 주소 필드에 data-base-address 속성을 저장하여 원본 주소 유지
    document.getElementById("address").setAttribute("data-base-address", baseAddress);

    // 상세주소 포함한 전체 주소 업데이트
    document.getElementById("address").value = detail ? `${baseAddress} ${detail}` : baseAddress;
});
