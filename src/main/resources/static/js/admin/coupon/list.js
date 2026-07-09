import { initModals, openModal } from '../../global/modal-form.js';
import { delegate } from '../../global/event-manager.js';
import { Validation } from '../../global/validation.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';

const COUPON_DETAIL_FIELDS = [
    "couponNo",
    "couponType",
    "couponName",
    "benefit",
    "period",
    "issuer",
    "issueCount",
    "usedCount",
    "status",
    "description"
];

const COUPON_TYPE_LABELS = {
    PRODUCT: "개별상품 할인",
    ORDER: "주문상품 할인",
    DELIVERY: "배송비 무료"
};

const COUPON_STATUS_LABELS = {
    ACTIVE: "발급중",
    FORCE_ENDED: "강제종료",
    EXPIRED: "기간만료"
};

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    initCouponRegisterValidation();
    initCouponDetailModal();
    initCouponEndButtons();
});

function initCouponRegisterValidation() {
    const form = document.getElementById("coupon-register-form");
    if (!form) return;

    ModalFormValidation.bind({
        form,
        validate: validateCouponRegisterForm,
        isField: isCouponRegisterField,
        getRelatedFieldIds: getCouponRegisterRelatedFieldIds,
        ensureErrors: ensureCouponRegisterErrors,
        validateOnOpen: true
    });

    form.addEventListener("change", function (e) {
        if (e.target.matches('input[name="benefit"]')) {
            FormValidation.clearFieldError(form, "coupon-register-benefit");
        }

        if (e.target.matches("#coupon-register-start-date, #coupon-register-end-date, #coupon-register-valid-days")) {
            FormValidation.clearFieldError(form, "coupon-register-period");
        }
    });
}

function ensureCouponRegisterErrors(form) {
    FormValidation.ensureFieldErrors(
        form,
        "#coupon-register-issuer, #coupon-register-type, #coupon-register-name, #coupon-register-description, #coupon-register-issued-cnt"
    );
    ensureGroupError(form, ".coupon-benefit-options", "coupon-register-benefit");
    ensureGroupError(form, ".coupon-period-fields:last-of-type", "coupon-register-period");
}

function ensureGroupError(form, selector, fieldId) {
    if (form.querySelector(`[data-error-for="${fieldId}"]`)) return;

    const target = form.querySelector(selector);
    if (!target) return;

    const error = document.createElement("p");
    error.className = "field-error";
    error.dataset.errorFor = fieldId;
    target.insertAdjacentElement("afterend", error);
}

function validateCouponRegisterForm(form) {
    const errors = [];

    FormValidation.addRequiredError(form, "coupon-register-issuer", "발급처를 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "coupon-register-type", "쿠폰종류를 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "coupon-register-name", "쿠폰명을 입력해주세요.", errors);

    // 💡 [추가] 총 발급수 검증 로직 작성
    // 1. 필수 값 체크
    FormValidation.addRequiredError(form, "coupon-register-issued-cnt", "총 발급수를 입력해주세요.", errors);

    // 2. 포맷 체크 (값이 비어있지 않을 때만 1 이상의 올바른 숫자인지 검증)
    const issuedCnt = form.querySelector("#coupon-register-issued-cnt")?.value || "";
    if (issuedCnt !== "") {
        if (!Validation.pattern(issuedCnt, /^[1-9]\d*$/).valid) {
            errors.push({
                fieldId: "coupon-register-issued-cnt",
                message: "총 발급수는 1 이상의 숫자로 입력해주세요."
            });
        }
    }

    validateCouponBenefit(form, errors);
    validateCouponPeriod(form, errors);
    FormValidation.addRequiredError(form, "coupon-register-description", "유의사항을 입력해주세요.", errors);

    return errors;
}

function validateCouponBenefit(form, errors) {
    if (form.querySelector('input[name="benefit"]:checked')) return;

    errors.push({
        fieldId: "coupon-register-benefit",
        message: "혜택을 선택해주세요."
    });
}

