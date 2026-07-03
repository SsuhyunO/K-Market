import { initModals, openModal } from '../../global/modal-form.js';
import { delegate } from '../../global/event-manager.js';

const COUPON_ISSUED_DETAIL_FIELDS = [
    "couponNo",
    "couponType",
    "couponName",
    "benefit",
    "period",
    "memberId",
    "memberName",
    "issuedAt",
    "usedAt",
    "status",
    "description"
];

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    initIssuedCouponDetailModal();
    initIssuedCouponStopButtons();
});

function initIssuedCouponDetailModal() {
    const modal = document.getElementById("coupon-issued-detail-modal");
    if (!modal) return;

    delegate(document, "click", "[data-coupon-issued-detail-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        COUPON_ISSUED_DETAIL_FIELDS.forEach(field => {
            setText(`coupon-issued-detail-${toKebabCase(field)}`, row.dataset[field]);
        });

        openModal(modal);
    });
}

function initIssuedCouponStopButtons() {
    delegate(document, "click", "[data-issued-coupon-stop-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        const issuedNo = row.querySelector("[data-coupon-issued-detail-button]")?.textContent?.trim() || "";
        const message = issuedNo
            ? `${issuedNo} 발급 쿠폰을 중단하시겠습니까?`
            : "발급 쿠폰을 중단하시겠습니까?";

        if (!window.confirm(message)) return;

        row.dataset.status = "중단";
        row.dataset.usedAt = "-";

        const status = row.querySelector(".coupon-status");
        if (status) {
            status.textContent = "중단";
            status.classList.remove("ready", "used", "expired");
            status.classList.add("disabled");
        }

        button.disabled = true;
    });
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (!target) return;

    target.textContent = value || "-";
}

function toKebabCase(value) {
    return value.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`);
}
