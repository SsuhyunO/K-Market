import { Validation } from '../global/validation.js';
import { KoreanPostposition } from '../global/korean-postposition.js';
import { FormValidation } from './global/form-validation.js';

document.addEventListener("DOMContentLoaded", function () {
    initSiteSettingsValidation();
});

function initSiteSettingsValidation() {
    document
        .querySelectorAll("#admin-main form")
        .forEach(bindSiteSettingsFormValidation);
}

function bindSiteSettingsFormValidation(form) {
    FormValidation.bind({
        form,
        validate: validateSiteSettingsForm,
        isField: isSiteSettingsField,
        ensureErrors: ensureSiteSettingsFieldErrors
    });
}

function validateSiteSettingsForm(form) {
    return getSiteSettingsFields(form).flatMap(field => validateSiteSettingsFieldValue(field));
}

function validateSiteSettingsFieldValue(field) {
    const label = getFieldLabel(field);
    const errors = [];

    if (field.type === "file") {
        const file = field.files[0];

        if (!file) {
            errors.push({ fieldId: field.id, message: label + KoreanPostposition.object(label) + " 선택해주세요." });
            return errors;
        }

        if (!Validation.imageMimeType(file.type).valid) {
            errors.push({ fieldId: field.id, message: label + KoreanPostposition.topic(label) + " 이미지 파일만 선택할 수 있습니다." });
        }

        return errors;
    }

    if (!Validation.required(field.value).valid) {
        errors.push({ fieldId: field.id, message: label + KoreanPostposition.object(label) + " 입력해주세요." });
        return errors;
    }

    if (field.id === "modify-email" && !Validation.pattern(field.value, /^[^\s@]+@[^\s@]+\.[^\s@]+$/).valid) {
        errors.push({ fieldId: field.id, message: "이메일 형식이 올바르지 않습니다." });
    }

    if ((field.id === "modify-phone" || field.id === "modify-dispute-officer") && !Validation.pattern(field.value, /^[0-9-]+$/).valid) {
        errors.push({ fieldId: field.id, message: label + KoreanPostposition.topic(label) + " 숫자와 하이픈만 입력해주세요." });
    }

    if (field.id === "modify-business-reg-number" && !Validation.pattern(field.value, /^\d{3}-\d{2}-\d{5}$/).valid) {
        errors.push({ fieldId: field.id, message: "사업자등록번호는 000-00-00000 형식으로 입력해주세요." });
    }

    return errors;
}

function getSiteSettingsFields(form) {
    return Array.from(form.querySelectorAll("input[id], textarea[id], select[id]"))
        .filter(field => !field.readOnly && !field.disabled);
}

function isSiteSettingsField(form, target) {
    return target.matches("input[id], textarea[id], select[id]") && form.contains(target) && !target.readOnly && !target.disabled;
}

function ensureSiteSettingsFieldErrors(form) {
    getSiteSettingsFields(form).forEach(field => {
        FormValidation.ensureFieldErrors(form, `#${field.id}`);
    });
}

function getFieldLabel(field) {
    return field.closest("tr")?.querySelector("th label")?.textContent.trim() || "필수 항목";
}
