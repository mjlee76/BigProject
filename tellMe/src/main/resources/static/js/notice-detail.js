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
const deleteReq = () => {
    console.log("삭제 요청");
    const id = document.getElementById("noticeId").value; // 공지사항 ID 가져오기
    location.href = "/tellMe/customer/delete/" + id; // 삭제 요청
};