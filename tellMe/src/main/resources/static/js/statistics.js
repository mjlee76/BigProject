document.addEventListener("DOMContentLoaded", function () {
    const datePicker = document.getElementById("date-picker");
    const searchBtn = document.getElementById("search-btn");
    const currentDateElement = document.getElementById("current-date");

    // ✅ 현재 날짜 설정
    function setCurrentDate() {
        const today = new Date();
        const formattedDate = today.toISOString().split("T")[0]; // YYYY-MM-DD 형식
        currentDateElement.textContent = `현재 날짜: ${formattedDate}`;
        datePicker.value = formattedDate; // 날짜 선택창 기본값 설정
    }

    setCurrentDate(); // ✅ 페이지 로드 시 현재 날짜 표시

    // ✅ 날짜별 통계 검색 기능
    searchBtn.addEventListener("click", function () {
        const selectedDate = datePicker.value; // 사용자가 선택한 날짜 가져오기
        if (!selectedDate) {
            alert("날짜를 선택해주세요!");
            return;
        }

        // ✅ 선택한 날짜로 데이터 요청
        fetch(`/api/statistics/daily?date=${selectedDate}`)
            .then(response => response.json())
            .then(data => {
                updateDailyChart(data); // ✅ 차트 업데이트 실행
            })
            .catch(error => console.error("데이터 불러오기 오류:", error));
    });

    // ✅ 날짜 선택 변경 시 자동 검색 (선택 사항)
    datePicker.addEventListener("change", function () {
        searchBtn.click();
    });
});


document.addEventListener("DOMContentLoaded", function () {
    const menuItems = document.querySelectorAll(".menu-item");

    // 각 통계별 div 요소 가져오기
    const sections = {
        "daily-complaints": document.getElementById("daily-complaints-content"),
        "monthly-complaints": document.getElementById("monthly-complaints-content"),
        "yearly-complaints": document.getElementById("yearly-complaints-content"),
        "malicious-type": document.getElementById("malicious-type-content"),
        "malicious-gender": document.getElementById("malicious-gender-content")
    };

    let chartInstances = {
        dailyChartInstance: null,
        monthlyChartInstance: null,
        yearlyChartInstance: null,
        maliciousTypeChartInstance: null,
        genderPieChartInstance: null,
        agePieChartInstance: null
    };

    function removeExistingChart(chartInstance) {
        if (chartInstance) {
            chartInstance.destroy();
            chartInstance = null;
        }
    }

    function showSection(target) {
        Object.values(sections).forEach(section => section.style.display = "none");
        if (sections[target]) sections[target].style.display = "flex";
    }

    function loadDailyChart() {
        removeExistingChart(chartInstances.dailyChartInstance);
        let ctx = document.getElementById("dailyAreaChart").getContext("2d");

        chartInstances.dailyChartInstance = new Chart(ctx, {
            type: "line",
            data: {
                labels: ["1일", "2일", "3일", "4일", "5일", "6일", "7일"],
                datasets: [{
                    label: "일별 민원 수",
                    data: [10, 15, 20, 25, 22, 18, 30],
                    fill: true,
                    backgroundColor: "rgba(255, 99, 132, 0.2)",
                    borderColor: "rgba(255, 99, 132, 1)",
                    pointRadius: 5,
                    tension: 0.4
                }]
            },
            options: { responsive: true }
        });
    }

    function loadMonthlyChart() {
        removeExistingChart(chartInstances.monthlyChartInstance);
        let ctx = document.getElementById("monthlyAreaChart").getContext("2d");

        chartInstances.monthlyChartInstance = new Chart(ctx, {
            type: "line",
            data: {
                labels: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"],
                datasets: [{
                    label: "월별 민원 수",
                    data: [100, 120, 140, 130, 160, 200, 250, 230, 220, 240, 270, 300],
                    fill: true,
                    backgroundColor: "rgba(54, 162, 235, 0.2)",
                    borderColor: "rgba(54, 162, 235, 1)",
                    pointRadius: 5,
                    tension: 0.4
                }]
            },
            options: { responsive: true }
        });
    }

    function loadYearlyChart() {
        removeExistingChart(chartInstances.yearlyChartInstance);
        let ctx = document.getElementById("yearlyPieChart").getContext("2d");

        chartInstances.yearlyChartInstance = new Chart(ctx, {
            type: "pie",
            data: {
                labels: ["2023년", "2022년", "2021년"],
                datasets: [{
                    data: [300, 250, 200],
                    backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56"]
                }]
            },
            options: { responsive: true }
        });
    }

    function loadMaliciousTypeChart() {
        removeExistingChart(chartInstances.maliciousTypeChartInstance);
        let ctx = document.getElementById("maliciousTypeChart").getContext("2d");

        chartInstances.maliciousTypeChartInstance = new Chart(ctx, {
            type: "bar",
            data: {
                labels: ["폭언X(0)", "폭언(1)", "단순욕설(2)", "외모(3)", "장애인(4)", "인종(5)", "종교(6)", "지역(7)", "성차별(8)", "나이(9)", "협박(10)", "성희롱(11)"],
                datasets: [{
                    label: "유형별 민원 수",
                    data: [30, 50, 40, 25, 35, 20, 15, 18, 22, 28, 10, 14],
                    backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#9966FF", "#FF9F40", "#C9CBCF", "#8AC926", "#FF595E", "#6A4C93", "#1982C4", "#FFCA3A", "#D72638"]
                }]
            },
            options: { responsive: true }
        });
    }

    function loadGenderChart() {
        removeExistingChart(chartInstances.genderPieChartInstance);
        let ctx = document.getElementById("genderPieChart").getContext("2d");

        chartInstances.genderPieChartInstance = new Chart(ctx, {
            type: "pie",
            data: { labels: ["남성", "여성"], datasets: [{ data: [60, 40], backgroundColor: ["#36A2EB", "#FF6384"] }] },
            options: { responsive: true }
        });
    }

    function loadAgeChart() {
        removeExistingChart(chartInstances.agePieChartInstance);
        let ctx = document.getElementById("ageBarChart").getContext("2d");

        chartInstances.agePieChartInstance = new Chart(ctx, {
            type: "bar", // ✅ 바 그래프로 변경
            data: {
                labels: ["10대", "20대", "30대", "40대", "50대", "60대 이상"],
                datasets: [{
                    label: "연령별 악성민원 수",
                    data: [20, 50, 40, 30, 25, 15], // ✅ 실제 데이터 입력
                    backgroundColor: "rgba(54, 162, 235, 0.6)",
                    borderColor: "rgba(54, 162, 235, 1)",
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    function loadCharts(target) {
        showSection(target);

        if (target === "daily-complaints") loadDailyChart();
        else if (target === "monthly-complaints") loadMonthlyChart();
        else if (target === "yearly-complaints") loadYearlyChart();
        else if (target === "malicious-type") loadMaliciousTypeChart();
        else if (target === "malicious-gender") {
            loadGenderChart();
            loadAgeChart();
        }
    }

    menuItems.forEach(item => {
        item.addEventListener("click", function () {
            menuItems.forEach(menu => menu.classList.remove("active"));
            this.classList.add("active");

            const target = this.getAttribute("data-target");
            loadCharts(target);
        });
    });

    // ✅ 기본 페이지 로드 (일별 민원 통계)
    loadCharts("daily-complaints");
});
