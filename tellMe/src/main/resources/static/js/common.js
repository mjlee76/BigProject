$(document).ready(function() {
   $("#logoutLink").on("click", function(e) {
      e.preventDefault();
      document.logoutForm.submit();
   });
});

//알림종
//document.addEventListener("DOMContentLoaded", function () {
//    let notificationIcon = document.getElementById("notification-icon");
//    let userId = notificationIcon ? notificationIcon.dataset.userid : null;
//
//    if (!userId) {
//        console.warn("🔍 [SSE] 로그인된 사용자가 아닙니다. SSE 연결을 하지 않습니다.");
//        return; // 로그인하지 않은 사용자는 SSE 연결 X
//    }
//    console.log("연결 시작 - userId:", userId);
//    function connectSSE() {
//        let eventSource = new EventSource(`/tellMe/api/notiBell/${userId}`);
//
//        eventSource.onmessage = function(event) {
//            console.log("📢 [SSE] 메시지 수신:", event.data);
//            showNotification(event.data);
//        };
//
//        eventSource.onerror = function(event) {
//            console.error("🚨 [SSE 오류 발생] 연결 끊김");
//
//            if (event.target.readyState === EventSource.CLOSED) {
//                console.warn("🔄 [SSE] 서버가 닫힘. 5초 후 재연결 시도...");
//                setTimeout(() => {
//                    connectSSE();
//                }, 5000);
//            }
//
//            if (event.target.readyState === EventSource.CONNECTING) {
//                console.warn("⏳ [SSE] 서버가 응답하지 않음. 재연결 중...");
//            }
//        };
//    }
//
//    connectSSE();
//
//    function showNotification(message) {
//        let countElement = document.getElementById("notification-count");
//        let notificationList = document.getElementById("notification-list");
//
//        // ✅ 알림 숫자 업데이트
//        let currentCount = parseInt(countElement.textContent) || 0;
//        countElement.textContent = currentCount + 1;
//        countElement.style.display = "inline-block";
//
//        // ✅ 알림 리스트에 추가
//        let li = document.createElement("li");
//        li.textContent = message;
//        li.onclick = () => removeNotification(li);
//        notificationList.appendChild(li);
//    }
//
//    function removeNotification(element) {
//        element.remove();
//        let countElement = document.getElementById("notification-count");
//        let currentCount = parseInt(countElement.textContent) || 0;
//        countElement.textContent = Math.max(currentCount - 1, 0);
//        if (countElement.textContent == "0") {
//            countElement.style.display = "none";
//        }
//    }
//
//    // 🔔 아이콘 클릭 시 알림 창 토글
//    if (notificationIcon) {
//        notificationIcon.addEventListener("click", function () {
//            let box = document.getElementById("notification-box");
//            box.style.display = (box.style.display === "none") ? "block" : "none";
//        });
//    }
//});