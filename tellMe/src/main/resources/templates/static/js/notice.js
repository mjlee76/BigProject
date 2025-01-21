// 데이터 정의
const notices = [
    { id: 6, title: "[업데이트] 10/6 업데이트", attachment: "📄", date: "2024.09.30", views: 129, category: "업데이트" },
    { id: 5, title: "[행사] 찬영이 생일축하 파티 행사", attachment: "📄", date: "2024.09.04", views: 1286, category: "행사" },
    { id: 4, title: "[공고] 게시판 관리자 모집", attachment: "📄", date: "2024.08.05", views: 620, category: "공고" },
    { id: 3, title: "[업데이트] 8/8 업데이트", attachment: "📄", date: "2024.07.19", views: 448, category: "업데이트" },
    { id: 2, title: "[업데이트] 6/13 업데이트", attachment: "📄", date: "2024.06.01", views: 544, category: "업데이트" },
    { id: 1, title: "[업데이트] 3/14 업데이트", attachment: "📄", date: "2024.03.07", views: 673, category: "업데이트" },
];

// 초기 상태
let currentCategory = "all";
let currentPage = 1;
const itemsPerPage = 10;

// 테이블 업데이트 함수
function updateTable() {
    const filteredNotices = currentCategory === "all"
        ? notices
        : notices.filter(notice => notice.category === currentCategory);

    const totalItems = filteredNotices.length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;

    const tableBody = document.getElementById("table-body");
    tableBody.innerHTML = "";

    if (totalItems === 0) {
        // 데이터가 없는 경우 "해당 공지사항이 없습니다." 출력
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center;">해당 공지사항이 없습니다.</td>
            </tr>
        `;
        document.getElementById("total-count").textContent = 0;
        document.getElementById("pagination").innerHTML = "";
        return;
    }

    filteredNotices.slice(start, end).forEach(notice => {
        const row = `
            <tr>
                <td>${notice.id}</td>
                <td>${notice.title}</td>
                <td>${notice.attachment}</td>
                <td>${notice.date}</td>
                <td>${notice.views}</td>
            </tr>
        `;
        tableBody.innerHTML += row;
    });

    document.getElementById("total-count").textContent = totalItems;
    updatePagination(totalPages);
}

// 페이지네이션 업데이트 함수
function updatePagination(totalPages) {
    const pagination = document.getElementById("pagination");
    pagination.innerHTML = "";

    for (let i = 1; i <= totalPages; i++) {
        const button = document.createElement("button");
        button.textContent = i;
        button.className = "page-button";
        if (i === currentPage) button.classList.add("active");
        button.addEventListener("click", () => {
            currentPage = i;
            updateTable();
        });
        pagination.appendChild(button);
    }
}

// 카테고리 변경 함수
document.querySelectorAll(".tab").forEach(tab => {
    tab.addEventListener("click", () => {
        currentCategory = tab.dataset.category;
        currentPage = 1; // 카테고리 변경 시 첫 페이지로
        updateTable();
    });
});

// 초기화
updateTable();
