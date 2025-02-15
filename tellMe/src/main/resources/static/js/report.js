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

    // ✅ 다운로드 링크 클릭 시 상태 변경 요청을 서버로 보내는 함수
    function updateReportStatus(reportId, linkElement) {
        fetch(`/manager/report/update-status/${reportId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ status: '확인완료' }),
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                    // 상태 셀 업데이트
                    const statusCell = row.querySelector('td:nth-child(5)'); // 5번째 <td> (상태 셀)
                    statusCell.textContent = '확인완료';
                    statusCell.classList.remove('unchecked');
                    statusCell.classList.add('checked');
                }
            }
        })
        .catch(error => {
            console.error('상태 변경 오류:', error);
        });
    }

    // ✅ 다운로드 링크 클릭 시 상태 변경
    downloadLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const reportId = e.target.closest('a').getAttribute('data-report-id'); // 각 보고서 ID 가져오기
            if (reportId) {
                updateReportStatus(reportId);  // 상태 변경 요청 보내기
            }
        });
    });
});
