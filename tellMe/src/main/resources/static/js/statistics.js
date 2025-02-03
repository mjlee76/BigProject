document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");
    const content = document.getElementById("content");
    let chartInstance = null; // ✅ 기존 차트 저장용

    const pageData = {
        "inquiry-stats": `
            <h2 class="text-center">문의 통계</h2>
            <div class="chart-wrapper">
                <div class="chart-container">
                    <canvas id="barChart"></canvas>
                </div>
            </div>
        `,
        "daily-stats": `
            <h2 class="text-center">일별 통계</h2>
            <div class="chart-wrapper">
                <div class="chart-container">
                    <canvas id="pieChart"></canvas>
                </div>
            </div>
        `,
        "monthly-stats": `
            <h2 class="text-center">월별 통계</h2>
            <div class="chart-wrapper">
                <div class="chart-container">
                    <canvas id="barChart"></canvas>
                </div>
            </div>
        `,
        "yearly-stats": `
            <h2 class="text-center">년별 통계</h2>
            <div class="chart-wrapper">
                <div class="chart-container">
                    <canvas id="pieChart"></canvas>
                </div>
            </div>
        `,
    };

    function removeExistingChart() {
        if (chartInstance) {
            chartInstance.destroy();
            chartInstance = null;
        }
    }

    function loadCharts(target) {
        removeExistingChart(); // ✅ 기존 차트 제거

        setTimeout(() => {
            let canvas = document.querySelector("canvas");
            if (!canvas) return; // ✅ 캔버스가 없으면 실행 중지

            let ctx = canvas.getContext("2d");

            if (target === "inquiry-stats" || target === "monthly-stats") {
                // ✅ 바 차트 (Bar Chart) - 데이터 및 라벨 수정
                chartInstance = new Chart(ctx, {
                    type: "bar",
                    data: {
                        labels: [
                            "폭언X(0)", "폭언(1)", "단순욕설(2)", "외모(3)",
                            "장애인(4)", "인종(5)", "종교(6)", "지역(7)",
                            "성차별(8)", "나이(9)", "협박(10)", "성희롱(11)"
                        ],
                        datasets: [{
                            label: "점수",
                            data: [65, 59, 80, 81, 56, 72, 45, 90, 66, 74, 50, 68],
                            backgroundColor: [
                                "#FF6384", "#36A2EB", "#FFCE56", "#9966FF",
                                "#FF9F40", "#C9CBCF", "#8AC926", "#FF595E",
                                "#6A4C93", "#1982C4", "#FFCA3A", "#D72638"
                            ]
                        }]
                    },
                    options: { responsive: true, scales: { y: { beginAtZero: true } } }
                });
            }

            if (target === "daily-stats" || target === "yearly-stats") {
                // ✅ 파이 차트 (Pie Chart)
                chartInstance = new Chart(ctx, {
                    type: "pie",
                    data: {
                        labels: ["Direct", "Social", "Referral"],
                        datasets: [{
                            data: [55, 35, 10],
                            backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56"]
                        }]
                    },
                    options: { responsive: true, plugins: { legend: { position: "bottom" } } }
                });
            }
        }, 300);
    }

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            menuItems.forEach(menu => menu.classList.remove("active"));
            this.classList.add("active");

            const target = this.getAttribute("data-target");
            content.innerHTML = pageData[target] || "<h2>페이지를 찾을 수 없습니다.</h2>";

            loadCharts(target);
        });
    });

    // ✅ 기본 페이지 로드 (문의 통계)
    content.innerHTML = pageData["inquiry-stats"];
    setTimeout(() => loadCharts("inquiry-stats"), 300);

    // ✅ 사이드바 접기 / 펼치기 기능
    document.getElementById("toggleSidebar").addEventListener("click", function () {
        document.getElementById("sidebar").classList.toggle("toggled");
    });
});
