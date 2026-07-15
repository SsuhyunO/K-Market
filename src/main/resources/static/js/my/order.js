let currentPage = 1;
let lastPageData = null;
let activeItem = null;
let periodSearchEnabled = false;

document.addEventListener('DOMContentLoaded', () => {
    initModals();
    initPeriodControls();
    initActions();
    loadOrders(1).catch(errorHandler);
});

function initModals() {
    document.querySelectorAll('[data-close]').forEach(button => {
        button.addEventListener('click', () => closeModal(button.dataset.close));
    });

    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', event => {
            if (event.target === overlay) closeModal(overlay.id);
        });
    });
}

function initPeriodControls() {
    document.getElementById('periodSearchBtn')?.addEventListener('click', () => {
        periodSearchEnabled = true;
        loadOrders(1).catch(errorHandler);
    });

    document.getElementById('pagePrev')?.addEventListener('click', event => {
        event.preventDefault();
        if (currentPage > 1) loadOrders(currentPage - 1).catch(errorHandler);
    });

    document.getElementById('pageNext')?.addEventListener('click', event => {
        event.preventDefault();
        if (lastPageData && currentPage < lastPageData.totalPage) {
            loadOrders(currentPage + 1).catch(errorHandler);
        }
    });
}

function initActions() {
    document.getElementById('orderListBody')?.addEventListener('click', event => {
        const row = event.target.closest('[data-order-item-no]');
        if (!row) return;

        activeItem = findCurrentItem(Number(row.dataset.orderItemNo));
        if (!activeItem) return;

        if (event.target.closest('.js-order-detail')) {
            event.preventDefault();
            renderDetailModal(activeItem);
            openModal('orderDetailModal');
        } else if (event.target.closest('.js-confirm-purchase')) {
            event.preventDefault();
            openModal('confirmPurchaseModal');
        } else if (event.target.closest('.js-return-request')) {
            event.preventDefault();
            renderClaimModal('return', activeItem);
            openModal('returnRequestModal');
        } else if (event.target.closest('.js-exchange-request')) {
            event.preventDefault();
            renderClaimModal('exchange', activeItem);
            openModal('exchangeRequestModal');
        }
    });

    document.querySelector('.js-confirm-ok')?.addEventListener('click', async () => {
        if (!activeItem) return;
        await postJson(`${contextPath()}my/order/api/${activeItem.orderItemNo}/confirm`, {});
        closeModal('confirmPurchaseModal');
        await loadOrders(currentPage);
    });

    document.getElementById('returnSubmitBtn')?.addEventListener('click', () => {
        submitClaim('RETURN', 'returnType', 'returnReason', 'returnRequestModal').catch(errorHandler);
    });
    document.getElementById('exchangeSubmitBtn')?.addEventListener('click', () => {
        submitClaim('EXCHANGE', 'exchangeType', 'exchangeReason', 'exchangeRequestModal').catch(errorHandler);
    });
    document.getElementById('orderCancelBtn')?.addEventListener('click', () => {
        cancelOrder().catch(errorHandler);
    });
}

async function loadOrders(page) {
    currentPage = page;
    const params = new URLSearchParams({ page, size: 10 });
    const range = getDateRange();
    if (range.startDate) params.set('startDate', range.startDate);
    if (range.endDate) params.set('endDate', range.endDate);

    lastPageData = await fetchJson(`${contextPath()}my/order/api/list?${params.toString()}`);
    renderOrderRows(lastPageData.list || []);
    renderPagination(lastPageData);
}

function getDateRange() {
    if (!periodSearchEnabled) return { startDate: null, endDate: null };

    const startInput = document.getElementById('periodStartDate')?.value;
    const endInput = document.getElementById('periodEndDate')?.value;
    if (startInput || endInput) {
        return { startDate: startInput || null, endDate: endInput || null };
    }

    const checkedPeriod = document.querySelector('input[name="periodType"]:checked');
    const days = checkedPeriod ? Number(checkedPeriod.dataset.days || 30) : 30;
    const end = new Date();
    const start = new Date();
    start.setDate(end.getDate() - days);
    return { startDate: formatDate(start), endDate: formatDate(end) };
}

