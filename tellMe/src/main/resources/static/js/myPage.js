//$(document).ready(function() {
//    $(".edit-name-btn").click(function() {
//        $("#nameEditModal").show();
//    });
//
//    $(".close").click(function() {
//        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
//        $("#nameEditModal").hide();
//    });
//
//    $("#nameEditForm").submit(function(event) {
//        event.preventDefault();
//
//        let formData = {
//            userName: $("#newName").val(),      // 변경할 새 이름
//            password: $("#currentPassword").val() // 현재 비밀번호
//        };
//
//        $.ajax({
//            type: "POST",
//            url: "/tellMe/user/updateName",  // ✅ 기본 경로 추가!
//            data: JSON.stringify(formData),
//            contentType: "application/json",
//            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
//            success: function(response) {
//                alert(response.message);
//                if (response.success) {
//                    // 화면에서 이름 업데이트
//                    $(".user-name").text(formData.userName);
//
//                    // 폼 초기화 (입력 필드 비우기)
//                    $("#nameEditForm")[0].reset();
//
//                    // 모달 닫기
//                    $("#nameEditModal").hide();
//
//                    location.reload();
//                }
//            },
//            error: function(xhr) {
//                console.error("에러 응답:", xhr);
//                alert(xhr.responseJSON?.message || "이름 변경 실패");
//            }
//        });
//    });
//});
//
//$(document).ready(function() {
//    $(".edit-phone-btn").click(function() {
//        $("#phoneEditModal").show();
//    });
//
//    $(".close").click(function() {
//        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
//        $("#phoneEditModal").hide();
//    });
//
//    $("#phoneEditForm").submit(function(event) {
//        event.preventDefault();
//
//        let formData = {
//            phone: $("#newPhone").val(),  // 변경할 새 핸드폰 번호
//            password: $("#currentPasswordPhone").val()  // 현재 비밀번호
//        };
//
//        $.ajax({
//            type: "POST",
//            url: "/tellMe/user/updatePhone",  // ✅ URL 확인
//            data: JSON.stringify(formData),
//            contentType: "application/json",
//            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
//            success: function(response) {
//                alert(response.message);
//                if (response.success) {
//                    $(".user-phone").text(formData.phone);
//                    $("#phoneEditForm")[0].reset();  // ✅ 폼 초기화
//                    $("#phoneEditModal").hide();  // ✅ 모달 닫기
//                    location.reload(); // 페이지 새로고침
//                }
//            },
//            error: function(xhr) {
//                console.error("에러 응답:", xhr);
//                alert(xhr.responseJSON?.message || "핸드폰 변경 실패");
//            }
//        });
//    });
//});
//
//$(document).ready(function() {
//    $(".edit-email-btn").click(function() {
//        $("#emailEditModal").show();
//    });
//
//    $(".close").click(function() {
//        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
//        $("#emailEditModal").hide();
//    });
//
//    $("#emailEditForm").submit(function(event) {
//        event.preventDefault();
//
//        let formData = {
//            email: $("#newEmail").val(),  // 변경할 새 이메일
//            password: $("#currentPasswordEmail").val()  // 현재 비밀번호
//        };
//
//        $.ajax({
//            type: "POST",
//            url: "/tellMe/user/updateEmail",  // ✅ URL 확인
//            data: JSON.stringify(formData),
//            contentType: "application/json",
//            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
//            success: function(response) {
//                alert(response.message);
//                if (response.success) {
//                    $(".user-email").text(formData.email);
//                    $("#emailEditForm")[0].reset();  // ✅ 폼 초기화
//                    $("#emailEditModal").hide();  // ✅ 모달 닫기
//                    location.reload();
//                }
//            },
//            error: function(xhr) {
//                console.error("에러 응답:", xhr);
//                alert(xhr.responseJSON?.message || "이메일 변경 실패");
//            }
//        });
//    });
//});
//
//$(document).ready(function() {
//    $(".edit-address-btn").click(function() {
//        $("#addressEditModal").show();
//    });
//
//    $(".close").click(function() {
//        $(this).closest(".modal").find("form")[0].reset(); // 해당 모달의 폼을 초기화
//        $("#addressEditModal").hide();
//    });
//
//    // 새 주소 입력 필드를 클릭하면 주소 검색 창 실행
//    $("#newAddress").click(function() {
//        new daum.Postcode({
//            oncomplete: function(data) {
//                $("#newAddress").val(data.address);  // 선택한 주소를 입력 필드에 반영
//            }
//        }).open();
//    });
//
//    $("#addressEditForm").submit(function(event) {
//        event.preventDefault();
//
//        let formData = {
//            address: $("#newAddress").val(),  // 변경할 새 주소
//            password: $("#currentPasswordAddress").val()  // 현재 비밀번호
//        };
//
//        $.ajax({
//            type: "POST",
//            url: "/tellMe/user/updateAddress",  // ✅ URL 확인
//            data: JSON.stringify(formData),
//            contentType: "application/json",
//            headers: { "X-CSRF-TOKEN": $("input[name='_csrf']").val() },
//            success: function(response) {
//                alert(response.message);
//                if (response.success) {
//                    $(".user-address").text(formData.address);
//                    $("#addressEditForm")[0].reset();  // ✅ 폼 초기화
//                    $("#addressEditModal").hide();  // ✅ 모달 닫기
//                    location.reload();
//                }
//            },
//            error: function(xhr) {
//                console.error("에러 응답:", xhr);
//                alert(xhr.responseJSON?.message || "주소 변경 실패");
//            }
//        });
//    });
//});
//
document.addEventListener("DOMContentLoaded", function() {
    const nameEditModal = document.getElementById("nameEditModal");
    const newNameInput = document.getElementById("newName");
    const submitNameBtn = document.getElementById("submitNameBtn");

    // "이름 수정" 버튼 클릭 시 모달 열기
    document.querySelectorAll(".edit-name-btn").forEach(button => {
        button.addEventListener("click", function() {
            nameEditModal.style.display = "block";
        });
    });

    // 닫기 버튼(X) 클릭 시 모달 닫기
    document.querySelectorAll(".close").forEach(button => {
        button.addEventListener("click", function() {
            closeNameEditModal();
        });
    });

});

