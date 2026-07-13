/* =============================================
   KMarket - common.js (공통 기능)
   ※ Thymeleaf 전환 후: header/aside/footer는 서버에서
     th:replace로 이미 합쳐진 채로 내려오므로,
     fetch로 HTML을 조립하는 includeHTML() 로직은
     더 이상 필요 없어 삭제했습니다.
   ============================================= */

// ── 페이지 로드 시 공통 기능 초기화 ──
document.addEventListener("DOMContentLoaded", function () {

    // 1. 배너 bxSlider 실행
    if ($('.bxslider').length > 0) {
        $('.bxslider').bxSlider({
            auto: true,
            speed: 500,
            pause: 3500,
            controls: true,
            pager: true
        });
    }

    // 2. GNB 탭 스크롤 기능 작동 (header가 이미 렌더링되어 있으므로 바로 실행 가능)
    initNavTabs();

    // 3. 카테고리 마우스 호버 기능 작동 (aside가 이미 렌더링되어 있으므로 바로 실행 가능)
    initCatMenu();

    // 4. 상품 카드 클릭 이벤트 바인딩
    initProductCards();

    // 5. 외부 페이지에서 메뉴를 클릭해 넘어왔을 때 스크롤 위치 보정
    handleHashScroll();
});


/* ── 기능 함수 정의들 ── */

// 외부 페이지에서 해시(#) 링크로 넘어왔을 때 헤더에 가려지지 않게 스크롤 보정
function handleHashScroll() {
    if (window.location.hash) {
        const target = document.querySelector(window.location.hash);
        if (target) {
            // 브라우저의 기본 점프가 끝난 직후에 부드럽게 위치를 조정합니다.
            setTimeout(() => {
                const offset = target.getBoundingClientRect().top + window.scrollY - 250;
                window.scrollTo({ top: offset, behavior: 'smooth' });
            }, 100);
        }
    }
}

// 메인 섹션 GNB 탭 이동 및 스크롤 감지
function initNavTabs() {
    const tabs = document.querySelectorAll('.header-nav a');
    if (tabs.length === 0) return; // header-nav가 없는 페이지는 그냥 종료

    const sections = {
        'hit':       document.getElementById('section-hit'),
        'recommend': document.getElementById('section-recommend'),
        'new':       document.getElementById('section-new'),
        'discount':  document.getElementById('section-discount'),
    };

    // 메인 페이지가 아니라 섹션 자체가 없는 페이지라면 스크롤 기능은 생략
    const hasSections = Object.values(sections).some(s => s !== null);
    if (!hasSections) return;

    // ✨ 추가된 부분: 처음 메인 화면에 들어왔을 때(해시가 없을 때) 기본으로 '히트상품' 탭 활성화
    if (!window.location.hash) {
        const defaultTab = Array.from(tabs).find(t => t.dataset.section === 'hit');
        if (defaultTab) defaultTab.classList.add('active');
    }

    tabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            const key = tab.dataset.section;
            const target = sections[key];
            if (target) {
                e.preventDefault();
                const offset = target.getBoundingClientRect().top + window.scrollY - 250;
                window.scrollTo({ top: offset, behavior: 'smooth' });
            }
            // target이 없으면(다른 페이지) 기본 링크 동작으로 index 페이지의 해당 섹션으로 이동
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
        });
    });

    // 스크롤 감지 - 현재 보고 있는 섹션 메뉴 불 켜기
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const id = entry.target.id.replace('section-', '');
                tabs.forEach(t => t.classList.toggle('active', t.dataset.section === id));
            }
        });
    }, { rootMargin: '-30% 0px -60% 0px' });

    Object.values(sections).forEach(s => s && observer.observe(s));
}

// 카테고리 서브메뉴 호버 이벤트
function initCatMenu() {
    if (document.querySelector('.aside-category details')) {
        return;
    }

    document.querySelectorAll('.aside-category li').forEach(li => {
        li.addEventListener('mouseenter', () => {
            const sub = li.querySelector('.cat-sub');
            if (sub) sub.style.display = 'block';
        });
        li.addEventListener('mouseleave', () => {
            const sub = li.querySelector('.cat-sub');
            if (sub) sub.style.display = 'none';
        });
    });
}

// 상품 카드 클릭 이동
function initProductCards() {
    document.querySelectorAll('.product-card').forEach(card => {
        card.addEventListener('click', (e) => {
            // th:href가 이미 정상적인 링크이므로 기본 동작(페이지 이동)을 막지 않음
            const id = card.dataset.id || '';
            console.log('상품 페이지 이동 번호:', id);
        });
    });
}