function renderOrderRows(items) {
    const tbody = document.getElementById('orderListBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="order-empty">조회된 주문내역이 없습니다.</td></tr>';
        return;
    }

    items.forEach(item => {
        const tr = document.createElement('tr');
        tr.className = 'js-order-row';
        tr.dataset.orderItemNo = item.orderItemNo;
        tr.innerHTML = `
            <td class="order-date">${escapeHtml(formatDateText(item.createdAt))}</td>
            <td class="order-product">
                <div class="product-inner">
                    <div class="product-img">${renderThumb(item.thumb1FileId)}</div>
                    <div class="product-info">
                        <p class="order-no"><a href="#" class="js-order-detail">주문번호 : ${escapeHtml(item.orderNo)}</a></p>
                        <p class="company-name">${escapeHtml(item.sellerName || item.sellerUid || '-')}</p>
                        <p class="product-name">${escapeHtml(item.productName || '-')}</p>
                        <p class="product-qty">수량 : ${escapeHtml(item.quantity || 0)}개</p>
                        <p class="product-price">${formatNumber(item.total)}원</p>
                        ${item.trackingNo ? `<p class="product-qty">${escapeHtml(item.courierName || '')} ${escapeHtml(item.trackingNo)}</p>` : ''}
                    </div>
                </div>
            </td>
            <td class="order-status">${renderStatus(item.itemStatus)}</td>
            <td class="order-action"><div class="action-btn-group">${actionButtons(item)}</div></td>
        `;
        tbody.appendChild(tr);
    });
}

function actionButtons(item) {
    const buttons = [];
    if (item.itemStatus === 'DELIVERED') {
        buttons.push('<a href="#" class="btn-blue js-confirm-purchase">구매확정</a>');
        buttons.push('<a href="#" class="btn-white js-return-request">반품요청</a>');
        buttons.push('<a href="#" class="btn-white js-exchange-request">교환요청</a>');
    }
    return buttons.length ? buttons.join('') : '-';
}

function renderPagination(pageData) {
    const pageNumbersEl = document.getElementById('pageNumbers');
    if (!pageNumbersEl) return;

    pageNumbersEl.innerHTML = '';
    const totalPage = Math.max(1, pageData.totalPage || 1);
    const startPage = Math.max(1, pageData.startPage || 1);
    const lastPage = Math.min(totalPage, pageData.lastPage || totalPage);

    for (let page = startPage; page <= lastPage; page++) {
        const link = document.createElement('a');
        link.href = '#';
        link.className = 'page-num' + (page === pageData.page ? ' active' : '');
        link.textContent = page;
        link.addEventListener('click', event => {
            event.preventDefault();
            loadOrders(page).catch(errorHandler);
        });
        pageNumbersEl.appendChild(link);
    }

    document.getElementById('pagePrev')?.classList.toggle('disabled', pageData.page <= 1);
    document.getElementById('pageNext')?.classList.toggle('disabled', pageData.page >= totalPage);
}

function renderDetailModal(item) {
    setText('detailDate', formatDateText(item.createdAt));
    setText('detailDate2', formatDateText(item.createdAt));
    setText('detailOrderNo', `주문번호 : ${item.orderNo}`);
    setText('detailCompany', item.sellerName || item.sellerUid || '-');
    setText('detailProduct', item.productName || '-');
    setText('detailQty', `수량 : ${item.quantity || 0}개`);
    setText('detailPrice', `${formatNumber(item.total)}원`);
    setText('detailPayBase', `${formatNumber(item.price)}원`);
    setText('detailPayTotal', `${formatNumber(item.total)}원`);
    setText('detailStatus', renderStatus(item.itemStatus));
    const cancelButton = document.getElementById('orderCancelBtn');
    if (cancelButton) {
        cancelButton.hidden = !['PAID', 'READY', 'SHIPPING'].includes(item.itemStatus);
    }
}

