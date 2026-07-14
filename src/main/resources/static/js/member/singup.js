// URL에서 type 파라미터 읽기
const params = new URLSearchParams(window.location.search);
const memberType = params.get('type'); // 'seller' 또는 null(일반)

// 페이지 로드 시 약관 내용 전환 및 전체동의 기능 초기화
window.addEventListener('DOMContentLoaded', function () {
    if (memberType === 'seller') {
        // 판매자: seller용 textarea 보이고 normal용 숨기기
        document.getElementById('terms1-normal').style.display = 'none';
        document.getElementById('terms1-seller').style.display = 'block';

        document.getElementById('terms2-normal').style.display = 'none';
        document.getElementById('terms2-seller').style.display = 'block';

        document.getElementById('terms3-normal').style.display = 'none';
        document.getElementById('terms3-seller').style.display = 'block';

        // 판매자는 위치정보 약관 섹션 자체를 숨김
        document.getElementById('terms4-wrap').style.display = 'none';
    }
    // 일반 회원은 기본값(normal)이 이미 보이므로 추가 처리 불필요

    initAgreeAll();
});

function initAgreeAll() {
    const agreeAll = document.getElementById('agreeAll');
    // 판매자는 terms4-wrap이 숨겨져 있으므로 agree4는 전체동의 대상에서 제외
    const itemCheckboxes = memberType === 'seller'
        ? [document.getElementById('agree1'), document.getElementById('agree2'), document.getElementById('agree3')]
        : Array.from(document.querySelectorAll('.agree-item'));

    // 전체 동의 체크박스 클릭 시 -> 하위 항목 전체 반영
    agreeAll.addEventListener('change', function () {
        itemCheckboxes.forEach(cb => {
            cb.checked = agreeAll.checked;
        });
    });

    // 개별 항목 체크 시 -> 전체 동의 상태 자동 동기화
    itemCheckboxes.forEach(cb => {
        cb.addEventListener('change', function () {
            const allChecked = itemCheckboxes.every(item => item.checked);
            agreeAll.checked = allChecked;
        });
    });
}

function handleAgree() {
    const required = [
        document.getElementById('agree1'),
        document.getElementById('agree2'),
        document.getElementById('agree3')
    ];

    const allChecked = required.every(cb => cb.checked);
    if (!allChecked) {
        alert('필수 약관에 모두 동의해 주세요.');
        return;
    }

    if (memberType === 'seller') {
        window.location.href = 'registerSeller';
    } else {
        window.location.href = 'register';
    }
}