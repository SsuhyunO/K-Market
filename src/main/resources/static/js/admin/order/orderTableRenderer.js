import { escapeHtml } from '../../global/htmlUtils.js';

export function renderOrderRows(pageData) {
    const table = document.getElementsByClassName("management-table")[0];
    if (!table) return;

    const headerRow = table.querySelector("tr");
    table.innerHTML = "";

    if (headerRow) {
        table.appendChild(headerRow);
    }

    const orders = pageData.list || [];
    if (orders.length === 0) {
        const emptyRow = document.createElement("tr");
        emptyRow.innerHTML = `<td colspan="9" style="text-align: center; padding: 30px 0;">\uc870\ud68c\ub41c \uc8fc\ubb38 \ub0b4\uc5ed\uc774 \uc5c6\uc2b5\ub2c8\ub2e4.</td>`;
        table.appendChild(emptyRow);
        return;
    }

    orders.forEach(order => table.appendChild(createOrderRow(order)));
}

function createOrderRow(order) {
    const row = document.createElement("tr");
    const orderNo = escapeHtml(order.orderNo);
    const status = order.status || "";
    const canRegisterDelivery = ["PAID", "READY", "PARTIAL_SHIPPING"].includes(status);

    row.dataset.orderNo = orderNo;
    row.innerHTML = `
        <td>
            <button class="order-detail-btn" type="button" data-order-no="${orderNo}">${orderNo}</button>
        </td>
        <td>${escapeHtml(order.memberUid || "-")}</td>
        <td>${escapeHtml(order.memberName || "-")}</td>
        <td>${escapeHtml(order.totalCount ?? 0)}</td>
        <td>${formatNumber(order.orderTotal)}</td>
        <td>${renderPayMethod(order.payMethod)}</td>
        <td>${renderStatus(status)}</td>
        <td>${escapeHtml(formatDate(order.createdAt))}</td>
        <td>${renderDeliveryAction(orderNo, canRegisterDelivery)}</td>
    `;

    return row;
}

function renderPayMethod(payMethod) {
    const labels = {
        CARD: "\uc2e0\uc6a9\uce74\ub4dc",
        CHECK_CARD: "\uccb4\ud06c\uce74\ub4dc",
        BANK: "\uacc4\uc88c\uc774\uccb4",
        VBANK: "\ubb34\ud1b5\uc7a5\uc785\uae08",
        PHONE: "\ud734\ub300\ud3f0\uacb0\uc81c",
        KAKAO: "\uce74\uce74\uc624\ud398\uc774"
    };

    return escapeHtml(labels[payMethod] || payMethod || "-");
}

function renderStatus(status) {
    const labels = {
        PAID: "\uacb0\uc81c\uc644\ub8cc",
        READY: "\uc0c1\ud488\uc900\ube44\uc911",
        SHIPPING: "\ubc30\uc1a1\uc911",
        PARTIAL_SHIPPING: "\ubd80\ubd84\ubc30\uc1a1\uc911",
        PARTIAL_DELIVERED: "\ubd80\ubd84\ubc30\uc1a1\uc644\ub8cc",
        CLAIM_PARTIAL: "\ud074\ub808\uc784\ucc98\ub9ac\uc911",
        CONFIRMED: "\uad6c\ub9e4\ud655\uc815",
        DELIVERED: "\ubc30\uc1a1\uc644\ub8cc",
        CANCEL: "\uc8fc\ubb38\ucde8\uc18c"
    };

    return escapeHtml(labels[status] || status || "-");
}

function renderDeliveryAction(orderNo, canRegisterDelivery) {
    if (!canRegisterDelivery) {
        return "-";
    }

    return `<button class="register-delivery-btn" type="button" data-order-no="${orderNo}">\ubc30\uc1a1\ud558\uae30</button>`;
}

function formatNumber(value) {
    const number = Number(value || 0);
    return Number.isFinite(number) ? number.toLocaleString("ko-KR") : "0";
}

function formatDate(value) {
    return value ? String(value).substring(0, 10) : "-";
}
