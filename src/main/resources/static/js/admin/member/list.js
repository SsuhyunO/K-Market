import { Validation } from '../../global/validation.js';
import { initModals, openModal, closeModal } from '../../global/modal-form.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { isDaumPostcodeCanceled, openDaumPostcode } from '../../global/daumpostcode.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    initMemberEditModal();
    initMemberEditValidation();
    initMemberPostcode();
    initMemberEditSubmit();   // 추가: 수정하기 버튼 -> 서버 반영
    initMemberGradeSelect();  // 추가: 등급 select -> 즉시 서버 반영
});

function initMemberPostcode() {
    const searchButton = document.querySelector(".member-address-search");
    const zipCodeField = document.getElementById("member-edit-zip-code");
    const addressField = document.getElementById("member-edit-address");
    const detailAddressField = document.getElementById("member-edit-detail-address");

    if (!searchButton || !zipCodeField || !addressField) return;

    searchButton.addEventListener("click", async function () {
        try {
            const { zipCode, address } = await openDaumPostcode();
            zipCodeField.value = zipCode;
            addressField.value = address;
            detailAddressField?.focus();
        } catch (error) {
            if (isDaumPostcodeCanceled(error)) return;

            console.error(error);
            alert("우편번호 서비스를 불러오지 못했습니다.");
        }
    });
}

function initMemberEditModal() {
    const form = document.getElementById("management-table-form");
    const selectAll = document.getElementById("select-all");
    const editButton = document.querySelector('button[data-management-action="bulk-edit"]');
    const modal = document.getElementById("member-edit-modal");
    const targetSelect = document.getElementById("member-edit-target-select");

    if (!form || !editButton || !modal || !targetSelect) return;

    bindCheckboxGroup(form, selectAll);

    editButton.addEventListener("click", function (e) {
        e.preventDefault();

        const selectedMembers = getSelectedMembers(form);
        if (selectedMembers.length === 0) {
            alert(editButton.dataset.emptyMessage || "수정할 회원을 선택해주세요.");
            return;
        }

        openModal(modal);
        renderTargetOptions(targetSelect, selectedMembers);
        fillEditForm(selectedMembers[0]);
        renderMemberEditErrors();
    });

    form.addEventListener("click", function (e) {
        const actionButton = e.target.closest(".member-action-button");
        if (!actionButton) return;

        const checkbox = actionButton.closest("tr")?.querySelector('input[name="memberNo"]');
        if (!checkbox) return;

        getMemberCheckboxes(form).forEach(item => {
            item.checked = item === checkbox;
        });
        syncSelectAllState(form, selectAll);

        const member = getMemberFromCheckbox(checkbox);
        openModal(modal);
        renderTargetOptions(targetSelect, [member]);
        fillEditForm(member);
        renderMemberEditErrors();
    });

    targetSelect.addEventListener("change", function () {
        const selectedMembers = getSelectedMembers(form);
        const member = selectedMembers.find(item => item.no === targetSelect.value);
        if (!member) return;

        fillEditForm(member);
        renderMemberEditErrors();
    });
}

// ===== 추가: 수정하기 버튼 클릭 시 서버로 전송 =====
function initMemberEditSubmit() {
    const editForm = document.getElementById("member-edit-form");
    if (!editForm) return;

    editForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const errors = validateMemberEditForm(editForm);
        if (errors.length > 0) {
            renderMemberEditErrors();
            return;
        }

        const checkedGender = editForm.querySelector('input[name="gender"]:checked');

        const payload = {
            uid: document.getElementById("member-edit-user-id").value,
            name: document.getElementById("member-edit-name").value,
            gender: checkedGender ? checkedGender.value : null,
            email: document.getElementById("member-edit-email").value,
            phone: document.getElementById("member-edit-phone").value,
            zipCode: document.getElementById("member-edit-zip-code").value,
            address: document.getElementById("member-edit-address").value,
            detailAddress: document.getElementById("member-edit-detail-address").value,
            note: document.getElementById("member-edit-note").value
        };

        try {
            const response = await fetch("member-update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                throw new Error("수정 요청 실패");
            }

            alert("회원 정보가 수정되었습니다.");
            location.reload(); // 목록 다시 불러오기 (DB 최신값 반영)
        } catch (error) {
            console.error(error);
            alert("수정 중 오류가 발생했습니다.");
        }
    });
}

// ===== 추가: 등급 select 변경 시 즉시 서버 반영 =====
function initMemberGradeSelect() {
    document.addEventListener("change", async function (e) {
        const select = e.target.closest(".member-grade-select");
        if (!select) return;

        const uid = select.dataset.uid;
        const memberLevel = Number(select.value);

        try {
            const response = await fetch("member-grade-update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ uid, memberLevel })
            });

            if (!response.ok) {
                throw new Error("등급 변경 실패");
            }
            // 성공 시 별도 알림 없이 조용히 반영 (필요하면 alert 추가 가능)
        } catch (error) {
            console.error(error);
            alert("등급 변경 중 오류가 발생했습니다.");
        }
    });
}

function initMemberEditValidation() {
    const registerForm = document.getElementById("member-edit-form");
    if (!registerForm) return;

    ModalFormValidation.bind({
        form: registerForm,
        validate: validateMemberEditForm,
        isField: isMemberEditField,
        getRelatedFieldIds: getMemberEditRelatedFieldIds,
        ensureErrors: ensureMemberEditErrors,
        validateOnOpen: true
    });
}

