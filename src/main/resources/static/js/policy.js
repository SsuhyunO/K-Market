/* =============================================
   KMarket - policy.js
   약관 페이지 전용 스크립트 (탭 표시 + 인쇄)
   ============================================= */

// 인쇄하기 버튼 (4번 요구사항 - 인쇄 창 띄우기)
function printPolicy() {
    window.print();
}

// 페이지 로드 시 인쇄 버튼 이벤트 바인딩
document.addEventListener('DOMContentLoaded', function () {
    const printBtn = document.querySelector('.btn-print');
    if (printBtn) {
        printBtn.addEventListener('click', printPolicy);
    }
});