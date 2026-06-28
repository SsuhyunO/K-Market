// 스티키 탭 메뉴 클릭 시 부드러운 스크롤 이동
    document.querySelectorAll('.view-tabs a').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const targetId = this.getAttribute('href');
        const targetSection = document.querySelector(targetId);

        document.querySelectorAll('.view-tabs a').forEach(a => a.classList.remove('active'));
        this.classList.add('active');

        const tabsEl = document.querySelector('.view-tabs');
        const tabsTop = parseInt(getComputedStyle(tabsEl).top, 10) || 0; // 224
        const tabsHeight = tabsEl.offsetHeight;
        const offsetTop = targetSection.getBoundingClientRect().top + window.scrollY - tabsTop - tabsHeight - 16;
        window.scrollTo({
            top: offsetTop,
            behavior: 'smooth'
        });
    });
});

    // 수량 조절에 따른 총 상품금액 실시간 계산 (서버 왕복 없이 클라이언트에서 처리)
    (function () {
    const UNIT_PRICE = 62300; // 상품 단가 (실제로는 서버에서 th:text로 내려줄 값)
    const minusBtn = document.querySelector('.count-control button:first-child');
    const plusBtn  = document.querySelector('.count-control button:last-child');
    const countInput = document.querySelector('.count-control input');
    const totalEl = document.querySelector('.total-price-wrap .total');

    function renderTotal(count) {
    const total = UNIT_PRICE * count;
    // "62,300" + <span>원</span> 구조 유지
    totalEl.innerHTML = total.toLocaleString() + '<span>원</span>';
}

    function getCount() {
    return parseInt(countInput.value, 10) || 1;
}

    minusBtn.addEventListener('click', () => {
    const next = Math.max(1, getCount() - 1);
    countInput.value = next;
    renderTotal(next);
});

    plusBtn.addEventListener('click', () => {
    const next = getCount() + 1;
    countInput.value = next;
    renderTotal(next);
});
})();

    // 리뷰 페이지네이션 (더미 클릭 동작만 — 실제 데이터 교체는 서버 연동 시 처리)
    (function () {
    const pageLinks = document.querySelectorAll('.review-pagination a:not(.page-nav)');
    pageLinks.forEach(link => {
    link.addEventListener('click', function (e) {
    e.preventDefault();
    pageLinks.forEach(l => l.classList.remove('active'));
    this.classList.add('active');
    // TODO: 서버 연동 시 이 페이지 번호로 리뷰 목록 재요청
});
});
})();
