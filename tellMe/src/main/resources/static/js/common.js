$(document).ready(function() {
   $("#logoutLink").on("click", function(e) {
      e.preventDefault();
      document.logoutForm.submit();
   });
});

//알림종
document.addEventListener("DOMContentLoaded", function () {
    const notificationIcon = document.getElementById("notification-icon");
    const userId = notificationIcon.dataset.userid;
    const notificationBox = document.getElementById("notification-box");
    const notificationList = document.getElementById("notification-list");
    const notificationCount = document.getElementById("notification-count");

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

    // 알림 개수 표시 함수
    function showNotificationBadge(count) {
        if (count > 0) {
            notificationCount.textContent = count;
            notificationCount.style.display = "block";
        } else {
            notificationCount.style.display = "none";
        }
    }

    // 알림 추가 함수 (localStorage 반영)
    function addNotificationToList(message) {
        const newNotification = document.createElement("li");
        newNotification.textContent = message;
        newNotification.classList.add("new-notification");
        notificationList.prepend(newNotification);
    }

    // 서버에서 알림 가져오기
    async function fetchNotifications() {
        try {
            const response = await fetch(`/tellMe/api/${userId}`);
            const notifications = await response.json();

            notificationList.innerHTML = ""; // 기존 알림 초기화
            notifications.forEach(addNotificationToList);
            showNotificationBadge(notifications.length);
        } catch (error) {
            console.error("알림을 불러오는 중 오류 발생:", error);
        }
    }

    // SSE 연결 (실시간 알림 받기)
    if (userId) {
        const eventSource = new EventSource(`/tellMe/api/notiBell/${userId}`);

        eventSource.onmessage = function (event) {
            console.log("🔔 새 알림 수신: ", event.data);
            location.reload();
        };

        eventSource.onerror = function () {
            console.error("SSE 연결 오류 발생");
            eventSource.close();
            setTimeout(() => {
                eventSource = new EventSource(`/tellMe/api/notiBell/${userId}`);
            }, 3000);
        };
    }
    fetchNotifications();
});

//document.addEventListener("DOMContentLoaded", function () {
//  const notificationCount = document.getElementById("notification-count");
//
//  function showNotificationBadge(count) {
//      notificationCount.textContent = count;
//      notificationCount.style.display = "block"; // 배지 표시
//  }
//});

