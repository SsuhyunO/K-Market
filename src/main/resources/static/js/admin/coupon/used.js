import { initModals, openModal } from '../../global/modal-form.js';
import { delegate } from '../../global/event-manager.js';

const COUPON_ISSUED_DETAIL_FIELDS = [
    "couponNo",
    "couponType",
    "couponName",
    "benefit",
    "period",
    "memberId",
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

const COUPON_STATUS_LABELS = {
    0: "미사용",
    1: "사용완료",
    2: "기간만료",
    3: "중단"
};

const COUPON_TYPE_LABELS = {
    PRODUCT: "개별상품 할인",
    ORDER: "주문상품 할인",
    DELIVERY: "배송비 무료"
};

function initIssuedCouponDetailModal() {
    const modal = document.getElementById("coupon-issued-detail-modal");
    if (!modal) return;

    delegate(document, "click", "[data-coupon-issued-detail-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        COUPON_ISSUED_DETAIL_FIELDS.forEach(field => {
            let value = row.dataset[field];

            if (field === "couponType") {
                value = COUPON_TYPE_LABELS[value] || value;
            }
            // status는 이미 서버에서 문자열(statusDisplay)로 내려주고 있어서 별도 변환 불필요

            setText(`coupon-issued-detail-${toKebabCase(field)}`, value);
        });

        openModal(modal);
    });
}

function initIssuedCouponStopButtons() {
    delegate(document, "click", "[data-issued-coupon-stop-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        const issueNo = button.dataset.issueNo || "";
        const message = issueNo
            ? `${issueNo} 발급 쿠폰을 중단하시겠습니까?`
            : "발급 쿠폰을 중단하시겠습니까?";

        if (!window.confirm(message)) return;

        fetch(`${CONTEXT_PATH}admin/coupon/issue/${issueNo}/stop`, {
            method: "PATCH"
        })
            .then(res => {
                if (res.status === 403) {
                    alert("중단 권한이 없습니다.");
                    throw new Error("FORBIDDEN");
                }
                if (res.status === 409) {
                    alert("이미 처리된 쿠폰입니다.");
                    throw new Error("CONFLICT");
                }
                if (!res.ok) throw new Error("서버 처리 실패");

                row.dataset.status = "중단";
                row.dataset.usedAt = "-";

                const status = row.querySelector(".coupon-status");
                if (status) {
                    status.textContent = "중단";
                    status.classList.remove("ready", "used", "expired");
                    status.classList.add("disabled");
                }

                button.disabled = true;
            })
            .catch(err => {
                if (err.message !== "FORBIDDEN" && err.message !== "CONFLICT") {
                    alert("쿠폰 중단 처리 중 오류가 발생했습니다.");
                    console.error(err);
                }
            });
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
