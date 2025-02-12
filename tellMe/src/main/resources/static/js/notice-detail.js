// 공지사항 목록 보기
const detailListReq = () => {
    console.log("목록 요청");
    const page = document.getElementById("page").value; // Hidden input에서 값 가져오기
    location.href = "/tellMe/customer/notice?page=" + page; // 해당 페이지로 이동
};

// 공지사항 수정 요청
const updateReq = () => {
    console.log("수정 요청");
    const id = document.getElementById("noticeId").value; // 공지사항 ID 가져오기
    const page = document.getElementById("page").value; // 현재 페이지 번호 가져오기
    location.href = "/tellMe/customer/update/" + id + "?page=" + page; // 수정 페이지로 이동하면서 page 값 유지
};

// 공지사항 삭제 요청
// 공지사항 삭제 요청 (삭제 전 확인 팝업 추가)
const deleteReq = () => {
    console.log("삭제 요청");

    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const id = document.getElementById("noticeId").value; // 공지사항 ID 가져오기

    if (!id) {
        alert("삭제할 공지사항이 없습니다.");
        return;
    }

    // 삭제 확인 팝업 추가
    if (!confirm("공지사항을 삭제하시겠습니까?")) {
        return; // 사용자가 취소하면 삭제 요청 중단
    }

    fetch("/tellMe/customer/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": csrfToken
        },
        body: JSON.stringify({id : id}),
    })
    .then(response => {
        if (response.ok) {
            alert("삭제되었습니다.");
            window.location.href = "/tellMe/customer/notice";
        } else {
            return response.text().then(text => {
                console.error("Error response:", text);
                alert("삭제 중 문제가 발생했습니다.");
            });
        }
    })
        .catch(error => console.error("Fetch error:", error));

    // 삭제 요청 실행
    //location.href = "/tellMe/customer/delete/" + id; // 삭제 요청
};
