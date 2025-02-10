document.addEventListener("DOMContentLoaded", () => {
    let currentIndex = 0;
    const slider = document.querySelector(".slider");
    const slides = document.querySelectorAll(".slide");
    const totalSlides = slides.length;
    const buttons = document.querySelectorAll(".control-button");
    let interval;

    // 첫 번째 슬라이드를 복제해서 마지막에 추가 (무한 슬라이드 효과)
    const firstClone = slides[0].cloneNode(true);
    slider.appendChild(firstClone);

    // 부모 컨테이너에 overflow 설정
    const sliderContainer = document.getElementById("home");
    sliderContainer.style.overflow = "hidden";

    // 슬라이드 크기 자동 조정
    slider.style.display = "flex";
    slider.style.width = `${(totalSlides + 1) * 100}vw`; // 복제된 슬라이드 포함하여 전체 크기 설정

    function moveSlide(index) {
        currentIndex = index;
        slider.style.transition = "transform 0.5s ease-in-out";
        slider.style.transform = `translateX(-${currentIndex * 100}vw)`;

        // 마지막 슬라이드에 도달하면 transition 없이 첫 번째 슬라이드로 이동
        if (currentIndex === totalSlides) {
            setTimeout(() => {
                slider.style.transition = "none";
                slider.style.transform = "translateX(0)";
                currentIndex = 0;
                updateActiveButton();
            }, 500);
        } else {
            updateActiveButton();
        }
    }

    function resetInterval() {
        clearInterval(interval);
        interval = setInterval(() => moveSlide(currentIndex + 1), 4000);
    }

    function goToSlide(index) {
        moveSlide(index);
        resetInterval();
    }

    function updateActiveButton() {
        buttons.forEach((btn, i) => {
            btn.classList.remove("active");
        });
        buttons[currentIndex % totalSlides].classList.add("active"); // 첫 번째 버튼 활성화
    }

    // 버튼 이벤트 추가
    buttons.forEach((button, index) => {
        button.addEventListener("click", () => goToSlide(index));
    });

    // 자동 슬라이드 시작
    interval = setInterval(() => moveSlide(currentIndex + 1), 4000);

    // 웹 시작 시 첫 번째 버튼 활성화
    updateActiveButton();
});
