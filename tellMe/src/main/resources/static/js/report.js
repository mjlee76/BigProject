document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("search-input");
    const searchForm = document.getElementById("search-form");
    const tabs = document.querySelectorAll(".tab");
    const downloadLinks = document.querySelectorAll('.download-link');  // 다운로드 링크 선택자

    // ✅ 현재 URL에서 파라미터 값을 가져오는 함수
    function getQueryParam(param) {
        const queryParams = new URLSearchParams(window.location.search);
        return queryParams.get(param) || "";
    }

    // ✅ 필터 변경 시 기존 검색어 유지하고 URL 변경
    function filterReports(filter) {
        const query = getQueryParam("query");  // 기존 검색어 유지
        const baseUrl = window.location.origin + "/tellMe/manager/report";  // 절대 경로로 변경
        const url = `${baseUrl}?query=${encodeURIComponent(query)}&status=${encodeURIComponent(filter)}&page=1`;
        window.location.href = url;  // URL 변경
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

function confirmAndDownload(reportId, reportName) {
    //const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    fetch('/tellMe/api/download/report/' + reportName)
        .then(response => {
            if (!response.ok) {
                throw new Error('File download failed: ' + response.status);
            }
            return response.blob();
        })
        .then(blob => {
            // 받은 Blob 데이터를 가상의 URL(Object URL)로 만든다
            const downloadUrl = URL.createObjectURL(blob);

            // a 태그를 동적으로 만들어서 클릭 이벤트 발생 -> 다운로드
            const a = document.createElement('a');
            a.href = downloadUrl;
            // 여기서 파일명을 지정해주면, 다운로드될 때 해당 파일명으로 저장됨
            // (서버에서 응답 헤더로도 filename을 내려주지만, JS에서 덮어쓸 수도 있음)
            a.download = reportName;

            document.body.appendChild(a);
            a.click();
            a.remove();

            // 메모리 누수 방지를 위해 URL 해제
            URL.revokeObjectURL(downloadUrl);
        })
        .then(() => {
            // 3) 상태 업데이트 API (POST 요청)
            return fetch(`/tellMe/report/updateStatus/${reportId}/confirm`, {
            method: 'POST',
            headers: {
                "Content-Type": "application/json",
                "X-CSRF-TOKEN": csrfToken
            },});
        })
        .then(updateRes => {
            if (!updateRes.ok) throw new Error('Confirm API error');
            // 4) 화면의 상태 표시를 '확인완료'로 변경
            document.getElementById(`status-${reportId}`).textContent = '확인완료';
        })
        .catch(error => {
            console.error('Error downloading file:', error);
        });
}
