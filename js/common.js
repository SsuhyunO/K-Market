/* =============================================
   KMarket - common.js (HTML 조립 및 공통 기능)
   ============================================= */

// HTML 조립 공통 함수 (유틸리티)
function includeHTML(elementId, url, callback) {
  const target = document.getElementById(elementId);
  if (!target) return;

  fetch(url)
    .then(res => {
      if (!res.ok) throw new Error(`${url} 파일을 불러오지 못했습니다.`);
      return res.text();
    })
    .then(data => {
      target.innerHTML = data; // 빈 선반에 파일 내용 이식 [cite: 598]
      if (callback) callback(); // 조립 완료 후 기능 실행(콜백)
    })
    .catch(err => console.error(err));
}

// ── 페이지 로드 시 HTML 조립 시작 ──
document.addEventListener("DOMContentLoaded", function () {
  
  // 1. 배너 bxSlider 실행 (본문에 있으므로 즉시 실행 가능) [cite: 464]
  if ($('.bxslider').length > 0) {
    $('.bxslider').bxSlider({
      auto: true,
      speed: 500,
      pause: 3500,
      controls: true,
      pager: true
    });
  }

  // 2. 헤더 조립 완료 후 GNB 탭 스크롤 기능 작동
  includeHTML('header', 'fragments/header.html', function() {
    // 메인 페이지에만 있는 #section-hit가 존재할 때만 스크롤 기능을 켭니다.
    if (document.getElementById('section-hit')) {
      initNavTabs();
    }
  });

  // 3. 사이드바 조립 완료 후 카테고리 마우스 호버 기능 작동
  includeHTML('aside', 'fragments/aside.html', function() {
    initCatMenu();
  });

  // 4. 푸터 조립
  includeHTML('footer', 'fragments/footer.html');

  // 5. 상품 카드 클릭 이벤트 바인딩
  initProductCards();
});


/* ── 기능 함수 정의들 ── */

// 메인 섹션 GNB 탭 이동 및 스크롤 감지
function initNavTabs() {
  const tabs = document.querySelectorAll('.header-nav a');
  const sections = {
    'hit':      document.getElementById('section-hit'),
    'recommend': document.getElementById('section-recommend'),
    'new':      document.getElementById('section-new'),
    'popular':  document.getElementById('section-popular'),
    'discount': document.getElementById('section-discount'),
  };

  tabs.forEach(tab => {
    tab.addEventListener('click', (e) => {
      const key = tab.dataset.section;
      const target = sections[key];
      if (target) {
        e.preventDefault();
        const offset = target.getBoundingClientRect().top + window.scrollY - 250;
        window.scrollTo({ top: offset, behavior: 'smooth' });
      }
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
    card.addEventListener('click', () => {
      const id = card.dataset.id || '';
      console.log('상품 페이지 이동 번호:', id);
    });
  });
}