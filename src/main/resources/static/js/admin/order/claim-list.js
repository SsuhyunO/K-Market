import { closeModal, initModals, openModal } from '../../global/modal-form.js';
import { initPagination } from '../../global/pagination.js';
import { escapeHtml } from '../../global/htmlUtils.js';
import { getContextPath } from '../../global/pathUtils.js';

let activeClaim = null;
let submitting = false;

document.addEventListener('DOMContentLoaded', function () {
    initModals();
    initSearchForm();
    initClaimTableEvents();
    initClaimActions();

    initPagination({
        fetchPage: loadClaims,
        onError: errorHandler
    });
});

async function loadClaims(page = 1) {
    const params = new URLSearchParams({ page });
    const type = getClaimType();
    if (type && type !== 'ALL') params.set('type', type);

    const pageData = await fetchJson(`${getContextPath()}admin/order/api/claims?${params.toString()}`);
    renderClaimRows(pageData);
    return pageData;
}

function initSearchForm() {
    const searchForm = document.searchForm;
    if (!searchForm) return;

    searchForm.addEventListener('submit', event => {
        event.preventDefault();
        const params = new URLSearchParams(window.location.search);
        const type = getClaimType();

        params.set('page', '1');
        type && type !== 'ALL' ? params.set('type', type) : params.delete('type');

        window.history.pushState(null, '', `${window.location.pathname}?${params.toString()}`);
        window.dispatchEvent(new CustomEvent('pagination:refresh', { detail: { page: 1 } }));
    });

    searchForm.type?.addEventListener('change', () => {
        searchForm.dispatchEvent(new Event('submit', { cancelable: true }));
    });
}

function getClaimType() {
    return document.searchForm?.type?.value || 'ALL';
}

function initClaimTableEvents() {
    const table = document.querySelector('.claim-list-table .management-table');
    const modal = document.getElementById('claim-detail-modal');
    if (!table || !modal) return;

    table.addEventListener('click', async event => {
        const button = event.target.closest('[data-claim-no]');
        if (!button) return;

        try {
            activeClaim = await fetchJson(`${getContextPath()}admin/order/api/claims/${encodeURIComponent(button.dataset.claimNo)}`);
            renderClaimDetail(activeClaim);
            openModal(modal);
        } catch (error) {
            errorHandler(error);
        }
    });
}

function initClaimActions() {
    document.getElementById('claim-approve-btn')?.addEventListener('click', () => submitClaimAction('approve'));
    document.getElementById('claim-reject-btn')?.addEventListener('click', () => submitClaimAction('reject'));
    document.getElementById('claim-reship-btn')?.addEventListener('click', submitReship);
}

function renderClaimRows(pageData) {
    const table = document.querySelector('.claim-list-table .management-table');
    resetTable(table);

    const claims = pageData.list || [];
    if (claims.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="10">조회된 클레임이 없습니다.</td>`;
        table?.appendChild(row);
        return;
    }

    claims.forEach(claim => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><button class="claim-detail-btn" type="button" data-claim-no="${escapeHtml(claim.claimNo)}">${escapeHtml(claim.claimNo)}</button></td>
            <td>${renderClaimType(claim.claimType)}</td>
            <td>${escapeHtml(claim.orderNo)}</td>
            <td>${escapeHtml(claim.memberName || claim.memberUid || '-')}</td>
            <td class="claim-product">${escapeHtml(formatProduct(claim))}</td>
            <td>${escapeHtml(claim.quantity || 0)}</td>
            <td><span class="claim-status ${statusClass(claim.claimStatus)}">${renderClaimStatus(claim.claimStatus)}</span></td>
            <td>${renderItemStatus(claim.itemStatus || claim.shipmentStatus)}</td>
            <td>${escapeHtml(formatDate(claim.requestedAt))}</td>
            <td>${renderActionSummary(claim)}</td>
        `;
        table?.appendChild(row);
    });
}

function renderClaimDetail(claim) {
    setText('claim-detail-no', claim.claimNo);
    setText('claim-detail-type', renderClaimType(claim.claimType));
    setText('claim-detail-status', renderClaimStatus(claim.claimStatus));
    setText('claim-detail-order-no', claim.orderNo);
    setText('claim-detail-member', `${claim.memberName || '-'} / ${claim.memberUid || '-'}`);
    setText('claim-detail-product', formatProduct(claim));
    setText('claim-detail-quantity', claim.quantity);
    setText('claim-detail-content', claim.claimContent || '-');
    setText('claim-detail-shipment', `${renderItemStatus(claim.itemStatus)} / ${renderItemStatus(claim.shipmentStatus)}`);
    setText('claim-detail-address', `[${claim.zipCode || ''}] ${claim.addr1 || ''} ${claim.addr2 || ''}`.trim());
    setText('claim-detail-requested-at', formatDateTime(claim.requestedAt));
    setText('claim-detail-processed-at', formatDateTime(claim.processedAt));

    const reshipForm = document.claimReshipForm;
    if (reshipForm) reshipForm.trackingNo.value = '';

    const isRequested = claim.claimStatus === 'REQUESTED';
    const canApprove = isSeller() && isRequested;
    const canReship = claim.claimType === 'EXCHANGE'
        && isSeller()
        && claim.claimStatus === 'APPROVED'
        && claim.itemStatus === 'EXCHANGE_WAITING';

    setVisible('claim-approve-btn', canApprove);
    setVisible('claim-reject-btn', canApprove);
    setVisible('claim-reship-btn', canReship);
    setVisible('.claim-reship-form', canReship);
}

