// ✅ Chart.js 기본 설정
document.addEventListener("DOMContentLoaded", function () {
    drawAreaChart();
    drawBarChart();
    drawPieChart();
});

// ✅ 영역 차트 (Area Chart)
function drawAreaChart() {
    var ctx = document.getElementById("areaChart").getContext("2d");
    new Chart(ctx, {
        type: "line",
        data: {
            labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월"],
            datasets: [{
                label: "이용자 수",
                data: [100, 200, 150, 300, 250, 400, 350],
                backgroundColor: "rgba(54, 162, 235, 0.2)",
                borderColor: "rgba(54, 162, 235, 1)",
                borderWidth: 2,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: { grid: { display: false } },
                y: { beginAtZero: true }
            }
        }
    });
}

// ✅ 막대 차트 (Bar Chart)
function drawBarChart() {
    var ctx = document.getElementById("barChart").getContext("2d");
    new Chart(ctx, {
        type: "bar",
        data: {
            labels: ["제품 A", "제품 B", "제품 C", "제품 D"],
            datasets: [{
                label: "판매량",
                data: [120, 180, 90, 220],
                backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0"],
                borderColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0"],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: { grid: { display: false } },
                y: { beginAtZero: true }
            }
        }
    });
}

// ✅ 도넛 차트 (Pie Chart)
function drawPieChart() {
    var ctx = document.getElementById("pieChart").getContext("2d");
    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: ["남성", "여성", "기타"],
            datasets: [{
                data: [50, 40, 10],
                backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56"],
                hoverBackgroundColor: ["#FF6384", "#36A2EB", "#FFCE56"]
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: "bottom" }
            }
        }
    });
}
