import { Validation } from '../global/validation.js';
import { KoreanPostposition } from '../global/korean-postposition.js';
import { FormValidation } from './global/form-validation.js';

document.addEventListener("DOMContentLoaded", function () {
    initTermsManagementValidation();
});

function initTermsManagementValidation() {
    document
        .querySelectorAll("#admin-main form")
        .forEach(bindTermsFormValidation);
}

function bindTermsFormValidation(form) {
    FormValidation.bind({
        form,
        validate: validateTermsForm,
        isField: isTermsField,
        ensureErrors: ensureTermsFieldErrors,
    });
}

function validateTermsForm(form) {
    return getTermsFields(form)
        .filter(field => !Validation.required(field.value).valid)
        .map(field => ({
            fieldId: field.id,
            message: getFieldLabel(field) + KoreanPostposition.object(getFieldLabel(field)) + " 입력해주세요."
        }));
}

function getTermsFields(form) {
    return Array.from(form.querySelectorAll("textarea[id]"));
}

function isTermsField(form, target) {
    return target.matches("textarea[id]") && form.contains(target);
}

function ensureTermsFieldErrors(form) {
    FormValidation.ensureFieldErrors(form, "textarea[id]");
}

function getFieldLabel(field) {
    return field.closest("tr")?.querySelector("th label")?.textContent.trim() || "필수 항목";
}
