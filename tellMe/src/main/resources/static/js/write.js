// ✅ 공지사항 목록으로 이동하는 함수
const writeListReq = () => {
    console.log("목록 요청");
    location.href = "/tellMe/customer/notice?page=1"; // 1페이지로 이동
};