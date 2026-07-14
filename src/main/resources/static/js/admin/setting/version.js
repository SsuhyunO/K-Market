import { initModals } from '../../global/modal-form.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { ManagementTableForm } from '../global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initVersionRegisterValidation();
    initVersionEditModal();
    initVersionEditValidation();
    initVersionDetailModal();
    notifyVersionUpdated();
});

// 수정 완료 후 리다이렉트된 경우(updated=true) alert 표시 후 URL 정리
function notifyVersionUpdated() {
    const params = new URLSearchParams(window.location.search);
    if (params.get("updated") !== "true") return;

    alert("수정되었습니다.");

    params.delete("updated");
    const query = params.toString();
    const newUrl = window.location.pathname + (query ? `?${query}` : "");
    window.history.replaceState({}, "", newUrl);
}

function initVersionRegisterValidation() {
    const registerForm = document.getElementById("version-register-form");
    if (!registerForm) return;

    ModalFormValidation.bind({
        form: registerForm,
        validate: validateVersionRegisterForm,
        isField: isVersionRegisterField,
        validateOnOpen: true
    });
}

function validateVersionRegisterForm(registerForm) {
    const errors = [];

    FormValidation.addRequiredError(registerForm, "version-name", "버전을 입력해주세요.", errors);
    FormValidation.addRequiredError(registerForm, "version-change", "변경내역을 입력해주세요.", errors);

    return errors;
}

function isVersionRegisterField(form, target) {
    return target.matches("input, textarea") && form.contains(target);
}

// 목록의 [수정] 버튼을 누르면 해당 버전의 데이터로 수정 모달 채우기
function initVersionEditModal() {
    const idEl = document.getElementById("edit-version-id");
    const nameEl = document.getElementById("edit-version-name");
    const changeEl = document.getElementById("edit-version-change");
    if (!idEl || !nameEl || !changeEl) return;

    document.addEventListener("click", function (e) {
        const button = e.target.closest('[data-modal-target="version-edit-modal"]');
        if (!button) return;

        idEl.value = button.dataset.id || "";
        nameEl.value = button.dataset.version || "";
        changeEl.value = button.dataset.content || "";
    });
}

function initVersionEditValidation() {
    const editForm = document.getElementById("version-edit-form");
    if (!editForm) return;

    ModalFormValidation.bind({
        form: editForm,
        validate: validateVersionEditForm,
        isField: isVersionEditField,
        validateOnOpen: true
    });
}

function validateVersionEditForm(editForm) {
    const errors = [];

    FormValidation.addRequiredError(editForm, "edit-version-name", "버전을 입력해주세요.", errors);
    FormValidation.addRequiredError(editForm, "edit-version-change", "변경내역을 입력해주세요.", errors);

    return errors;
}

function isVersionEditField(form, target) {
    return target.matches("input, textarea") && form.contains(target);
}

// 목록의 [확인] 버튼을 누르면 해당 버전의 데이터로 상세 모달 채우기
function initVersionDetailModal() {
    const nameEl = document.getElementById("version-detail-name");
    const listEl = document.getElementById("version-detail-change");
    if (!nameEl || !listEl) return;

    document.addEventListener("click", function (e) {
        const button = e.target.closest(".version-detail-button");
        if (!button) return;
        if (button.dataset.modalTarget !== "version-detail-modal") return;

        nameEl.textContent = button.dataset.version || "";

        const content = button.dataset.content || "";
        const lines = content.split(/\r?\n/).map(l => l.trim()).filter(l => l.length > 0);

        listEl.innerHTML = "";
        lines.forEach(line => {
            const li = document.createElement("li");
            li.textContent = line;
            listEl.appendChild(li);
        });
    });
}