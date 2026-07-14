import { closeModal, initModals, openModal } from '../../global/modal-form.js';
import { initPagination } from '../../global/pagination.js';
import { getOrders, getOrderDetail, getShippableItems, registerDelivery } from './orderApi.js';
import { renderOrderRows } from './orderTableRenderer.js';
import { escapeHtml } from '../../global/htmlUtils.js';

let selectedDeliveryOrder = null;
let deliverySubmitting = false;

document.addEventListener('DOMContentLoaded', function() {
    initModals();
    initOrderTableEvents();
    initSearchForm();
    initDeliveryForm();

    initPagination({
        fetchPage: loadOrders,
        onError: errorHandler
    });
});

async function loadOrders(page = 1) {
    const { type, keyword } = getSearchCondition();
    const pageData = await getOrders({ page, searchType: type, keyword });
    renderOrderRows(pageData);
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

function initOrderTableEvents() {
    const orderModal = document.getElementById("order-detail-modal");
    const deliveryModal = document.getElementById("delivery-register-modal");
    const table = document.getElementsByClassName("management-table")[0];
    if (!table) return;

    table.addEventListener('click', async event => {
        const detailButton = event.target.closest('.order-detail-btn');
        if (detailButton && orderModal) {
            try {
                renderOrderDetail(await getOrderDetail(detailButton.dataset.orderNo));
                openModal(orderModal);
            } catch (error) {
                errorHandler(error);
            }
            return;
        }

        const deliveryButton = event.target.closest('.register-delivery-btn');
        if (deliveryButton && deliveryModal) {
            try {
                const orderNo = deliveryButton.dataset.orderNo;
                selectedDeliveryOrder = await getOrderDetail(orderNo);
                renderDeliveryOrder(selectedDeliveryOrder, await getShippableItems(orderNo));
                openModal(deliveryModal);
            } catch (error) {
                errorHandler(error);
            }
        }
    });
}

function renderOrderDetail(data) {
    const order = data.order || {};
    const items = data.items || [];
    const productTable = document.querySelector('#order-detail-modal .management-table');
    resetTable(productTable);

    items.forEach(item => productTable?.appendChild(createProductRow(item, false)));

    setText('modal-product-total', formatNumber(order.orderPrice) + '원');
    setText('modal-discount-total', formatNumber(order.orderDiscount) + '원');
    setText('modal-shipping-total', formatNumber(order.shippingFee) + '원');
    setText('modal-final-total', formatNumber(order.orderTotal) + '원');
    setText('modal-order-no', order.orderNo);
    setText('modal-pay-method', renderPayMethod(order.payMethod));
    setText('modal-orderer-info', `${order.memberUid || '-'} / ${order.phone || '-'}`);
    setText('modal-pay-status', renderStatus(order.status));
    setText('modal-payment-total', formatNumber(order.orderTotal) + '원');
    setText('modal-receiver', order.receiver);
    setText('modal-phone', order.phone);
    setText('modal-address', `[${order.zipCode || ''}] ${order.addr1 || ''} ${order.addr2 || ''}`.trim());
}

function renderDeliveryOrder(data, items) {
    const order = data.order || {};
    const form = document.deliveryRegisterForm;
    if (!form) return;

    form.orderNo.value = order.orderNo || '';
    form.recipient.value = order.receiver || '';
    form.zipCode.value = order.zipCode || '';
    form.defaultAddr.value = order.addr1 || '';
    form.detailAddr.value = order.addr2 || '';
    form.trackingNumber.value = '';

    const table = document.getElementById('delivery-items-table');
    resetTable(table);

    if (!items || items.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="6">\ubc30\uc1a1 \ub4f1\ub85d \uac00\ub2a5\ud55c \uc0c1\ud488\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.</td>`;
        table?.appendChild(row);
        return;
    }

    items.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><label><input type="checkbox" name="orderItemNo" value="${escapeHtml(item.orderItemNo)}" data-seller-uid="${escapeHtml(item.sellerUid)}"></label></td>
            <td>${escapeHtml(item.prodNo || '-')}</td>
            <td>${escapeHtml(item.prodName || '-')}</td>
            <td>${escapeHtml(item.sellerName || item.sellerUid || '-')}</td>
            <td>${escapeHtml(item.count || 0)}</td>
            <td>${renderStatus(item.itemStatus)}</td>
        `;
        table?.appendChild(row);
    });
}

