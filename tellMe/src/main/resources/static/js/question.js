document.addEventListener("DOMContentLoaded", function () {
    const tabs = document.querySelectorAll(".tab");

    // URL에서 현재 status 값 가져오기
    function getCurrentStatus() {
        const params = new URLSearchParams(window.location.search);
        return params.get("status") || "all";
    }

    // 현재 status에 맞게 active 클래스 설정
    function updateActiveTab() {
        const currentStatus = getCurrentStatus();
        tabs.forEach(tab => {
            tab.classList.remove("active");
            if (tab.dataset.status === currentStatus) {
                tab.classList.add("active");
            }
        });
    }

    // 탭 클릭 이벤트 리스너 추가
    tabs.forEach(tab => {
        tab.addEventListener("click", function (event) {
            event.preventDefault();

            const selectedStatus = this.dataset.status;
            const query = new URLSearchParams(window.location.search);
            query.set("status", selectedStatus);
            query.set("page", 1);  // 필터 변경 시 1페이지로 이동

            window.location.href = window.location.pathname + "?" + query.toString();
        });
    });

    // 페이지 로드 시 active 업데이트
    updateActiveTab();
});