async function submitClaimAction(action) {
    if (!activeClaim || submitting) return;
    const message = action === 'approve' ? '클레임을 승인하시겠습니까?' : '클레임을 거절하시겠습니까?';
    if (!confirm(message)) return;

    submitting = true;
    try {
        activeClaim = await postJson(`${getContextPath()}admin/order/api/claims/${encodeURIComponent(activeClaim.claimNo)}/${action}`, {});
        renderClaimDetail(activeClaim);
        window.dispatchEvent(new CustomEvent('pagination:refresh'));
    } catch (error) {
        errorHandler(error);
    } finally {
        submitting = false;
    }
}

async function submitReship() {
    if (!activeClaim || submitting) return;
    const form = document.claimReshipForm;
    const courierName = form?.courierName?.value;
    const trackingNo = form?.trackingNo?.value.trim();
    if (!courierName || !trackingNo) {
        alert('택배사와 운송장번호를 입력해주세요.');
        return;
    }
    if (!confirm('기존 배송지로 교환상품을 재배송하시겠습니까?')) return;

    submitting = true;
    try {
        activeClaim = await postJson(`${getContextPath()}admin/order/api/claims/${encodeURIComponent(activeClaim.claimNo)}/reship`, {
            courierName,
            trackingNo
        });
        renderClaimDetail(activeClaim);
        forceCloseModal('claim-detail-modal');
        window.dispatchEvent(new CustomEvent('pagination:refresh'));
    } catch (error) {
        errorHandler(error);
    } finally {
        submitting = false;
    }
}

function resetTable(table) {
    if (!table) return;
    const header = table.querySelector('tr');
    table.innerHTML = '';
    if (header) table.appendChild(header);
}

async function fetchJson(url) {
    const response = await fetch(url, { headers: { Accept: 'application/json' } });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

async function postJson(url, payload) {
    const response = await fetch(url, {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(json.message || `Request failed: ${response.status}`);
    return json;
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (target) target.textContent = value ?? '-';
}

function setVisible(selector, visible) {
    const target = selector.startsWith('.') ? document.querySelector(selector) : document.getElementById(selector);
    if (!target) return;
    target.hidden = !visible;
    target.style.display = visible ? '' : 'none';
}

function forceCloseModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    closeModal(modal);
    modal.classList.add('hidden');
    modal.style.display = 'none';
}

function renderClaimType(type) {
    return {
        RETURN: '반품',
        EXCHANGE: '교환',
        CANCEL: '취소'
    }[type] || type || '-';
}

function renderClaimStatus(status) {
    return {
        REQUESTED: '승인대기',
        APPROVED: '승인',
        REJECTED: '거절',
        RESHIPPING: '재배송중',
        COMPLETED: '완료'
    }[status] || status || '-';
}

function renderItemStatus(status) {
    return {
        PAID: '결제완료',
        READY: '배송준비',
        SHIPPING: '배송중',
        DELIVERED: '배송완료',
        CONFIRMED: '구매확정',
        RETURN_REQUESTED: '반품요청',
        RETURN_SHIPPING: '반품 반송중',
        RETURNED: '반품완료',
        EXCHANGE_REQUESTED: '교환요청',
        EXCHANGE_SHIPPING: '교환 반송중',
        EXCHANGE_WAITING: '상품대기중',
        CANCELED: '주문취소'
    }[status] || status || '-';
}

function isSeller() {
    return document.querySelector('meta[name="login-member-type"]')?.content === 'SELLER';
}

function statusClass(status) {
    if (status === 'REQUESTED') return 'pending';
    if (status === 'APPROVED' || status === 'RESHIPPING') return 'active';
    if (status === 'COMPLETED') return 'complete';
    if (status === 'REJECTED') return 'rejected';
    return 'pending';
}

function renderActionSummary(claim) {
    if (claim.claimStatus === 'REQUESTED') return '승인대기';
    if (claim.claimType === 'EXCHANGE' && claim.claimStatus === 'APPROVED' && claim.itemStatus === 'EXCHANGE_WAITING') {
        return '배송하기';
    }
    return renderClaimStatus(claim.claimStatus);
}

function formatProduct(claim) {
    return `${claim.productName || '-'}${claim.optionText ? ` / ${claim.optionText}` : ''}`;
}

function formatDate(value) {
    return value ? String(value).substring(0, 10) : '-';
}

function formatDateTime(value) {
    return value ? String(value).replace('T', ' ').substring(0, 16) : '-';
}

function errorHandler(error) {
    console.error(error);
    alert(error.message || '클레임 정보를 처리하지 못했습니다.');
}
