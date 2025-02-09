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
    const referrer = document.referrer; // 사용자가 이 페이지에 오기 전의 URL

    let targetUrl = "/tellMe/complaint/question?page=" + page + "&size=10"; // 기본값: 게시판 목록으로 이동

    if (referrer.includes("/myPage/myComplaint")) {
        // 🔹 마이페이지에서 넘어왔다면 다시 마이페이지로 이동
        targetUrl = "/tellMe/myPage/myComplaint?page=" + page;
    }

    location.href = targetUrl;
};

