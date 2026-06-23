document.addEventListener('DOMContentLoaded', function(e) {
    /* ── Aside 서브메뉴 — hover 유지 (CSS 보완) ── */
    // CSS :hover만으로도 동작하지만, 터치 디바이스 대비
    document.querySelectorAll('.nav-item').forEach(item => {
        item.addEventListener('mouseenter', () => {
            document.querySelectorAll('.nav-item').forEach(i => {
                if (i !== item) i.classList.remove('open');
            });
        });
    });
});