document.addEventListener("DOMContentLoaded", () => {
    let deleteMode = false; // 삭제 모드 여부
    const checkboxes = () => document.querySelectorAll(".delete-checkbox"); // 체크박스 가져오기 함수
    const checkboxColumns = document.querySelectorAll(".number-column");
    const deleteButton = document.getElementById("delete-button");
    const selectButton = document.getElementById("select-button");
    const searchForm = document.getElementById("search-form"); // 검색 폼
    const searchInput = document.getElementById("search-input"); // 검색 입력 필드
    const tableBody = document.getElementById("table-body"); // 공지사항 테이블 본문
    const allRows = Array.from(tableBody.querySelectorAll("tr")); // 모든 공지사항 행
    const pagination = document.querySelector(".pagination"); // 페이지네이션
    const postsPerPage = 10; // 페이지당 표시할 게시글 수
    let filteredPosts = [...allRows]; // 필터링된 게시글 목록

//    // ✅ 삭제 모드 활성화/비활성화 함수
//    function toggleDeleteMode() {
//        deleteMode = !deleteMode; // 상태 변경
//        checkboxes().forEach(checkbox => checkbox.style.display = deleteMode ? "inline-block" : "none");
//        checkboxColumns.forEach(column => column.style.display = deleteMode ? "table-cell" : "none");
//        deleteButton.style.display = deleteMode ? "inline-block" : "none";
//        selectButton.innerText = deleteMode ? "취소" : "삭제";
//    }
    // ✅ 삭제 모드 활성화/비활성화 함수
    function toggleDeleteMode() {
        deleteMode = !deleteMode;
        document.body.classList.toggle("delete-mode");

        // 체크박스 상태 업데이트
        checkboxes().forEach(checkbox => {
            checkbox.style.display = deleteMode ? "block" : "none";
        });

        // 버튼 그룹 전환 로직 유지
        const buttonsContainer = document.querySelector('.delete-mode-buttons');
        const normalButtons = document.querySelector('.button-group:not(.delete-mode-buttons)');
        if (deleteMode) {
            normalButtons.style.display = 'none';
            buttonsContainer.style.display = 'flex';
        } else {
            normalButtons.style.display = 'flex';
            buttonsContainer.style.display = 'none';
        }
    }

    // 초기화 시 삭제 모드 버튼 숨기기
    document.querySelector('.delete-mode-buttons').style.display = 'none';

    // ✅ 선택된 공지사항 삭제 요청
    function deleteSelectedNotices() {
        const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

        const selectedIds = Array.from(checkboxes())
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);

        if (selectedIds.length === 0) {
            alert("삭제할 항목을 선택하세요.");
            return;
        }

        if (!confirm("선택한 공지사항을 삭제하시겠습니까?")) {
            return;
        }

        fetch("/tellMe/customer/delete-notices", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken,
            },
            body: JSON.stringify({ ids: selectedIds }),
        })
            .then(response => {
                if (response.ok) {
                    alert("삭제되었습니다.");
                    location.reload();
                } else {
                    return response.text().then(text => {
                        console.error("Error response:", text);
                        alert("삭제 중 문제가 발생했습니다.");
                    });
                }
            })
            .catch(error => console.error("Fetch error:", error));
    }

    // ✅ 검색 버튼 클릭 시 검색 실행
    searchForm.addEventListener("submit", (event) => {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const query = searchInput.value.trim().toLowerCase(); // 검색어
        filteredPosts = allRows.filter(row => {
            const title = row.querySelector(".post-title a")?.textContent.toLowerCase() || "";
            return title.includes(query);
        });

        renderPosts(1); // 검색 후 1페이지부터 다시 렌더링
    });

    // ✅ 검색 후 리스트 렌더링 (체크박스 문제 해결 포함)
    function renderPosts(page) {
        tableBody.innerHTML = ""; // 기존 목록 초기화

        const totalPosts = filteredPosts.length;
        const totalPages = Math.ceil(totalPosts / postsPerPage);
        const start = (page - 1) * postsPerPage;
        const end = start + postsPerPage;
        const currentPosts = filteredPosts.slice(start, end);

        if (currentPosts.length === 0) {
            // ✅ 검색 결과가 없을 때 "검색 결과가 없습니다." 메시지 출력
            const noResultRow = document.createElement("tr");
            noResultRow.innerHTML = `<td colspan="4" style="text-align: center; padding: 15px; color: #888;">검색 결과가 없습니다.</td>`;
            tableBody.appendChild(noResultRow);
        } else {
            // ✅ 검색 결과가 있을 때 게시글 출력
            currentPosts.forEach(row => {
                const clonedRow = row.cloneNode(true); // 기존 행 복사
                tableBody.appendChild(clonedRow);
            });

            // ✅ 삭제 모드가 활성화된 경우, 검색 후에도 체크박스 유지
            if (deleteMode) {
                toggleDeleteMode(); // 삭제 모드 초기화 후 다시 적용
                toggleDeleteMode();
            }
        }

        renderPagination(page, totalPages); // 페이지네이션 업데이트
    }
    // 초기화 시 삭제 모드 버튼 숨기기
    document.querySelector('.delete-mode-buttons').style.display = 'none';

    // ✅ 이벤트 리스너 추가
    selectButton.addEventListener("click", toggleDeleteMode);
    deleteButton.addEventListener("click", deleteSelectedNotices);
    document.getElementById("cancel-button").addEventListener("click", toggleDeleteMode);
});
