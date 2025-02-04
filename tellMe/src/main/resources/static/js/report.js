document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search-input");
    const searchForm = document.getElementById("search-form");
    const tabs = document.querySelectorAll(".tab");
    const reportRows = document.querySelectorAll(".post-table tbody tr");
    const prevBtn = document.querySelector(".prev");
    const nextBtn = document.querySelector(".next");
    const pageNum = document.querySelector(".page-num");

    let currentPage = 1;
    const rowsPerPage = 10;

    // ✅ 상태 필터링 함수
    function filterReports(filter) {
        reportRows.forEach(row => {
            const statusCell = row.querySelector("td:nth-child(5)").textContent.trim();
            row.style.display = (filter === "all" || statusCell === filter) ? "" : "none";
        });
    }

    // ✅ 검색 기능 개선 (제목, 유형, 상태 검색 가능)
    function searchReports(event) {
        event.preventDefault(); // 폼 제출 방지
        const query = searchInput.value.toLowerCase();

        reportRows.forEach(row => {
            const title = row.querySelector("td:nth-child(2) a").textContent.toLowerCase();
            const category = row.querySelector("td:nth-child(4)").textContent.toLowerCase();
            const status = row.querySelector("td:nth-child(5)").textContent.toLowerCase();

            row.style.display = title.includes(query) || category.includes(query) || status.includes(query) ? "" : "none";
        });
    }

    // ✅ 페이지네이션 기능
    function paginateReports() {
        let totalRows = reportRows.length;
        let totalPages = Math.ceil(totalRows / rowsPerPage);
        if (pageNum) pageNum.textContent = currentPage;

        reportRows.forEach((row, index) => {
            row.style.display = (index >= (currentPage - 1) * rowsPerPage && index < currentPage * rowsPerPage) ? "" : "none";
        });
    }

    // ✅ 탭 클릭 이벤트 (필터링)
    tabs.forEach(tab => {
        tab.addEventListener("click", function () {
            tabs.forEach(t => t.classList.remove("active"));
            this.classList.add("active");
            filterReports(this.dataset.filter);
        });
    });

    // ✅ 검색 이벤트 등록
    if (searchForm) {
        searchForm.addEventListener("submit", searchReports);
    }

    // ✅ 페이지네이션 버튼 이벤트 (예외 처리 추가)
    if (prevBtn) {
        prevBtn.addEventListener("click", function () {
            if (currentPage > 1) {
                currentPage--;
                paginateReports();
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener("click", function () {
            let totalPages = Math.ceil(reportRows.length / rowsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                paginateReports();
            }
        });
    }

    paginateReports(); // 초기 로딩 시 적용
});
