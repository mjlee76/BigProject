function updateContent(newMainText) {
    // main-text와 sub-text 요소를 가져옵니다.
    const mainText = document.getElementById('main-text');
    const subText = document.getElementById('sub-text');
    const contactInfo = document.getElementById('contact-info');
    const contactIcon = document.getElementById('contact-icon');
    const contactSt = document.getElementById('contact-strong');

    // main-text의 내용을 업데이트합니다.
    mainText.textContent = newMainText;

    // main-text 내용에 따라 sub-text의 내용을 업데이트합니다.
    if (newMainText.includes('유선 상담')) {
        subText.textContent = '유선 상담을 통해 상세한 안내를 받을 수 있습니다.';
        contactIcon.textContent = '📞'; // Update icon
        contactInfo.textContent = '02)873-4466'
        contactSt.textContent = 'Contact : '
    } else if (newMainText.includes('채팅 상담')) {
        subText.textContent = '채팅 상담으로 실시간 도움을 받을 수 있습니다.';
        contactIcon.textContent = '💬'; // Update icon
        contactInfo.textContent = 'chat@example.com'
        contactSt.textContent = 'Contact : '
    } else if (newMainText.includes('기술 지원')) {
        subText.textContent = '기술 지원과 관련된 문의는 이곳에서 처리됩니다.';
        contactIcon.textContent = '🛠'; // Update icon
        contactInfo.textContent = 'support@example.com'
        contactSt.textContent = 'Contact : '
    } else {
        subText.textContent = '궁금한 내용을 게시판에 물어보세요.';
    }

    contactInfo.textContent = newContactInfo;
}