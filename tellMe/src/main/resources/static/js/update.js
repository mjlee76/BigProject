// 공지사항 목록 보기
const updateListReq = () => {
    console.log("목록 요청");
    const page = document.getElementById("page").value; // Hidden input에서 값 가져오기
    location.href = "/tellMe/customer/notice?page=" + page;
};