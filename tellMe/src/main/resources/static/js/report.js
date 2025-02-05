document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search-input");
    const searchForm = document.getElementById("search-form");
    const tabs = document.querySelectorAll(".tab");

    // ✅ 현재 URL에서 파라미터 값을 가져오는 함수
    function getQueryParam(param) {
        const queryParams = new URLSearchParams(window.location.search);
        return queryParams.get(param) || "";
    }

    // ✅ 필터 변경 시 기존 검색어 유지하고 URL 변경
    function filterReports(filter) {
        const query = getQueryParam("query");  // 기존 검색어 유지
        window.location.href = `/manager/report?query=${encodeURIComponent(query)}&status=${encodeURIComponent(filter)}&page=1`;
    }


    // ✅ 검색 실행 (기본 HTML form action 사용)
    function searchReports(event) {
        event.preventDefault();
        searchForm.submit();  // Thymeleaf에서 action을 처리
    }

    // ✅ 탭 클릭 이벤트 (URL 변경)
    tabs.forEach(tab => {
        tab.addEventListener("click", function (event) {
            event.preventDefault();
            filterReports(this.dataset.filter);
        });
    });

    // ✅ 검색 이벤트 (form submit)
    if (searchForm) {
        searchForm.addEventListener("submit", searchReports);
    }
});
