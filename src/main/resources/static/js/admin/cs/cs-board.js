import { delegate } from '../../global/event-manager.js';
import { initModals } from '../../global/modal-form.js';
import { Validation } from '../../global/validation.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { ManagementTableForm } from '../global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initRecruitRegisterValidation();
    initCsBoardLinks();
    initCsDeleteButtons();
});

function initRecruitRegisterValidation() {
    const form = document.getElementById("recruit-register-form");
    if (!form) return;

    ModalFormValidation.bind({
        form,
        validate: validateRecruitRegisterForm,
        isField: isRecruitRegisterField,
        getRelatedFieldIds: getRecruitRegisterRelatedFieldIds,
        ensureErrors: ensureRecruitRegisterErrors,
        validateOnOpen: true
    });
}

function ensureRecruitRegisterErrors(form) {
    FormValidation.ensureFieldErrors(
        form,
        "#recruit-title, #recruit-department, #recruit-career, #recruit-employment-type, #recruit-start-date, #recruit-end-date, #recruit-note"
    );
}

function validateRecruitRegisterForm(form) {
    const errors = [];

    FormValidation.addRequiredError(form, "recruit-title", "제목을 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-department", "채용부서를 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-career", "경력사항을 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-employment-type", "채용형태를 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-start-date", "모집 시작일을 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-end-date", "모집 종료일을 선택해주세요.", errors);

    const startDate = form.querySelector("#recruit-start-date")?.value || "";
    const endDate = form.querySelector("#recruit-end-date")?.value || "";
    if (startDate !== "" && endDate !== "" && !Validation.greaterThanOrEqual(startDate, endDate).valid) {
        errors.push({
            fieldId: "recruit-end-date",
            message: "모집 종료일은 시작일 이후로 선택해주세요."
        });
    }

    FormValidation.addRequiredError(form, "recruit-note", "비고를 입력해주세요.", errors);

    return errors;
}

function getRecruitRegisterRelatedFieldIds(fieldId) {
    if (fieldId === "recruit-start-date" || fieldId === "recruit-end-date") {
        return ["recruit-start-date", "recruit-end-date"];
    }

    return [fieldId];
}

function isRecruitRegisterField(form, target) {
    return target.matches("input, select, textarea") && form.contains(target);
}

function initCsBoardLinks() {
    delegate(document, "click", "[data-cs-link]", function (e, button) {
        const href = button.dataset.csLink;
        if (!href) return;

        e.preventDefault();
        window.location.href = href;
    });
}

function initCsDeleteButtons() {
    delegate(document, "click", "[data-cs-delete]", function (e, button) {
        const message = button.dataset.confirmMessage || "삭제하시겠습니까?";
        if (window.confirm(message)) return;

        e.preventDefault();
    });
}
