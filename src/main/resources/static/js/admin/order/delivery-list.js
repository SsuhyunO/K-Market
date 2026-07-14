import { initModals, openModal } from '../../global/modal-form.js';
import { initPagination } from '../../global/pagination.js';
import { escapeHtml } from '../../global/htmlUtils.js';
import { getContextPath, getFileUrl } from '../../global/pathUtils.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    initSearchForm();
    initDeliveryTableEvents();

    initPagination({
        fetchPage: loadDeliveries,
        onError: errorHandler
    });
});

async function loadDeliveries(page = 1) {
    const { type, keyword } = getSearchCondition();
    const params = new URLSearchParams({ page });
    if (type) params.set('type', type);
    if (keyword) params.set('keyword', keyword);

    const pageData = await fetchJson(`${getContextPath()}admin/order/api/deliveries?${params.toString()}`);
    renderDeliveryRows(pageData);
    return pageData;
}

function initSearchForm() {
    const searchForm = document.searchForm;
    if (!searchForm) return;

    searchForm.addEventListener('submit', event => {
        event.preventDefault();
        const params = new URLSearchParams(window.location.search);
        const { type, keyword } = getSearchCondition();
        params.set('page', '1');
        type ? params.set('type', type) : params.delete('type');
        keyword ? params.set('keyword', keyword) : params.delete('keyword');
        window.history.pushState(null, '', `${window.location.pathname}?${params.toString()}`);
        window.dispatchEvent(new CustomEvent('pagination:refresh', { detail: { page: 1 } }));
    });
}

function getSearchCondition() {
    const searchForm = document.searchForm;
    return {
        type: searchForm?.searchType?.value || null,
        keyword: searchForm?.keyword?.value.trim() || null
    };
}

function initDeliveryTableEvents() {
    const table = document.querySelector('.delivery-list-table .management-table');
    const modal = document.getElementById('delivery-detail-modal');
    if (!table || !modal) return;

    table.addEventListener('click', async event => {
        const button = event.target.closest('[data-shipment-no]');
        if (!button) return;

        try {
            renderDeliveryDetail(await fetchJson(`${getContextPath()}admin/order/api/deliveries/${encodeURIComponent(button.dataset.shipmentNo)}`));
            openModal(modal);
        } catch (error) {
            errorHandler(error);
        }
    });
}

function renderDeliveryRows(pageData) {
    const table = document.querySelector('.delivery-list-table .management-table');
    resetTable(table);

    const deliveries = pageData.list || [];
    if (deliveries.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="10">\uc870\ud68c\ub41c \ubc30\uc1a1 \ub0b4\uc5ed\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.</td>`;
        table?.appendChild(row);
        return;
    }

    deliveries.forEach(delivery => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><button class="delivery-detail-btn" type="button" data-shipment-no="${escapeHtml(delivery.shipmentNo)}">${escapeHtml(delivery.trackingNo || '-')}</button></td>
            <td>${escapeHtml(delivery.courierName || '-')}</td>
            <td>${escapeHtml(delivery.orderNo)}</td>
            <td>${escapeHtml(delivery.receiver || '-')}</td>
            <td>${escapeHtml(delivery.prodName || '-')}</td>
            <td>${escapeHtml(delivery.quantity || 0)}</td>
            <td>${formatNumber(delivery.itemTotal)}</td>
            <td>${renderShippingFee(delivery.shippingFee)}</td>
            <td><span class="delivery-status ${statusClass(delivery.itemStatus || delivery.status)}">${renderStatus(delivery.itemStatus || delivery.status)}</span></td>
            <td>${escapeHtml(formatDate(delivery.shippedAt || delivery.createdAt))}</td>
        `;
        table?.appendChild(row);
    });
}

function renderDeliveryDetail(data) {
    const shipment = data.shipment || {};
    const items = data.items || [];
    const table = document.querySelector('#delivery-detail-modal .management-table');
    resetTable(table);

    let total = 0;
    items.forEach(item => {
        total += Number(item.total || 0);
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.thumb1FileId ? `<img class="product-thumb" src="${getFileUrl(item.thumb1FileId)}" alt="">` : '-'}</td>
            <td>${escapeHtml(item.prodNo || '-')}</td>
            <td>${escapeHtml(item.prodName || '-')}</td>
            <td>${escapeHtml(item.sellerName || item.sellerUid || '-')}</td>
            <td>${formatNumber(item.price)}</td>
            <td>${escapeHtml(item.count || 0)}</td>
            <td>${formatNumber(item.total)}</td>
        `;
        table?.appendChild(row);
    });

    setText('delivery-detail-order-no', shipment.orderNo);
    setText('delivery-detail-orderer', `${shipment.receiver || '-'} / ${shipment.phone || '-'}`);
    setText('delivery-detail-total', `${formatNumber(total)}원`);
    setText('delivery-detail-receiver', shipment.receiver);
    setText('delivery-detail-phone', shipment.phone);
    setText('delivery-detail-address', shipment.address);
    setText('delivery-detail-courier', shipment.courierName);
    setText('delivery-detail-tracking-no', shipment.trackingNo);
    setText('delivery-detail-status', renderStatus(shipment.status));
    setText('delivery-detail-created-at', formatDate(shipment.shippedAt || shipment.createdAt));
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

function setText(id, value) {
    const target = document.getElementById(id);
    if (target) target.textContent = value ?? '-';
}

function renderStatus(status) {
    return {
        PAID: '\uacb0\uc81c\uc644\ub8cc',
        READY: '\uc0c1\ud488\uc900\ube44\uc911',
        SHIPPING: '\ubc30\uc1a1\uc911',
        PARTIAL_SHIPPING: '\ubd80\ubd84\ubc30\uc1a1\uc911',
        DELIVERED: '\ubc30\uc1a1\uc644\ub8cc',
        CONFIRMED: '\uad6c\ub9e4\ud655\uc815',
        RETURN_REQUESTED: '\ubc18\ud488\uc694\uccad',
        EXCHANGE_REQUESTED: '\uad50\ud658\uc694\uccad',
        CANCEL_REQUESTED: '\ucde8\uc18c\uc694\uccad',
        CLAIM_PARTIAL: '\ud074\ub808\uc784\ucc98\ub9ac\uc911'
    }[status] || status || '-';
}

function statusClass(status) {
    if (status === 'DELIVERED' || status === 'CONFIRMED') return 'complete';
    if (String(status || '').includes('REQUESTED') || status === 'CLAIM_PARTIAL') return 'claim';
    return 'in-transit';
}

function renderShippingFee(value) {
    const fee = Number(value || 0);
    return fee > 0 ? formatNumber(fee) : '\ubb34\ub8cc';
}

function formatNumber(value) {
    const number = Number(value || 0);
    return Number.isFinite(number) ? number.toLocaleString('ko-KR') : '0';
}

function formatDate(value) {
    return value ? String(value).substring(0, 10) : '-';
}

function errorHandler(error) {
    console.error(error);
    alert(error.message || '\ubc30\uc1a1 \ub0b4\uc5ed\uc744 \ubd88\ub7ec\uc624\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4.');
}
