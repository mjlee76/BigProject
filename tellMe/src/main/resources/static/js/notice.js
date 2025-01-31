// ✅ 삭제 모드 활성화/비활성화 함수
let deleteMode = false; // 현재 삭제 모드 여부를 저장

function toggleDeleteMode() {
    const checkboxes = document.querySelectorAll('.delete-checkbox'); // 체크박스 목록
    const checkboxColumns = document.querySelectorAll('.checkbox-column'); // 체크박스 열
    const deleteButton = document.getElementById('delete-button'); // 삭제 버튼
    const selectButton = document.getElementById('select-button'); // 선택 버튼

    if (!deleteMode) {
        // ✅ 삭제 모드 활성화
        checkboxes.forEach(checkbox => checkbox.style.display = 'inline-block'); // 체크박스 표시
        checkboxColumns.forEach(column => column.style.display = 'table-cell'); // 체크박스 열 표시
        deleteButton.style.display = 'inline-block'; // 삭제 버튼 표시
        selectButton.innerText = '취소'; // 선택 버튼 텍스트 변경
    } else {
        // ✅ 삭제 모드 비활성화
        checkboxes.forEach(checkbox => {
            checkbox.checked = false; // 체크박스 선택 해제
            checkbox.style.display = 'none';
        }); // 체크박스 숨김
        checkboxColumns.forEach(column => column.style.display = 'none'); // 체크박스 열 숨김
        deleteButton.style.display = 'none'; // 삭제 버튼 숨김
        selectButton.innerText = '선택'; // 선택 버튼 텍스트 초기화
    }

    deleteMode = !deleteMode; // 삭제 모드 상태 토글
}

// ✅ 선택된 ID를 한 번에 삭제 요청
function deleteSelectedNotices() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    const selectedIds = Array.from(document.querySelectorAll('.delete-checkbox:checked'))
        .map(checkbox => checkbox.value);

    if (selectedIds.length === 0) {
        alert('삭제할 항목을 선택하세요.');
        return;
    }

    if (!confirm('선택한 공지사항을 삭제하시겠습니까?')) {
        return;
    }

    // ✅ POST 요청으로 ID 목록 전송
    fetch('/tellMe/customer/delete-notices', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken, // CSRF 토큰 추가
        },
        body: JSON.stringify({ ids: selectedIds }), // 선택된 ID 목록을 JSON으로 전송
    })
        .then(response => {
            if (response.ok) {
                alert('삭제되었습니다.');
                location.reload(); // 성공 시 페이지 새로고침
            } else {
                return response.text().then(text => {
                    console.error("Error response:", text);
                    alert('삭제 중 문제가 발생했습니다.');
                });
            }
        })
        .catch(error => console.error('Fetch error:', error)); // 네트워크 오류 처리
}