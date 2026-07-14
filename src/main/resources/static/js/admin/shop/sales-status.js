// 관리자 상점관리 > 매출현황
document.addEventListener('DOMContentLoaded', function () {
    const periodSelect = document.getElementById('periodSelect');
    if (!periodSelect) return;

    periodSelect.addEventListener('change', function () {
        const url = new URL(window.location.href);
        url.searchParams.set('period', this.value);
        url.searchParams.set('page', '1'); // 기간 바뀌면 1페이지로
        window.location.href = url.toString();
    });
});