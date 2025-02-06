// 게시판 목록 보기
const detailListReq = () => {
        console.log("목록 요청");
        const page = document.getElementById("page").value; // Hidden input에서 값 가져오기
        location.href = "/tellMe/complaint/question?page=" + page + "&size=10"; // 해당 페이지로 이동
    };