function initDeliveryForm() {
    const form = document.deliveryRegisterForm;
    if (!form) return;

    form.addEventListener('submit', async event => {
        event.preventDefault();
        if (deliverySubmitting) return;

        const checked = [...form.querySelectorAll('input[name="orderItemNo"]:checked')];
        if (checked.length === 0) {
            alert('\ubc30\uc1a1\ud560 \uc0c1\ud488\uc744 \uc120\ud0dd\ud574\uc8fc\uc138\uc694.');
            return;
        }

        const sellerUids = new Set(checked.map(input => input.dataset.sellerUid));
        if (sellerUids.size > 1) {
            alert('\uac19\uc740 \ud310\ub9e4\uc790 \uc0c1\ud488\ub9cc \ud558\ub098\uc758 \uc1a1\uc7a5\uc73c\ub85c \ubb36\uc744 \uc218 \uc788\uc2b5\ub2c8\ub2e4.');
            return;
        }

        const submitButton = form.querySelector('button[type="submit"]');
        deliverySubmitting = true;
        if (submitButton) submitButton.disabled = true;

        try {
            await registerDelivery({
                orderNo: Number.parseInt(form.orderNo.value, 10),
                courierName: form.deliveryCompany.value,
                trackingNo: form.trackingNumber.value.trim(),
                orderItemNos: checked.map(input => Number.parseInt(input.value, 10))
            });
            forceCloseModal('delivery-register-modal');
            form.reset();
            alert('\ubc30\uc1a1\uc774 \ub4f1\ub85d\ub418\uc5c8\uc2b5\ub2c8\ub2e4.');
            window.dispatchEvent(new CustomEvent('pagination:refresh'));
        } catch (error) {
            errorHandler(error);
        } finally {
            deliverySubmitting = false;
            if (submitButton) submitButton.disabled = false;
        }
    });
}

function forceCloseModal(id) {
    const modal = document.getElementById(id);
    if (!modal) return;
    closeModal(modal);
    modal.classList.add('hidden');
    modal.style.display = 'none';
}

function createProductRow(item) {
    const row = document.createElement('tr');
    row.innerHTML = `
        <td>${item.thumb1FileId ? `<img class="product-thumb" src="${getFileUrl(item.thumb1FileId)}" alt="">` : '-'}</td>
        <td>${escapeHtml(item.prodNo || '-')}</td>
        <td>${escapeHtml(item.prodName || '-')}</td>
        <td>${escapeHtml(item.sellerName || item.sellerUid || '-')}</td>
        <td>${formatNumber(item.originalPrice || item.price)}</td>
        <td>${renderDiscount(item)}</td>
        <td>${escapeHtml(item.count || 0)}</td>
        <td>${formatNumber(item.shippingFee)}</td>
        <td>${formatNumber(item.total)}</td>
    `;
    return row;
}

function renderDiscount(item) {
    const amount = Number(item.discountAmount || 0);
    const rate = Number(item.discountRate || 0);
    if (amount > 0) return `${formatNumber(amount)}원`;
    if (rate > 0) return `${formatNumber(rate)}%`;
    return '0원';
}

function resetTable(table) {
    if (!table) return;
    const header = table.querySelector('tr');
    table.innerHTML = '';
    if (header) table.appendChild(header);
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (target) target.textContent = value ?? '-';
}

function getFileUrl(fileId) {
    const contextPath = document.querySelector('meta[name="context-path"]')?.content || '/';
    return `${contextPath}files/${encodeURIComponent(fileId)}`;
}

function renderPayMethod(payMethod) {
    return {
        CARD: '\uc2e0\uc6a9\uce74\ub4dc',
        CHECK_CARD: '\uccb4\ud06c\uce74\ub4dc',
        BANK: '\uacc4\uc88c\uc774\uccb4',
        VBANK: '\ubb34\ud1b5\uc7a5\uc785\uae08',
        PHONE: '\ud734\ub300\ud3f0\uacb0\uc81c',
        KAKAO: '\uce74\uce74\uc624\ud398\uc774'
    }[payMethod] || payMethod || '-';
}

function renderStatus(status) {
    return {
        PAID: '\uacb0\uc81c\uc644\ub8cc',
        READY: '\uc0c1\ud488\uc900\ube44\uc911',
        SHIPPING: '\ubc30\uc1a1\uc911',
        PARTIAL_SHIPPING: '\ubd80\ubd84\ubc30\uc1a1\uc911',
        DELIVERED: '\ubc30\uc1a1\uc644\ub8cc',
        CONFIRMED: '\uad6c\ub9e4\ud655\uc815',
        CLAIM_PARTIAL: '\ud074\ub808\uc784\ucc98\ub9ac\uc911',
        RETURN_REQUESTED: '\ubc18\ud488\uc694\uccad',
        EXCHANGE_REQUESTED: '\uad50\ud658\uc694\uccad',
        CANCEL_REQUESTED: '\ucde8\uc18c\uc694\uccad'
    }[status] || status || '-';
}

function formatNumber(value) {
    const number = Number(value || 0);
    return Number.isFinite(number) ? number.toLocaleString('ko-KR') : '0';
}

function errorHandler(error) {
    console.error('Error:', error);
    alert(error.message || '\uc694\uccad\uc744 \ucc98\ub9ac\ud558\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4.');
}
