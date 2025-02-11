document.addEventListener("DOMContentLoaded", function() {
    // 실시간 민원 현황 차트
    var realTimeCtx = document.getElementById('realTimeChart').getContext('2d');
    var realTimeChart = new Chart(realTimeCtx, {
        type: 'line',
        data: {
            labels: ['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00', '08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00', '19:00', '20:00', '21:00', '22:00', '23:00'],
            datasets: [{
                label: '일반 민원',
                data: questionsAndMaliciousByHour.normal,  // 일반 민원의 시간대별 데이터
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }, {
                label: '악성 민원',
                data: questionsAndMaliciousByHour.malicious,  // 악성 민원의 시간대별 데이터
                borderColor: 'rgba(255, 99, 132, 1)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });

    // 민원 처리 상태별 파이 차트
    var ctx = document.getElementById('statusChart').getContext('2d');
    var statusChart = new Chart(ctx, {
        plugins: [ChartDataLabels],
        type: 'doughnut',
        data: {
            labels: ['악성민원', '일반민원'],
            datasets: [{
                label: '민원 종류 비율',
                data: [
                    statistics.악성민원수,
                    statistics.일반민원수
                ],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            plugins: {
                datalabels: {
                    color: 'black',
                    font: {
                        size: 14,
                        weight: 'bold'
                    },
                    formatter: function (value, context) {
                        var idx = context.dataIndex; // 각 데이터 인덱스

                        // 출력 텍스트
                        return context.chart.data.labels[idx] + value + '건';
                    }
                }
            }
        }
    });



});