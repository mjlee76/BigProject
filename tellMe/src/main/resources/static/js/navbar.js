document.addEventListener("DOMContentLoaded", function () {
    const notificationIcon = document.getElementById("notification-icon");
    const notificationBox = document.getElementById("notification-box");

    // 알림 아이콘 클릭 시 알림 박스 토글
    notificationIcon.addEventListener("click", function (event) {
        event.stopPropagation(); // 클릭 이벤트 전파 방지

        if (notificationBox.style.display === "none" || notificationBox.style.opacity === "0") {
            // 박스 나타내기
            notificationBox.style.display = "block";
            setTimeout(() => {
                notificationBox.style.opacity = "1";
                notificationBox.style.transform = "translateY(0)"; // 부드러운 애니메이션 효과
            }, 10);
        } else {
            // 박스 숨기기
            notificationBox.style.opacity = "0";
            notificationBox.style.transform = "translateY(-10px)";
            setTimeout(() => {
                notificationBox.style.display = "none";
            }, 300); // 애니메이션이 끝난 후 display를 none으로 변경
        }
    });

    // 문서 클릭 시 알림 박스 닫기
    document.addEventListener("click", function (event) {
        if (!notificationIcon.contains(event.target) && !notificationBox.contains(event.target)) {
            notificationBox.style.opacity = "0";
            notificationBox.style.transform = "translateY(-10px)";
            setTimeout(() => {
                notificationBox.style.display = "none";
            }, 300);
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const notificationCount = document.getElementById("notification-count");

    function showNotificationBadge(count) {
        notificationCount.textContent = count;
        notificationCount.style.display = "block"; // 배지 표시
    }

    // 테스트용: 알림 개수를 3으로 설정
    showNotificationBadge(3);
});