// ✅ 모달 닫기 함수 (전역 함수로 이동)
function closeNameEditModal() {
    const nameEditModal = document.getElementById("nameEditModal");
    nameEditModal.style.display = "none";

    document.getElementById("newName").value = ""; // 입력 필드 초기화
    document.getElementById("submitNameBtn").disabled = true; // 인증 전까지 버튼 비활성화
    document.getElementById("emailVerificationCode").value = "";
    document.getElementById("emailVerificationStatus").innerText = "";
}

// ✅ 이메일 인증 요청 (이메일을 프론트엔드에서 보내지 않음)
function sendVerificationEmail() {
    fetch("/tellMe/request-verification", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector("input[name='_csrf']").value
        }
    })
    .then(response => response.json())
    .then(data =>
        document.getElementById("emailVerificationStatus").innerText = data.message;
    })
    .catch(error => {
        console.error("Error:", error);
    });
}

// ✅ 이메일 인증 코드 확인
function verifyEmailCode() {
    const code = document.getElementById("emailVerificationCode").value;

    fetch("/tellMe/verify-code", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector("input[name='_csrf']").value
        },
        body: JSON.stringify({ code: code })  // ✅ 이메일 없이 코드만 전송
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            alert("이메일 인증이 완료되었습니다!");
            document.getElementById("newName").disabled = false;
            document.getElementById("submitNameBtn").disabled = false;
        } else {
            alert("인증 코드가 올바르지 않습니다.");
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
}

// ✅ 이름 변경 요청 (이메일을 보내지 않고, 백엔드에서 사용자 식별)
function changeUserName() {
    const newName = document.getElementById("newName").value;
    const verificationCode = document.getElementById("emailVerificationCode").value; // ✅ 인증 코드 가져오기

    fetch("/tellMe/update-name", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": document.querySelector("input[name='_csrf']").value,
            "Verification-Code": verificationCode  // ✅ 인증 코드를 헤더에 포함
        },
        body: JSON.stringify({
            userName: newName  // ✅ 이름만 JSON으로 보냄
        })
    })
    .then(response => response.json())
    .then(data => {
        alert(data.message);
        if (data.success) {
            document.querySelector(".user-name").innerText = newName;
            closeNameEditModal();
            location.reload();
        }
    })
    .catch(error => {
        console.error("Error:", error);
    });
}


