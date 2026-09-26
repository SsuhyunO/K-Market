const modalStack = [];

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.remove('active');
    if (modalStack.length > 0) {
        const previousModal = document.getElementById(modalStack.pop());
        if (previousModal) previousModal.classList.add('active');
    }
}

document.querySelectorAll('[data-close]').forEach((button) => {
    button.addEventListener('click', () => closeModal(button.dataset.close));
});

document.querySelectorAll('.modal-overlay').forEach((overlay) => {
    overlay.addEventListener('click', (event) => {
        if (event.target === overlay) closeModal(overlay.id);
    });
});

const today = new Date();
today.setHours(0, 0, 0, 0);

function parseDate(value) {
    if (!value) return null;
    const date = new Date(String(value).substring(0, 10) + 'T00:00:00');
    return Number.isNaN(date.getTime()) ? null : date;
}

function formatDate(value) {
    const date = parseDate(value);
    return date ? `${date.getMonth() + 1}/${date.getDate()}` : '-';
}

function getStatus(coupon) {
    const expireDate = parseDate(coupon.expireDate);
    if (Number(coupon.status) === 1) return '사용완료';
    if (Number(coupon.status) === 2 || (expireDate && expireDate < today)) return '기간만료';
    if (Number(coupon.status) === 3) return '사용중단';
    return '사용가능';
}

function getBenefit(coupon) {
    if (coupon.benefit === 'DELIVERY_FREE') return '배송비 무료';
    const benefit = Number(coupon.benefit);
    if (!Number.isFinite(benefit)) return coupon.benefit || '-';
    return benefit <= 100 ? `${benefit}% 할인` : `${benefit.toLocaleString('ko-KR')}원 할인`;
}

function getCondition(coupon) {
    const issuer = coupon.companyName || 'K마켓';
    const scope = coupon.sellerUid ? `${issuer} 상품에 적용` : '전체 주문에 적용';
    return coupon.notice ? `${scope} · ${coupon.notice}` : scope;
}

const couponData = (Array.isArray(SERVER_COUPONS) ? SERVER_COUPONS : []).map((coupon) => ({
    ...coupon,
    issueDate: String(coupon.createdAt || '').substring(0, 10),
    displayStatus: getStatus(coupon)
}));

let filteredData = couponData.slice();

function renderCouponRows() {
    const tbody = document.getElementById('couponListBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    if (filteredData.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="order-empty">조회된 쿠폰내역이 없습니다.</td></tr>';
        return;
    }

    filteredData.forEach((coupon) => {
        const row = document.createElement('tr');
        const statusClass = coupon.displayStatus === '사용가능'
            ? 'coupon-status-active'
            : 'coupon-status-expired';
        row.innerHTML = `
            <td class="coupon-name"></td>
            <td class="coupon-amount"></td>
            <td class="coupon-condition"></td>
            <td class="${statusClass}"></td>
            <td class="coupon-period"></td>
        `;
        row.children[0].textContent = coupon.couponName || '-';
        row.children[1].textContent = getBenefit(coupon);
        row.children[2].textContent = getCondition(coupon);
        row.children[3].textContent = coupon.displayStatus;
        row.children[4].textContent = `${formatDate(coupon.startDate)} ~ ${formatDate(coupon.expireDate)}`;
        tbody.appendChild(row);
    });
}

document.querySelectorAll('input[name="periodType"]').forEach((radio) => {
    radio.addEventListener('change', () => {
        document.getElementById('periodMonthSelect').value = '0';
        document.getElementById('periodStartDate').value = '';
        document.getElementById('periodEndDate').value = '';
    });
});

document.getElementById('periodMonthSelect')?.addEventListener('change', () => {
    document.querySelectorAll('input[name="periodType"]').forEach((radio) => radio.checked = false);
    document.getElementById('periodStartDate').value = '';
    document.getElementById('periodEndDate').value = '';
});

document.querySelectorAll('.period-date-input').forEach((input) => {
    input.addEventListener('change', () => {
        document.querySelectorAll('input[name="periodType"]').forEach((radio) => radio.checked = false);
        document.getElementById('periodMonthSelect').value = '0';
    });
});

document.getElementById('periodSearchBtn')?.addEventListener('click', () => {
    const checkedPeriod = document.querySelector('input[name="periodType"]:checked');
    const monthsAgo = Number(document.getElementById('periodMonthSelect').value);
    const startValue = document.getElementById('periodStartDate').value;
    const endValue = document.getElementById('periodEndDate').value;
    let rangeStart;
    let rangeEnd = new Date(today);

    if (startValue || endValue) {
        if (!startValue || !endValue) {
            alert('시작일과 종료일을 모두 선택해주세요.');
            return;
        }
        rangeStart = parseDate(startValue);
        rangeEnd = parseDate(endValue);
        if (rangeStart > rangeEnd) {
            alert('시작일이 종료일보다 늦을 수 없습니다.');
            return;
        }
        const oneYearLater = new Date(rangeStart);
        oneYearLater.setFullYear(oneYearLater.getFullYear() + 1);
        if (rangeEnd > oneYearLater) {
            alert('조회 기간은 최대 1년까지 가능합니다.');
            return;
        }
    } else if (checkedPeriod) {
        rangeStart = new Date(today);
        rangeStart.setDate(rangeStart.getDate() - Number(checkedPeriod.dataset.days));
    } else {
        rangeStart = new Date(today);
        rangeStart.setMonth(rangeStart.getMonth() - (monthsAgo + 1));
    }

    filteredData = couponData.filter((coupon) => {
        const issueDate = parseDate(coupon.issueDate);
        return issueDate && issueDate >= rangeStart && issueDate <= rangeEnd;
    });
    renderCouponRows();
});

renderCouponRows();
