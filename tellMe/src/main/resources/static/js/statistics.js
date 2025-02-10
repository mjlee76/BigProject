
//document.addEventListener("DOMContentLoaded", function () {
//    // 민원 처리 상태별 파이 차트
//    var ctx = document.getElementById('statusChart').getContext('2d');
//    var statusChart = new Chart(ctx, {
//        type: 'pie',
//        data: {
//            labels: ['접수중', '처리중'],
//            datasets: [{
//                label: '민원 처리 상태',
//                data: [
//                    /* 통계 데이터 (서버에서 전달된 값으로 대체됨) */
//                    [[${statistics.접수중Count}]],
//                    [[${statistics.처리중Count}]]
//                ],
//                backgroundColor: [
//                    'rgba(255, 99, 132, 0.2)',
//                    'rgba(54, 162, 235, 0.2)'
//                ],
//                borderColor: [
//                    'rgba(255, 99, 132, 1)',
//                    'rgba(54, 162, 235, 1)'
//                ],
//                borderWidth: 1
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });
//
//    // 보고서 상태별 파이 차트
//    var reportCtx = document.getElementById('reportstatusChart').getContext('2d');
//    var reportstatusChart = new Chart(reportCtx, {
//        type: 'pie',
//        data: {
//            labels: ['미확인', '확인완료'],
//            datasets: [{
//                label: '보고서 상태',
//                data: [
//                    /* 통계 데이터 (서버에서 전달된 값으로 대체됨) */
//                    [[${statistics.미확인Count}]],
//                    [[${statistics.확인완료Count}]]
//                ],
//                backgroundColor: [
//                    'rgba(255, 159, 64, 0.2)',
//                    'rgba(75, 192, 192, 0.2)'
//                ],
//                borderColor: [
//                    'rgba(255, 159, 64, 1)',
//                    'rgba(75, 192, 192, 1)'
//                ],
//                borderWidth: 1
//            }]
//        },
//        options: {
//            responsive: true
//        }
//    });
//});