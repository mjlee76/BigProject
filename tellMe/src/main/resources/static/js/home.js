// 초기화 함수: 페이지 로드 시 기본값 설정
function initializeContent() {
    updateContent('유선 상담을 원하시나요?'); // 기본값: "유선 상담"
}

// 업데이트 함수: 버튼 클릭 시 호출
function updateContent(newMainText) {
    // 요소 가져오기
    const mainText = document.getElementById('main-text');
    const subText = document.getElementById('sub-text');
    const contactInfo = document.getElementById('contact-info');
    const contactIcon = document.getElementById('contact-icon');
    const contactSt = document.getElementById('contact-strong');

    // main-text 업데이트
    mainText.textContent = newMainText;

    // main-text에 따라 sub-text와 기타 정보를 업데이트
    if (newMainText.includes('유선 상담')) {
        subText.textContent = '유선 상담을 통해 상세한 안내를 받을 수 있습니다.';
        contactIcon.textContent = '📞'; // 아이콘 업데이트
        contactInfo.textContent = '02)873-4466'; // 연락처 업데이트
        contactSt.textContent = 'Contact : ';
    } else if (newMainText.includes('채팅 상담')) {
        subText.textContent = '채팅 상담으로 실시간 도움을 받을 수 있습니다.';
        contactIcon.textContent = '💬'; // 아이콘 업데이트
        contactInfo.textContent = 'chat@example.com'; // 연락처 업데이트
        contactSt.textContent = 'Contact : ';
    } else if (newMainText.includes('기술 지원')) {
        subText.textContent = '기술 지원과 관련된 문의는 이곳에서 처리됩니다.';
        contactIcon.textContent = '🛠'; // 아이콘 업데이트
        contactInfo.textContent = 'support@example.com'; // 연락처 업데이트
        contactSt.textContent = 'Contact : ';
    } else {
        subText.textContent = '유선 상담을 통해 상세한 안내를 받을 수 있습니다.';
        contactIcon.textContent = '📞'; // 아이콘 업데이트
        contactInfo.textContent = '02)873-4466'; // 연락처 업데이트
        contactSt.textContent = 'Contact : ';
    }
}

// 페이지 로드 시 초기화 함수 호출
window.onload = initializeContent;
