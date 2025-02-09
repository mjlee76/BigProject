//// 게시판 목록 보기
//const detailListReq = () => {
//        console.log("목록 요청");
//        const page = document.getElementById("page").value; // Hidden input에서 값 가져오기
//        location.href = "/tellMe/complaint/question?page=" + page + "&size=10"; // 해당 페이지로 이동
//    };
//
const detailListReq = () => {
    console.log("목록 요청");

    const page = document.getElementById("page").value; // 현재 페이지 번호
    const currentUserId = document.getElementById("currentUserId").value; // 로그인한 유저 ID
    const questionUserId = document.getElementById("questionUserId").value; // 문의 작성자 ID

    let targetUrl = "/tellMe/complaint/question?page=" + page + "&size=10"; // 기본 경로

    if (currentUserId && currentUserId === questionUserId) {
        // 🔹 현재 로그인한 유저가 글 작성자일 경우 마이페이지 이동
        targetUrl = "/tellMe/myPage/myComplaint?page=" + page;
    }

    location.href = targetUrl;
};