function validateCouponPeriod(form, errors) {
    const startDate = form.querySelector("#coupon-register-start-date")?.value || "";
    const endDate = form.querySelector("#coupon-register-end-date")?.value || "";
    const validDays = form.querySelector("#coupon-register-valid-days")?.value || "";

    // 💡 함수가 실행되는 시점(버튼 클릭 시점)의 오늘 날짜 구하기 (YYYY-MM-DD 포맷)
    const kstToday = new Date(new Date().getTime() - (new Date().getTimezoneOffset() * 60000));
    const todayStr = kstToday.toISOString().split('T')[0];

    const hasStartDate = startDate !== "";
    const hasEndDate = endDate !== "";
    const hasValidDays = validDays !== "";

    // 1. 시작일은 무조건 선택해야 함
    if (!hasStartDate) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "사용 시작일을 선택해주세요."
        });
        return;
    }

    // 2. [추가 묶음] 시작일이 오늘보다 과거인지 검증
    if (startDate < todayStr) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "시작일은 오늘 또는 오늘 이후 날짜만 선택할 수 있습니다."
        });
        return;
    }

    // 3. [핵심 조건] 종료일과 사용일수가 모두 입력된 경우 (상호 배타적 에러 처리)
    if (hasEndDate && hasValidDays) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "종료일 지정과 사용일수 입력 중 하나만 설정할 수 있습니다."
        });
        return;
    }

    // 4. 종료일과 사용일수가 둘 다 비어 있는 경우
    if (!hasEndDate && !hasValidDays) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "종료일을 지정하거나 발급일 기준 사용일수를 입력해주세요."
        });
        return;
    }

    // 5. 종료일만 정상적으로 입력되었을 때 -> 날짜 선후 관계 검증
    if (hasEndDate) {
        if (!Validation.greaterThanOrEqual(startDate, endDate).valid) {
            errors.push({
                fieldId: "coupon-register-period",
                message: "사용기간 종료일은 시작일 이후로 선택해주세요."
            });
        }
    }

    // 6. 사용일수만 정상적으로 입력되었을 때 -> 1 이상의 숫자 포맷 검증
    if (hasValidDays) {
        if (!Validation.pattern(validDays, /^[1-9]\d*$/).valid) {
            errors.push({
                fieldId: "coupon-register-period",
                message: "발급일 기준 사용일수는 1 이상의 숫자로 입력해주세요."
            });
        }
    }
}

function getCouponRegisterRelatedFieldIds(fieldId) {
    if (fieldId === "coupon-register-start-date"
        || fieldId === "coupon-register-end-date"
        || fieldId === "coupon-register-valid-days") {
        return ["coupon-register-period"];
    }

    if (fieldId === "") return ["coupon-register-benefit"];

    return [fieldId];
}

function isCouponRegisterField(form, target) {
    return target.matches("input, select, textarea") && form.contains(target);
}

function initCouponDetailModal() {
    const modal = document.getElementById("coupon-detail-modal");
    if (!modal) return;

    delegate(document, "click", "[data-coupon-detail-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        COUPON_DETAIL_FIELDS.forEach(field => {
            let value = row.dataset[field];

            if (field === "couponType") {
                value = COUPON_TYPE_LABELS[value] || value;
            } else if (field === "status") {
                value = COUPON_STATUS_LABELS[value] || value;
            }

            setText(`coupon-detail-${toKebabCase(field)}`, value);
        });

        openModal(modal);
    });
}

function initCouponEndButtons() {
    delegate(document, "click", "[data-coupon-end-button]", function (e, button) {
        const row = button.closest("tr");
        if (!row) return;

        const couponNo = row.dataset.couponNo || "";
        const message = couponNo
            ? `${couponNo} 쿠폰 발급을 종료하시겠습니까?`
            : "쿠폰 발급을 종료하시겠습니까?";

        if (!window.confirm(message)) return;

        fetch(`${CONTEXT_PATH}admin/coupon/${couponNo}/end`, {
            method: "PATCH"
        })
            .then(res => {
                if (res.status === 403) {
                    alert("종료 권한이 없습니다.");
                    throw new Error("FORBIDDEN"); // catch로 넘겨서 아래 공통 alert는 안 뜨게
                }
                if (!res.ok) throw new Error("서버 처리 실패");

                row.dataset.status = "FORCE_ENDED";

                const status = row.querySelector(".coupon-status");
                if (status) {
                    status.textContent = "강제종료";
                    status.classList.remove("active");
                    status.classList.add("disabled");
                }

                button.disabled = true;
            })
            .catch(err => {
                if (err.message !== "FORBIDDEN") {
                    alert("쿠폰 종료 처리 중 오류가 발생했습니다.");
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
