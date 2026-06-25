import { Validation } from '../global/validation.js';
import { initModals } from '../global/modal-form.js';
import { FormValidation } from './global/form-validation.js';
import { ManagementTableForm } from './global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initBannerRegisterValidation();
});

function initBannerRegisterValidation() {
    const registerForm = document.getElementById("banner-register-form");
    if (!registerForm) return;

    FormValidation.bind({
        form: registerForm,
        validate: validateBannerRegisterForm,
        isField: isBannerRegisterField,
        getRelatedFieldIds: getBannerRegisterRelatedFieldIds,
        validateOnOpen: true
    });
}

function getBannerRegisterRelatedFieldIds(fieldId) {
    if (fieldId === "banner-start-date" || fieldId === "banner-end-date") {
        return ["banner-start-date", "banner-end-date"];
    }

    if (fieldId === "banner-start-time" || fieldId === "banner-end-time") {
        return ["banner-start-time", "banner-end-time"];
    }

    return [fieldId];
}

function validateBannerRegisterForm(registerForm) {
    const errors = [];

    FormValidation.addRequiredError(registerForm, "banner-name", "배너명을 입력해주세요.", errors);
    validateBannerSize(registerForm, errors);
    validateBackgroundColor(registerForm, errors);
    validateBannerLink(registerForm, errors);
    FormValidation.addRequiredError(registerForm, "banner-position", "배너위치를 선택해주세요.", errors);
    validateBannerDateRange(registerForm, errors);
    validateBannerTimeRange(registerForm, errors);
    validateBannerFile(registerForm, errors);

    return errors;
}

function validateBannerSize(form, errors) {
    const field = form.querySelector("#banner-size");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "배너크기를 입력해주세요." });
        return;
    }

    if (!Validation.pattern(value, /^\d+x\d+$/i).valid) {
        errors.push({ fieldId: field.id, message: "배너크기는 1200x80 형식으로 입력해주세요." });
    }
}

function validateBackgroundColor(form, errors) {
    const field = form.querySelector("#banner-background-color");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "배경색을 입력해주세요." });
        return;
    }

    if (!Validation.pattern(value, /^#([0-9a-f]{3}|[0-9a-f]{6})$/i).valid) {
        errors.push({ fieldId: field.id, message: "배경색은 #ffffff 형식으로 입력해주세요." });
    }
}

function validateBannerLink(form, errors) {
    const field = form.querySelector("#banner-link");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "배너링크를 입력해주세요." });
        return;
    }

    if (!Validation.url(value).valid) {
        errors.push({ fieldId: field.id, message: "배너링크는 올바른 URL 형식으로 입력해주세요." });
    }
}

function validateBannerDateRange(form, errors) {
    const startDate = form.querySelector("#banner-start-date");
    const endDate = form.querySelector("#banner-end-date");
    if (!startDate || !endDate) return;

    if (!Validation.required(startDate.value).valid) {
        errors.push({ fieldId: startDate.id, message: "노출 시작일을 선택해주세요." });
    }

    if (!Validation.required(endDate.value).valid) {
        errors.push({ fieldId: endDate.id, message: "노출 종료일을 선택해주세요." });
    }

    if (startDate.value !== "" && endDate.value !== "" && !Validation.greaterThanOrEqual(startDate.value, endDate.value).valid) {
        errors.push({ fieldId: endDate.id, message: "노출 종료일은 시작일 이후로 선택해주세요." });
    }
}

function validateBannerTimeRange(form, errors) {
    const startTime = form.querySelector("#banner-start-time");
    const endTime = form.querySelector("#banner-end-time");
    if (!startTime || !endTime) return;

    if (!Validation.required(startTime.value).valid) {
        errors.push({ fieldId: startTime.id, message: "노출 시작시간을 선택해주세요." });
    }

    if (!Validation.required(endTime.value).valid) {
        errors.push({ fieldId: endTime.id, message: "노출 종료시간을 선택해주세요." });
    }

    if (startTime.value !== "" && endTime.value !== "" && !Validation.greaterThan(startTime.value, endTime.value).valid) {
        errors.push({ fieldId: endTime.id, message: "노출 종료시간은 시작시간 이후로 선택해주세요." });
    }
}

function validateBannerFile(form, errors) {
    const field = form.querySelector("#banner-file");
    if (!field) return;

    const file = field.files[0];
    if (!file) {
        errors.push({ fieldId: field.id, message: "배너이미지를 선택해주세요." });
        return;
    }

    if (!Validation.imageMimeType(file.type).valid) {
        errors.push({ fieldId: field.id, message: "배너이미지는 이미지 파일만 등록할 수 있습니다." });
    }
}

function isBannerRegisterField(form, target) {
    return target.matches("input, select") && form.contains(target);
}
