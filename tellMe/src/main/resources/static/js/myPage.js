$(document).ready(function() {
    $(".edit-name-btn").click(function() {
        $("#nameEditModal").show();
    });

    $(".close").click(function() {
        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
        $("#nameEditModal").hide();
    });

    $("#nameEditForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            userName: $("#newName").val(),      // 변경할 새 이름
            password: $("#currentPassword").val() // 현재 비밀번호
        };

        $.ajax({
            type: "POST",
            url: "/tellMe/user/updateName",  // ✅ 기본 경로 추가!
            data: JSON.stringify(formData),
            contentType: "application/json",
            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
            success: function(response) {
                alert(response.message);
                if (response.success) {
                    // 화면에서 이름 업데이트
                    $(".user-name").text(formData.userName);

                    // 폼 초기화 (입력 필드 비우기)
                    $("#nameEditForm")[0].reset();

                    // 모달 닫기
                    $("#nameEditModal").hide();

                    location.reload();
                }
            },
            error: function(xhr) {
                console.error("에러 응답:", xhr);
                alert(xhr.responseJSON?.message || "이름 변경 실패");
            }
        });
    });
});

$(document).ready(function() {
    $(".edit-phone-btn").click(function() {
        $("#phoneEditModal").show();
    });

    $(".close").click(function() {
        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
        $("#phoneEditModal").hide();
    });

    $("#phoneEditForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            phone: $("#newPhone").val(),  // 변경할 새 핸드폰 번호
            password: $("#currentPasswordPhone").val()  // 현재 비밀번호
        };

        $.ajax({
            type: "POST",
            url: "/tellMe/user/updatePhone",  // ✅ URL 확인
            data: JSON.stringify(formData),
            contentType: "application/json",
            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
            success: function(response) {
                alert(response.message);
                if (response.success) {
                    $(".user-phone").text(formData.phone);
                    $("#phoneEditForm")[0].reset();  // ✅ 폼 초기화
                    $("#phoneEditModal").hide();  // ✅ 모달 닫기
                    location.reload(); // 페이지 새로고침
                }
            },
            error: function(xhr) {
                console.error("에러 응답:", xhr);
                alert(xhr.responseJSON?.message || "핸드폰 변경 실패");
            }
        });
    });
});

$(document).ready(function() {
    $(".edit-email-btn").click(function() {
        $("#emailEditModal").show();
    });

    $(".close").click(function() {
        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
        $("#emailEditModal").hide();
    });

    $("#emailEditForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            email: $("#newEmail").val(),  // 변경할 새 이메일
            password: $("#currentPasswordEmail").val()  // 현재 비밀번호
        };

        $.ajax({
            type: "POST",
            url: "/tellMe/user/updateEmail",  // ✅ URL 확인
            data: JSON.stringify(formData),
            contentType: "application/json",
            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
            success: function(response) {
                alert(response.message);
                if (response.success) {
                    $(".user-email").text(formData.email);
                    $("#emailEditForm")[0].reset();  // ✅ 폼 초기화
                    $("#emailEditModal").hide();  // ✅ 모달 닫기
                    location.reload();
                }
            },
            error: function(xhr) {
                console.error("에러 응답:", xhr);
                alert(xhr.responseJSON?.message || "이메일 변경 실패");
            }
        });
    });
});

$(document).ready(function() {
    $(".edit-address-btn").click(function() {
        $("#addressEditModal").show();
    });

    $(".close").click(function() {
        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
        $("#addressEditModal").hide();
    });

    // 새 주소 입력 필드를 클릭하면 주소 검색 창 실행
    $("#newAddress").click(function() {
        new daum.Postcode({
            oncomplete: function(data) {
                $("#newAddress").val(data.address);  // 선택한 주소를 입력 필드에 반영
            }
        }).open();
    });

    $("#addressEditForm").submit(function(event) {
        event.preventDefault();

        let formData = {
            address: $("#newAddress").val(),  // 변경할 새 주소
            password: $("#currentPasswordAddress").val()  // 현재 비밀번호
        };

        $.ajax({
            type: "POST",
            url: "/tellMe/user/updateAddress",  // ✅ URL 확인
            data: JSON.stringify(formData),
            contentType: "application/json",
            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
            success: function(response) {
                alert(response.message);
                if (response.success) {
                    $(".user-address").text(formData.address);
                    $("#addressEditForm")[0].reset();  // ✅ 폼 초기화
                    $("#addressEditModal").hide();  // ✅ 모달 닫기
                    location.reload();
                }
            },
            error: function(xhr) {
                console.error("에러 응답:", xhr);
                alert(xhr.responseJSON?.message || "주소 변경 실패");
            }
        });
    });
});