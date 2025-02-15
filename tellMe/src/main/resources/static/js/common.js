$(document).ready(function() {
   $("#logoutLink").on("click", function(e) {
      e.preventDefault();
      document.logoutForm.submit();
   });
});

document.addEventListener("DOMContentLoaded", function () {
    const notificationIcon = document.getElementById("notification-icon");
    const notificationBox = document.getElementById("notification-box");
    const notificationList = document.getElementById("notification-list");
    const notificationCount = document.getElementById("notification-count");
    let csrfToken = document.querySelector('input[name="_csrf"]').value;

    const userId = window.userId;

    if (typeof userId === "undefined" || !userId || userId === "anonymous") {
        console.error("❌ userId가 정의되지 않았습니다. SSE를 실행할 수 없습니다.");
    } else {
    //    const notificationIcon = document.getElementById("notification-icon");
    //    const userId = notificationIcon ? notificationIcon.dataset.userid : null;
        console.log(userId);
        let eventSource = new EventSource(`/tellMe/api/sse/${userId}`);

        // ✅ 알림 이벤트 처리
        eventSource.addEventListener("notification", function (event) {
            console.log("🔔 새로운 알림 수신:", event.data);
            //alert(event.data); // 알림 UI 업데이트 로직 추가
            fetchNotifications(); // 새 알림 수신 시 UI 갱신
        });

        // ✅ 새로고침 이벤트 처리
        eventSource.addEventListener("refresh", function (event) {
            console.log("🔄 새로고침 이벤트 수신!");
            location.reload(); // 페이지 새로고침
        });

        // 오류 발생 시 자동 재연결
        eventSource.onerror = function () {
            console.error("❌ SSE 연결 오류. 재연결 시도 중...");
            eventSource.close();
            setTimeout(() => {
                eventSource = new EventSource(`/tellMe/api/sse/${userId}`);
            }, 3000);
        };

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
        function showNotificationBadge(notifications) {
            const unreadCount = notifications.filter(n => !n.read).length;
            if (unreadCount > 0) {
                notificationCount.textContent = unreadCount ;
                notificationCount.style.display = "block";
            } else {
                notificationCount.style.display = "none";
            }
        }

        // ✅ 알림 클릭 시 읽음 상태 업데이트
        async function markAsRead(notificationId) {
            try{
                const response = await fetch(`/tellMe/api/markAsRead`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        "X-CSRF-TOKEN": csrfToken
                    },
                    body: JSON.stringify({
                        id: notificationId
                    })
                });

                if (response.ok) {
                    console.log(`✅ 알림(${notificationId})을 읽음 상태로 변경`);

//                    const notificationElement = document.querySelector([data-id="${notificationId}"]);
//                    if (notificationElement) {
//                        notificationElement.classList.add("read"); // ✅ 읽은 알림 스타일 적용
//                    }

                    await fetchNotifications(); // ✅ DB 변경 후 즉시 UI 반영
                } else {
                    console.error("❌ 알림 상태 변경 실패");
                }
                //UI에서 읽음 상태 반영
                //element.classList.add("read");
            }catch (error) {
                console.error("❌ 알림 읽음 처리 실패:", error);
            }
        }

        // 알림 추가 함수 (읽음 상태 반영)
        function addNotificationToList(notification) {
            const newNotification = document.createElement("li");
            newNotification.textContent = notification.message;
            newNotification.classList.add("notification-item");

            // ✅ isRead가 true이면 어둡게 표시
            if (notification.read) {
                newNotification.classList.add("read");
            }

            // ✅ 알림 클릭 시 읽음 처리
            newNotification.addEventListener("click", function () {
                if (!notification.isRead) {
                    markAsRead(notification.id);
                }
            });

            notificationList.prepend(newNotification);
        }

        // 서버에서 알림 가져오기
        async function fetchNotifications() {
            try {
                const response = await fetch(`/tellMe/api/notifiList/${userId}`);
                if (!response.ok) throw new Error(`HTTP 오류: ${response.status}`);
                const notifications = await response.json();
                console.log(notifications);

                notificationList.innerHTML = ""; // 기존 알림 초기화
                notifications.reverse().forEach(addNotificationToList);
                showNotificationBadge(notifications);
            } catch (error) {
                console.error("알림을 불러오는 중 오류 발생:", error);
            }
        }
        fetchNotifications();
    }
});

