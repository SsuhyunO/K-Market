import { Validation } from '../../global/validation.js';
import { KoreanPostposition } from '../../global/korean-postposition.js';
import { FormValidation } from '../global/form-validation.js';

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
    const fields = getSiteSettingsFields(form);

    // 로고 수정 폼이면 파일 3개 중 최소 1개만 선택하면 통과
    if (isLogoForm(form)) {
        return validateLogoForm(fields);
    }

    // 나머지 일반 폼은 기존처럼 필수값 검사
    return fields.flatMap(field => validateSiteSettingsFieldValue(field));
}

// 로고 수정 폼인지 확인
function isLogoForm(form) {
    return form.querySelector("#modify-header-logo")
        || form.querySelector("#modify-footer-logo")
        || form.querySelector("#modify-favicon");
}

// 로고 폼 검증
function validateLogoForm(fields) {
    const errors = [];

    const fileFields = fields.filter(field => field.type === "file");

    const selectedFiles = fileFields.filter(field => field.files && field.files.length > 0);

    // 3개 중 아무것도 선택 안 했을 때만 막기
    if (selectedFiles.length === 0) {
        errors.push({
            fieldId: fileFields[0]?.id || "modify-header-logo",
            message: "수정할 로고 파일을 최소 1개 이상 선택해주세요."
        });

        return errors;
    }

    // 선택된 파일만 이미지 파일인지 검사
    selectedFiles.forEach(field => {
        const label = getFieldLabel(field);
        const file = field.files[0];

        if (!Validation.imageMimeType(file.type).valid) {
            errors.push({
                fieldId: field.id,
                message: label + KoreanPostposition.topic(label) + " 이미지 파일만 선택할 수 있습니다."
            });
        }
    });

    return errors;
}

function validateSiteSettingsFieldValue(field) {
    const label = getFieldLabel(field);
    const errors = [];

    // 일반 file input 처리
    // 단, 로고 폼은 위 validateLogoForm에서 따로 처리하므로 여기서는 개별 file 필수검사 안 함
    if (field.type === "file") {
        const file = field.files[0];

        if (!file) {
            return errors;
        }

        if (!Validation.imageMimeType(file.type).valid) {
            errors.push({
                fieldId: field.id,
                message: label + KoreanPostposition.topic(label) + " 이미지 파일만 선택할 수 있습니다."
            });
        }

        return errors;
    }

    if (!Validation.required(field.value).valid) {
        errors.push({
            fieldId: field.id,
            message: label + KoreanPostposition.object(label) + " 입력해주세요."
        });
        return errors;
    }

    if (field.id === "modify-email" && !Validation.pattern(field.value, /^[^\s@]+@[^\s@]+\.[^\s@]+$/).valid) {
        errors.push({
            fieldId: field.id,
            message: "이메일 형식이 올바르지 않습니다."
        });
    }

    if ((field.id === "modify-phone" || field.id === "modify-dispute-officer") && !Validation.pattern(field.value, /^[0-9-]+$/).valid) {
        errors.push({
            fieldId: field.id,
            message: label + KoreanPostposition.topic(label) + " 숫자와 하이픈만 입력해주세요."
        });
    }

    if (field.id === "modify-business-reg-number" && !Validation.pattern(field.value, /^\d{3}-\d{2}-\d{5}$/).valid) {
        errors.push({
            fieldId: field.id,
            message: "사업자등록번호는 000-00-00000 형식으로 입력해주세요."
        });
    }

    return errors;
}

function getSiteSettingsFields(form) {
    return Array.from(form.querySelectorAll("input[id], textarea[id], select[id]"))
        .filter(field => !field.readOnly && !field.disabled);
}

function isSiteSettingsField(form, target) {
    return target.matches("input[id], textarea[id], select[id]")
        && form.contains(target)
        && !target.readOnly
        && !target.disabled;
}

function ensureSiteSettingsFieldErrors(form) {
    getSiteSettingsFields(form).forEach(field => {
        FormValidation.ensureFieldErrors(form, `#${field.id}`);
    });
}

function getFieldLabel(field) {
    return field.closest("tr")?.querySelector("th label")?.textContent.trim() || "필수 항목";
}