let isPasswordValid = false;
let currentEdit = "";

// 이름 수정
function changeName() {
    var currentName = document.querySelector(".user-name").innerText;
    document.getElementById("newName").value = currentName;

    currentEdit = "name";  // 현재 수정하려는 항목은 'name'
    showEditForm("name");
}

// 핸드폰 번호 수정
function changePhone() {
    var currentPhone = document.querySelector(".user-phone").innerText;
    document.getElementById("newPhone").value = currentPhone;

    currentEdit = "phone";  // 현재 수정하려는 항목은 'phone'
    showEditForm("phone");
}

// 이메일 수정
function changeEmail() {
    var currentEmail = document.querySelector(".user-email").innerText;
    document.getElementById("newEmail").value = currentEmail;

    currentEdit = "email";  // 현재 수정하려는 항목은 'email'
    showEditForm("email");
}

// 수정 폼 표시
function showEditForm(editType) {

    const modal = document.getElementById("editModal");
    modal.style.display = "block";

    // 모든 편집 섹션 숨기고 비밀번호 입력창만 표시
    document.querySelectorAll(".edit-section").forEach(section => section.style.display = "none");
    document.getElementById("password").disabled = false;
    document.getElementById("passwordBtn").disabled = false;
    isPasswordValid = false; // 초기화

    // 해당 섹션 표시
    const section = document.getElementById(`${editType}EditSection`);
    section.style.display = "block";

    if(editType === "name") {
        document.getElementById("nameEditSection").style.display = "block";
        document.getElementById("phoneEditSection").style.display = "none";
        document.getElementById("emailEditSection").style.display = "none";
        document.getElementById("newName").disabled = false;
        document.getElementById("saveNameBtn").disabled = false;
    }else if (editType === "phone") {
        document.getElementById("phoneEditSection").style.display = "block";
        document.getElementById("nameEditSection").style.display = "none";
        document.getElementById("emailEditSection").style.display = "none";
        document.getElementById("newPhone").disabled = false;
        document.getElementById("savePhoneBtn").disabled = false;
    }else if (editType === "email") {
         document.getElementById("emailEditSection").style.display = "block";
         document.getElementById("nameEditSection").style.display = "none";
         document.getElementById("phoneEditSection").style.display = "none";
         document.getElementById("newEmail").disabled = false;
         document.getElementById("saveEmailBtn").disabled = false;
     }

    document.getElementById("password").disabled = true;
    document.getElementById("passwordBtn").disabled = true;
}

async function checkPassword() {
  const password = document.getElementById("password").value;

  if (password.trim() === "") {
      alert("비밀번호를 입력해주세요.");
      return;
  }

  // CSRF 토큰 추출
  const csrfToken = document.querySelector("input[name='_csrf']").value;

  try {
      // 비밀번호 확인 요청
      const response = await fetch(checkPasswordUrl, {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              "X-CSRF-TOKEN": csrfToken  // CSRF 토큰 추가
          },
          body: JSON.stringify({ "password": password })
      });

      if(response.ok) {
        // 비밀번호가 맞으면, 이름 입력 필드와 저장 버튼을 보여줌
        isPasswordValid = true;  // 비밀번호가 맞는 경우
        document.getElementById("newName").disabled = false;  // 이름 입력 필드 표시
        document.getElementById("saveNameBtn").disabled = false;
        document.getElementById("savePhoneBtn").disabled = false;
        document.getElementById("saveEmailBtn").disabled = false;

        // 비밀번호 입력창과 인증 버튼 비활성화
        document.getElementById("password").disabled = true;  // 비밀번호 입력창 비활성화
        document.getElementById("passwordBtn").disabled = true;  // 인증 버튼 비활성화

        alert("비밀번호 인증 성공");
        showEditForm(currentEdit);
      }else {
        const errorMessage = await response.text();  // 서버에서 보내는 오류 메시지
        throw new Error(errorMessage);  // 서버에서 보내는 오류 메시지로 예외 처리
      }
  } catch (error) {
    console.error(error);
    alert("비밀번호 인증 실패: " + error.message);
  }
}

// 이름 저장
async function saveNewName() {
    if (!isPasswordValid) {
      alert("비밀번호 인증이 필요합니다.");
      return;
    }

    var newName = document.getElementById("newName").value;
    if (newName.trim() === "") {
      alert("이름을 입력해주세요.");
      return;
    }
    await saveData(saveNameUrl, ".user-name", "newName", newName);
}

 // 핸드폰 번호 저장
async function saveNewPhone() {
    if (!isPasswordValid) {
        alert("비밀번호 인증이 필요합니다.");
        return;
    }

    const newPhone = document.getElementById("newPhone").value;
    if (newPhone.trim() === "") {
        alert("핸드폰 번호를 입력해주세요.");
        return;
    }
    await saveData(savePhoneUrl, ".user-phone", "newPhone", newPhone);
}

// 이메일 저장
async function saveNewEmail() {
    if (!isPasswordValid) {
        alert("비밀번호 인증이 필요합니다.");
        return;
    }

    const newEmail = document.getElementById("newEmail").value;
    if (newEmail.trim() === "") {
        alert("이메일을 입력해주세요.");
        return;
    }
    await saveData(saveEmailUrl, ".user-email", "newEmail", newEmail);
}

// 공통 저장 함수 생성
async function saveData(url, elementId, fieldName, value) {
  try {
    const response = await fetch(url, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": getCsrfToken()
      },
      body: JSON.stringify({ [fieldName] : value })
    });

    if (response.ok) {
      document.querySelector(elementId).innerText = value;
      alert("수정 완료");
      cancelEdit();
//      setTimeout(cancelEdit, 1000);
    }
  } catch (error) {
    console.error(error);
    alert("수정 실패");
  }
}

function getCsrfToken() {
  return document.querySelector("input[name='_csrf']").value;
}

// 취소 버튼 클릭 시 이름 수정 폼 숨기기
function cancelEdit() {
    document.getElementById("nameEditForm").style.display = "none";
    document.getElementById("modalBackdrop").style.display = "none"; // 배경 숨김

    isPasswordValid = false;
    document.getElementById("password").value = "";
    document.getElementById("newName").value = "";
    document.getElementById("newPhone").value = "";
    document.getElementById("newEmail").value = "";
    document.getElementById("password").disabled = false;
    document.getElementById("passwordBtn").disabled = false;
    document.getElementById("newName").disabled = true;
    document.getElementById("newPhone").disabled = true;
    document.getElementById("newEmail").disabled = true;
    document.getElementById("saveNameBtn").disabled = true;
    document.getElementById("savePhoneBtn").disabled = true;
    document.getElementById("saveEmailBtn").disabled = true;

    document.getElementById("editModal").style.display = "none";
}