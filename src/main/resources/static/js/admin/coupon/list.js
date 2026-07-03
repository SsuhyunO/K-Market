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
        "#coupon-register-issuer, #coupon-register-type, #coupon-register-name, #coupon-register-description"
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
    const hasDateRange = startDate !== "" && endDate !== "";
    const hasValidDays = validDays !== "";

    if (!hasDateRange && !hasValidDays) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "사용기간 또는 발급일 기준 사용일수를 입력해주세요."
        });
        return;
    }

    if (hasDateRange && !Validation.greaterThanOrEqual(startDate, endDate).valid) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "사용기간 종료일은 시작일 이후로 선택해주세요."
        });
    }

    if (hasValidDays && !Validation.pattern(validDays, /^[1-9]\d*$/).valid) {
        errors.push({
            fieldId: "coupon-register-period",
            message: "발급일 기준 사용일수는 1 이상의 숫자로 입력해주세요."
        });
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
            setText(`coupon-detail-${toKebabCase(field)}`, row.dataset[field]);
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

        row.dataset.status = "종료";
        const status = row.querySelector(".coupon-status");
        if (status) {
            status.textContent = "종료";
            status.classList.remove("active");
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
