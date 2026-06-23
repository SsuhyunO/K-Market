/* =============================================
   KMarket - company.js
   회사소개 메인 페이지 전용 스크립트
   ============================================= */

document.addEventListener('DOMContentLoaded', function () {
    initSloganSlider();
});

/* ── ② 슬라이드 배너 (좌우 화살표 + dots) ── */
function initSloganSlider() {
    const track = document.querySelector('.slogan-slider-track');
    if (!track) return;

    const slides = Array.from(track.children);
    const dots = document.querySelectorAll('.slogan-dots .dot');
    const prevBtn = document.querySelector('.slider-arrow.prev');
    const nextBtn = document.querySelector('.slider-arrow.next');

    const slidesPerView = 2; // 와이어프레임 기준 한 화면에 2개씩
    let currentPage = 0;
    const totalPages = Math.ceil(slides.length / slidesPerView);

    function render() {
// 현재 페이지에 해당하는 슬라이드만 표시
        slides.forEach((slide, i) => {
            const page = Math.floor(i / slidesPerView);
            slide.style.display = (page === currentPage) ? '' : 'none';
        });
        dots.forEach((dot, i) => dot.classList.toggle('active', i === currentPage));
    }

    prevBtn?.addEventListener('click', () => {
        currentPage = (currentPage - 1 + totalPages) % totalPages;
        render();
    });

    nextBtn?.addEventListener('click', () => {
        currentPage = (currentPage + 1) % totalPages;
        render();
    });

    dots.forEach((dot, i) => {
        dot.addEventListener('click', () => {
            currentPage = i;
            render();
        });
    });

    render();
}