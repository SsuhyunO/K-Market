document.addEventListener('DOMContentLoaded', function () {
    // 1. 상품 상세 화면(view.html) 스티키 탭 스무스 스크롤 기능 바인딩
    initViewTabsScroll();

    // 2. 상품 상세 화면(view.html) 수량 조절 및 총 금액 계산 기능 바인딩
    initQuantityControl();

    // 3. 상품 상세 화면(view.html) 리뷰 페이지네이션 기능 바인딩
    initReviewPagination();
});


/* ─────────────────────────────────────────────
   상품 상세(view.html) 탭 스무스 스크롤 및 Scroll Spy
───────────────────────────────────────────── */
function initViewTabsScroll() {
    const viewTabs = document.querySelectorAll('.view-tabs a');
    if (viewTabs.length === 0) return;

    // 1. 클릭 시 부드럽게 스크롤 이동
    viewTabs.forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            const targetSection = document.querySelector(targetId);

            if (!targetSection) return;

            // 클릭할 때 일단 즉각적으로 active 변경
            viewTabs.forEach(a => a.classList.remove('active'));
            this.classList.add('active');

            // 탭 메뉴 전체 컨테이너와 높이 가져오기
            const tabsEl = document.querySelector('.view-tabs');
            const tabsTop = parseInt(getComputedStyle(tabsEl).top, 10) || 0;
            const tabsHeight = tabsEl.offsetHeight;

            // 정확한 목표 스크롤 위치 계산
            const offsetTop = targetSection.getBoundingClientRect().top + window.scrollY - tabsTop - tabsHeight - 16;

            window.scrollTo({
                top: offsetTop,
                behavior: 'smooth'
            });
        });
    });

    // 2. 스크롤 감지 - IntersectionObserver를 활용하여 현재 섹션 불 켜기
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // 진입한 섹션의 id (예: 'detail', 'notice', 'review')
                const id = entry.target.id;

                // 해당 id를 href('#id')로 가진 탭만 active 클래스 토글
                viewTabs.forEach(tab => {
                    tab.classList.toggle('active', tab.getAttribute('href') === `#${id}`);
                });
            }
        });
    }, { rootMargin: '-30% 0px -60% 0px' }); // 화면의 중앙 부분에 닿을 때 감지

    // 각 탭이 가리키는 섹션(article)들을 찾아서 관찰(observe) 대상에 추가
    viewTabs.forEach(tab => {
        const targetId = tab.getAttribute('href');
        const targetSection = document.querySelector(targetId);
        if (targetSection) {
            observer.observe(targetSection);
        }
    });
}


/* ─────────────────────────────────────────────
   상품 상세(view.html) 수량 조절 및 총 금액 계산
───────────────────────────────────────────── */
function initQuantityControl() {
    const countControl = document.querySelector('.count-control');
    // 해당 요소가 없으면(다른 페이지면) 함수 종료
    if (!countControl) return;

    // 💡 실제로는 서버에서 전달받은 단가를 넣어야 함
    const UNIT_PRICE = 62300;

    const minusBtn = countControl.querySelector('button:first-child');
    const plusBtn  = countControl.querySelector('button:last-child');
    const countInput = countControl.querySelector('input');
    const totalEl = document.querySelector('.total-price-wrap .total');

    function renderTotal(count) {
        const total = UNIT_PRICE * count;
        if (totalEl) {
            totalEl.innerHTML = total.toLocaleString() + '<span>원</span>';
        }
    }

    function getCount() {
        return parseInt(countInput.value, 10) || 1;
    }

    if (minusBtn && plusBtn && countInput) {
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
    }
}


/* ─────────────────────────────────────────────
   상품 상세(view.html) 리뷰 페이지네이션 기능 (더미)
───────────────────────────────────────────── */
function initReviewPagination() {
    const pagination = document.querySelector('.review-pagination');
    if (!pagination) return;

    const allLinks = pagination.querySelectorAll('a');

    allLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();

            const numberLinks = Array.from(pagination.querySelectorAll('a:not(.page-nav)'));

            if (this.classList.contains('page-nav')) {
                const currentIndex = numberLinks.findIndex(l => l.classList.contains('active'));
                const isPrev = this.textContent.includes('이전');
                let nextIndex = isPrev ? currentIndex - 1 : currentIndex + 1;
                nextIndex = Math.max(0, Math.min(nextIndex, numberLinks.length - 1));

                numberLinks.forEach(l => l.classList.remove('active'));
                numberLinks[nextIndex].classList.add('active');
            } else {
                numberLinks.forEach(l => l.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });
}