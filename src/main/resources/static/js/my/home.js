let lastRecentOrders = [];
let activeItem = null;

document.addEventListener('DOMContentLoaded', () => {
    initModals();
    initRecentOrderActions();
    loadRecentOrders().catch(errorHandler);
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

    document.querySelectorAll('#reviewStarRating .star').forEach(star => {
        star.addEventListener('click', () => {
            const value = Number(star.dataset.value);
            document.querySelectorAll('#reviewStarRating .star').forEach(s => {
                s.classList.toggle('star-filled', Number(s.dataset.value) <= value);
            });
        });
    });
    document.querySelector('#reviewWriteModal .review-file-input')?.addEventListener('change', function () {
        document.getElementById('reviewFileName1').textContent = this.files.length > 0 ? this.files[0].name : '선택된파일없음';
    });
}

function initRecentOrderActions() {
    document.getElementById('recentOrderListBody')?.addEventListener('click', async event => {
        const row = event.target.closest('[data-order-item-no]');
        if (!row) return;

        activeItem = lastRecentOrders.find(item => item.orderItemNo === Number(row.dataset.orderItemNo));
        if (!activeItem) return;

        if (event.target.closest('.js-order-detail')) {
            event.preventDefault();
            try {
                const orderItems = await fetchOrderItemsByOrderNo(activeItem.orderNo);
                fillOrderModal(orderItems);
                openModal('orderDetailModal');
            } catch (error) {
                errorHandler(error);
            }
        } else if (event.target.closest('.js-confirm-purchase')) {
            event.preventDefault();
            openModal('confirmPurchaseModal');
        } else if (event.target.closest('.js-return-request')) {
            event.preventDefault();
            fillClaimModal('return', activeItem);
            openModal('returnRequestModal');
        } else if (event.target.closest('.js-exchange-request')) {
            event.preventDefault();
            fillClaimModal('exchange', activeItem);
            openModal('exchangeRequestModal');
        } else if (event.target.closest('.js-write-review')) {
            event.preventDefault();
            fillReviewModal(activeItem);
            openModal('reviewWriteModal');
        }
    });

    document.querySelector('.js-confirm-ok')?.addEventListener('click', async () => {
        if (!activeItem) return;
        await postJson(`${contextPath()}my/order/api/${activeItem.orderItemNo}/confirm`, {});
        closeModal('confirmPurchaseModal');
        await loadRecentOrders();
    });

    document.getElementById('returnSubmitBtn')?.addEventListener('click', () => {
        submitClaim('RETURN', 'returnType', 'returnReason', 'returnRequestModal').catch(errorHandler);
    });
    document.getElementById('exchangeSubmitBtn')?.addEventListener('click', () => {
        submitClaim('EXCHANGE', 'exchangeType', 'exchangeReason', 'exchangeRequestModal').catch(errorHandler);
    });
    document.getElementById('reviewSubmitBtn')?.addEventListener('click', () => {
        submitReview().catch(errorHandler);
    });
    document.getElementById('orderCancelBtn')?.addEventListener('click', () => {
        cancelOrder().catch(errorHandler);
    });
}

async function loadRecentOrders() {
    const data = await fetchJson(`${contextPath()}my/order/api/list?page=1&size=5`);
    lastRecentOrders = data.list || [];
    renderRecentOrders(lastRecentOrders);
}

function renderRecentOrders(items) {
    const tbody = document.getElementById('recentOrderListBody');
    if (!tbody) return;
    tbody.innerHTML = '';

    if (items.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="order-empty">최근 주문내역이 없습니다.</td></tr>';
        return;
    }

    items.forEach(item => {
        const row = document.createElement('tr');
        row.className = 'js-order-row';
        row.dataset.orderItemNo = item.orderItemNo;
        row.innerHTML = `
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
                    </div>
                </div>
            </td>
            <td class="order-status">${renderStatus(item.itemStatus)}</td>
            <td class="order-action"><div class="action-btn-group">${actionButtons(item)}</div></td>
        `;
        tbody.appendChild(row);
    });
}

function actionButtons(item) {
    const buttons = [];
    if (item.itemStatus === 'DELIVERED') {
        buttons.push('<a href="#" class="btn-blue js-confirm-purchase">구매확정</a>');
        buttons.push('<a href="#" class="btn-white js-return-request">반품요청</a>');
        buttons.push('<a href="#" class="btn-white js-exchange-request">교환요청</a>');
    } else if (item.itemStatus === 'CONFIRMED' && !item.reviewed) {
        buttons.push('<a href="#" class="btn-blue js-write-review">리뷰작성</a>');
    }
    return buttons.length ? buttons.join('') : '-';
}

function fillOrderModal(items) {
    const orderItems = Array.isArray(items) ? items : [];
    const firstItem = orderItems[0] || activeItem;

    setText('detailDate', formatDateText(firstItem?.createdAt));
    const tbody = document.getElementById('orderDetailItemsBody');
    if (tbody) {
        tbody.innerHTML = orderItems.length > 0
            ? orderItems.map(renderDetailItemRow).join('')
            : '<tr><td colspan="4">주문정보를 찾을 수 없습니다.</td></tr>';
    }

    const cancelButton = document.getElementById('orderCancelBtn');
    if (cancelButton) {
        cancelButton.hidden = !isOrderCancelable(orderItems);
    }
}

function renderDetailItemRow(item) {
    return `
        <tr>
            <td class="modal-order-date">${escapeHtml(formatDateText(item.createdAt))}</td>
            <td class="modal-order-product">
                <div class="product-inner">
                    <div class="product-img">${renderThumb(item.thumb1FileId)}</div>
                    <div class="product-info">
                        <p class="order-no">주문번호 : ${escapeHtml(item.orderNo)}</p>
                        <p class="company-name">${escapeHtml(item.sellerName || item.sellerUid || '-')}</p>
                        <p class="product-name">${escapeHtml(item.productName || '-')}</p>
                        ${item.optionText ? `<p class="product-qty">옵션 : ${escapeHtml(item.optionText)}</p>` : ''}
                        <p class="product-qty">수량 : ${escapeHtml(item.quantity || 0)}개</p>
                        <p class="product-price">${formatNumber(item.total)}원</p>
                    </div>
                </div>
            </td>
            <td class="modal-pay-info">
                <p>판매가 <span>${formatNumber(item.price)}원</span></p>
                <p class="modal-pay-total">결제금액 <span>${formatNumber(item.total)}원</span></p>
            </td>
            <td class="modal-order-status">${escapeHtml(renderStatus(item.itemStatus))}</td>
        </tr>
    `;
}

function isOrderCancelable(items) {
    const cancellableStatuses = ['PAID', 'READY', 'SHIPPING', 'PARTIAL_SHIPPING'];
    return items.length > 0 && items.every(item => cancellableStatuses.includes(item.itemStatus));
}

function fillClaimModal(type, item) {
    const prefix = type === 'return' ? 'return' : 'exchange';
    setHtml(`${prefix}Thumb`, renderThumb(item.thumb1FileId));
    setText(`${prefix}Date`, formatDateText(item.createdAt));
    setText(`${prefix}OrderNo`, `주문번호 : ${item.orderNo}`);
    setText(`${prefix}Company`, item.sellerName || item.sellerUid || '-');
    setText(`${prefix}Product`, item.productName || '-');
    setText(`${prefix}Qty`, `수량 : ${item.quantity || 0}개`);
    setText(`${prefix}Price`, `${formatNumber(item.total)}원`);
}

function fillReviewModal(item) {
    const nameEl = document.getElementById('reviewProductName');
    nameEl.textContent = item.productName || '-';
    nameEl.dataset.prodNo = item.prodNo;

    document.querySelectorAll('#reviewStarRating .star').forEach(s => s.classList.remove('star-filled'));
    document.getElementById('reviewContent').value = '';
    document.getElementById('reviewFileName1').textContent = '선택된파일없음';
    document.querySelector('#reviewWriteModal .review-file-input').value = '';
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
    await loadRecentOrders();
}

async function submitReview() {
    if (!activeItem) return;

    const content = document.getElementById('reviewContent').value.trim();
    const rating = document.querySelectorAll('#reviewStarRating .star.star-filled').length;
    const prodNo = document.getElementById('reviewProductName').dataset.prodNo;

    if (content.length < 10) {
        alert('최소 10자 이상 작성해주세요.');
        return;
    }
    if (rating === 0) {
        alert('별점을 선택해주세요.');
        return;
    }

    const formData = new FormData();
    formData.append('orderItemNo', activeItem.orderItemNo);
    formData.append('prodNo', prodNo);
    formData.append('rating', rating);
    formData.append('content', content);

    const fileInput = document.querySelector('#reviewWriteModal .review-file-input');
    if (fileInput.files.length > 0) {
        formData.append('photo', fileInput.files[0]);
    }

    await postForm(`${contextPath()}review/api/write`, formData);
    closeModal('reviewWriteModal');
    await loadRecentOrders();
}

async function postForm(url, formData) {
    const response = await fetch(url, {
        method: 'POST',
        headers: { Accept: 'application/json' },
        body: formData
    });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

async function cancelOrder() {
    if (!activeItem) return;
    if (!confirm('해당 주문을 취소하시겠습니까? 배송중인 배송건은 배송취소 상태로 변경됩니다.')) {
        return;
    }
    await postJson(`${contextPath()}my/order/api/orders/${activeItem.orderNo}/cancel`, {});
    closeModal('orderDetailModal');
    await loadRecentOrders();
}

async function fetchJson(url) {
    const response = await fetch(url, { headers: { Accept: 'application/json' } });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

function fetchOrderItemsByOrderNo(orderNo) {
    return fetchJson(`${contextPath()}my/order/api/orders/${encodeURIComponent(orderNo)}/items`);
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

function setHtml(id, value) {
    const target = document.getElementById(id);
    if (target) target.innerHTML = value;
}

function contextPath() {
    const meta = document.querySelector('meta[name="context-path"]')?.content || '/';
    return meta.endsWith('/') ? meta : `${meta}/`;
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString('ko-KR');
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
