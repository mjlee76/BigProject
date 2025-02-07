document.addEventListener("DOMContentLoaded", () => {
    // Home Section 자동 슬라이드
    let currentIndex = 0;
    const slides = document.querySelector(".slider");
    const totalSlides = document.querySelectorAll(".slide").length;

    function moveSlide() {
        currentIndex = (currentIndex + 1) % totalSlides;
        slides.style.transform = `translateX(-${currentIndex * 100}vw)`;
    }

    setInterval(moveSlide, 4000); // 3초마다 슬라이드 이동
});
