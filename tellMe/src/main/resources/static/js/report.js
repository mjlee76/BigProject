document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search");
    const searchBtn = document.getElementById("search-btn");
    const tabs = document.querySelectorAll(".tab");
    const reportRows = document.querySelectorAll(".post-list tr");
    const prevBtn = document.querySelector(".prev");
    const nextBtn = document.querySelector(".next");
    const pageNum = document.querySelector(".page-num");

    let currentPage = 1;
    const rowsPerPage = 10;

    function filterReports(filter) {
        reportRows.forEach(row => {
            const statusCell = row.querySelector(".status");
            if (filter === "all" || (statusCell && statusCell.classList.contains(filter))) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    }

    function searchReports() {
        const query = searchInput.value.toLowerCase();
        reportRows.forEach(row => {
            const title = row.querySelector(".post-title a").textContent.toLowerCase();
            const author = row.cells[2].textContent.toLowerCase();
            row.style.display = title.includes(query) || author.includes(query) ? "" : "none";
        });
    }

    function paginateReports() {
        let totalRows = reportRows.length;
        let totalPages = Math.ceil(totalRows / rowsPerPage);
        pageNum.textContent = currentPage;

        reportRows.forEach((row, index) => {
            row.style.display = (index >= (currentPage - 1) * rowsPerPage && index < currentPage * rowsPerPage) ? "" : "none";
        });
    }

    tabs.forEach(tab => {
        tab.addEventListener("click", function () {
            tabs.forEach(t => t.classList.remove("active"));
            this.classList.add("active");
            filterReports(this.dataset.filter);
        });
    });

    searchBtn.addEventListener("click", searchReports);
    searchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") searchReports();
    });

    prevBtn.addEventListener("click", function () {
        if (currentPage > 1) {
            currentPage--;
            paginateReports();
        }
    });

    nextBtn.addEventListener("click", function () {
        let totalPages = Math.ceil(reportRows.length / rowsPerPage);
        if (currentPage < totalPages) {
            currentPage++;
            paginateReports();
        }
    });

    paginateReports();
});