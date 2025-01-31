// 초기 상태
let currentCategory = "all";
let currentPage = 1;
const itemsPerPage = 10;
let notices = [
    { id: 1, title: "공지사항 테스트", attachment: "-", date: "2025-01-31 10:35:30", views: 1 }
]; // 공지사항 예제 데이터
let isDeleteMode = false; // 🔥 삭제 모드 여부 (체크박스 표시 여부)

// ✅ 테이블 업데이트 함수
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
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center;">해당 공지사항이 없습니다.</td>
            </tr>
        `;
        document.getElementById("total-count").textContent = 0;
        document.getElementById("pagination").innerHTML = "";
        return;
    }

    filteredNotices.slice(start, end).forEach(notice => {
        const row = `
            <tr>
                <td>
                    ${isDeleteMode
                        ? `<input type="checkbox" class="delete-checkbox" data-id="${notice.id}">`
                        : notice.id}
                </td>
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

// ✅ 페이지네이션 업데이트 함수
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

// ✅ 카테고리 변경 이벤트
document.querySelectorAll(".tab").forEach(tab => {
    tab.addEventListener("click", () => {
        currentCategory = tab.dataset.category;
        currentPage = 1;
        updateTable();
    });
});

// ✅ 삭제 버튼 이벤트 추가
document.getElementById("delete-button").addEventListener("click", function () {
    isDeleteMode = true; // 🔥 삭제 모드 활성화
    updateTable(); // 🔥 테이블 다시 렌더링 (번호 -> 체크박스 변경)

    document.getElementById("select-delete-button").style.display = "inline-block";
    document.getElementById("cancel-button").style.display = "inline-block";
    document.getElementById("delete-button").style.display = "none"; // 삭제 버튼 숨기기
});

// ✅ 선택 삭제 버튼 이벤트 추가
document.getElementById("select-delete-button").addEventListener("click", function () {
    const selectedNotices = Array.from(document.querySelectorAll(".delete-checkbox:checked"))
        .map(checkbox => parseInt(checkbox.dataset.id));

    if (selectedNotices.length === 0) {
        alert("삭제할 항목을 선택해주세요.");
        return;
    }

    // 🔥 서버 연동이 필요하면 여기에서 AJAX 요청 추가 가능
    // 예: fetch(`/delete`, { method: "POST", body: JSON.stringify({ ids: selectedNotices }) })

    // 🔥 로컬 데이터에서 삭제 (테스트용)
    notices = notices.filter(notice => !selectedNotices.includes(notice.id));

    isDeleteMode = false; // 🔥 삭제 모드 해제
    updateTable(); // 🔥 테이블 다시 렌더링 (체크박스 제거)

    // 버튼 상태 초기화
    document.getElementById("select-delete-button").style.display = "none";
    document.getElementById("cancel-button").style.display = "none";
    document.getElementById("delete-button").style.display = "inline-block";
});

// ✅ 취소 버튼 이벤트 추가
document.getElementById("cancel-button").addEventListener("click", function () {
    isDeleteMode = false; // 🔥 삭제 모드 비활성화
    updateTable(); // 🔥 체크박스 제거 후 번호 복구

    // 버튼 상태 복구
    document.getElementById("select-delete-button").style.display = "none";
    document.getElementById("cancel-button").style.display = "none";
    document.getElementById("delete-button").style.display = "inline-block";
});

// ✅ 초기 테이블 로드
updateTable();