function renderClaimModal(type, item) {
    const prefix = type === 'return' ? 'return' : 'exchange';
    setText(`${prefix}Date`, formatDateText(item.createdAt));
    setText(`${prefix}OrderNo`, `주문번호 : ${item.orderNo}`);
    setText(`${prefix}Company`, item.sellerName || item.sellerUid || '-');
    setText(`${prefix}Product`, item.productName || '-');
    setText(`${prefix}Qty`, `수량 : ${item.quantity || 0}개`);
    setText(`${prefix}Price`, `${formatNumber(item.total)}원`);
}

async function submitClaim(claimType, radioName, reasonId, modalId) {
    if (!activeItem) return;
    const reasonType = document.querySelector(`input[name="${radioName}"]:checked`)?.value;
    const claimContent = document.getElementById(reasonId)?.value.trim();
    if (!reasonType || !claimContent) {
        alert('유형과 사유를 입력해주세요.');
        return;
    }

    await postJson(`${contextPath()}my/order/api/${activeItem.orderItemNo}/claims`, {
        claimType,
        reasonType,
        claimContent
    });
    closeModal(modalId);
    document.getElementById(reasonId).value = '';
    await loadOrders(currentPage);
}

async function cancelOrder() {
    if (!activeItem) return;
    if (!confirm('해당 주문을 취소하시겠습니까? 배송중인 배송건은 배송취소 상태로 변경됩니다.')) {
        return;
    }
    await postJson(`${contextPath()}my/order/api/orders/${activeItem.orderNo}/cancel`, {});
    closeModal('orderDetailModal');
    await loadOrders(currentPage);
}

async function fetchJson(url) {
    const response = await fetch(url, { headers: { Accept: 'application/json' } });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

async function postJson(url, body) {
    const response = await fetch(url, {
        method: 'POST',
        headers: { Accept: 'application/json', 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

function renderThumb(fileId) {
    return fileId ? `<img src="${contextPath()}files/${encodeURIComponent(fileId)}" alt="">` : '상품이미지';
}

function renderStatus(status) {
    return {
        PAID: '결제완료',
        READY: '배송준비',
        SHIPPING: '배송중',
        PARTIAL_SHIPPING: '부분배송중',
        DELIVERED: '배송완료',
        CONFIRMED: '구매확정',
        RETURN_REQUESTED: '반품요청',
        RETURN_SHIPPING: '반품 반송중',
        EXCHANGE_REQUESTED: '교환요청',
        EXCHANGE_SHIPPING: '교환 반송중',
        EXCHANGE_WAITING: '상품대기중',
        CANCEL_REQUESTED: '취소요청',
        RETURNED: '반품완료',
        EXCHANGED: '교환완료',
        CANCELED: '주문취소'
    }[status] || status || '-';
}

function openModal(modalId) {
    document.getElementById(modalId)?.classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId)?.classList.remove('active');
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (target) target.textContent = value;
}

function findCurrentItem(orderItemNo) {
    return (lastPageData?.list || []).find(item => item.orderItemNo === orderItemNo);
}

function contextPath() {
    const meta = document.querySelector('meta[name="context-path"]')?.content || '/';
    return meta.endsWith('/') ? meta : `${meta}/`;
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString('ko-KR');
}

function formatDate(date) {
    return date.toISOString().slice(0, 10);
}

function formatDateText(value) {
    if (!value) return '-';
    return String(value).replace('T', ' ').slice(0, 10);
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, char => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    }[char]));
}

function errorHandler(error) {
    console.error(error);
    alert(error.message || '처리 중 오류가 발생했습니다.');
}