function ensureMemberEditErrors(form) {
    FormValidation.ensureFieldErrors(
        form,
        "#member-edit-target-select, #member-edit-name, #member-edit-email, #member-edit-phone, #member-edit-detail-address"
    );
}

function getMemberEditRelatedFieldIds(fieldId) {
    if (fieldId === "member-edit-gender-m" || fieldId === "member-edit-gender-f") {
        return ["member-edit-gender"];
    }

    return [fieldId];
}

function validateMemberEditForm(form) {
    const errors = [];

    FormValidation.addRequiredError(form, "member-edit-target-select", "수정 대상을 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "member-edit-name", "이름을 입력해주세요.", errors);
    validateGender(form, errors);
    validateEmail(form, errors);
    validatePhone(form, errors);
    FormValidation.addRequiredError(form, "member-edit-detail-address", "상세주소를 입력해주세요.", errors);

    return errors;
}

function validateGender(form, errors) {
    const checkedGender = form.querySelector('input[name="gender"]:checked');
    if (checkedGender) return;

    errors.push({
        fieldId: "member-edit-gender",
        message: "성별을 선택해주세요."
    });
}

function validateEmail(form, errors) {
    const field = form.querySelector("#member-edit-email");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "이메일을 입력해주세요." });
        return;
    }

    if (!Validation.pattern(value, /^[^\s@]+@[^\s@]+\.[^\s@]+$/).valid) {
        errors.push({ fieldId: field.id, message: "이메일 형식이 올바르지 않습니다." });
    }
}

function validatePhone(form, errors) {
    const field = form.querySelector("#member-edit-phone");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "휴대폰 번호를 입력해주세요." });
        return;
    }

    if (!Validation.pattern(value, /^01[016789]-?\d{3,4}-?\d{4}$/).valid) {
        errors.push({ fieldId: field.id, message: "휴대폰 번호 형식이 올바르지 않습니다." });
    }
}

function isMemberEditField(form, target) {
    return target.matches("input, select, textarea") && form.contains(target);
}

function renderMemberEditErrors() {
    const form = document.getElementById("member-edit-form");
    if (!form) return;

    FormValidation.clearFieldErrors(form);
    FormValidation.renderFieldErrors(form, validateMemberEditForm(form));
}

function bindCheckboxGroup(form, selectAll) {
    if (!form || !selectAll) return;

    selectAll.addEventListener("change", function () {
        getMemberCheckboxes(form).forEach(checkbox => {
            checkbox.checked = selectAll.checked;
        });

        syncSelectAllState(form, selectAll);
    });

    form.addEventListener("change", function (e) {
        if (!e.target.matches('input[name="memberNo"]')) return;
        syncSelectAllState(form, selectAll);
    });

    syncSelectAllState(form, selectAll);
}

function renderTargetOptions(targetSelect, members) {
    targetSelect.innerHTML = "";

    members.forEach(member => {
        const option = document.createElement("option");
        option.value = member.no;
        option.textContent = `${member.no} / ${member.userId} / ${member.name}`;
        targetSelect.append(option);
    });
}

function fillEditForm(member) {
    setValue("member-edit-user-id", member.userId);
    setValue("member-edit-name", member.name);
    setValue("member-edit-grade", member.grade);
    setValue("member-edit-status", member.status);
    setValue("member-edit-email", member.email);
    setValue("member-edit-phone", member.phone);
    setValue("member-edit-zip-code", member.zipCode);
    setValue("member-edit-address", member.address);
    setValue("member-edit-detail-address", member.detailAddress);
    setValue("member-edit-created-at", member.createdAt);
    setValue("member-edit-last-login-at", member.lastLoginAt);
    setValue("member-edit-note", member.note);

    const genderField = document.querySelector(`input[name="gender"][value="${member.gender}"]`);
    if (genderField) genderField.checked = true;
}

function setValue(id, value) {
    const field = document.getElementById(id);
    if (!field) return;

    field.value = value || "";
}

function getSelectedMembers(form) {
    return getMemberCheckboxes(form)
        .filter(checkbox => checkbox.checked)
        .map(getMemberFromCheckbox);
}

function getMemberCheckboxes(form) {
    return Array.from(form.querySelectorAll('input[name="memberNo"]'));
}

function getMemberFromCheckbox(checkbox) {
    return {
        no: checkbox.value,
        userId: checkbox.dataset.userId || "",
        name: checkbox.dataset.name || "",
        gender: checkbox.dataset.gender || "",
        grade: checkbox.dataset.grade || "",
        status: checkbox.dataset.status || "",
        email: checkbox.dataset.email || "",
        phone: checkbox.dataset.phone || "",
        zipCode: checkbox.dataset.zipCode || "",
        address: checkbox.dataset.address || "",
        detailAddress: checkbox.dataset.detailAddress || "",
        createdAt: checkbox.dataset.createdAt || "",
        lastLoginAt: checkbox.dataset.lastLoginAt || "",
        note: checkbox.dataset.note || ""
    };
}

function syncSelectAllState(form, selectAll) {
    const checkboxes = getMemberCheckboxes(form);
    const checkedCount = checkboxes.filter(checkbox => checkbox.checked).length;

    selectAll.checked = checkboxes.length > 0 && checkedCount === checkboxes.length;
    selectAll.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
}