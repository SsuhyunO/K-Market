import { initModals } from '../../global/modal-form.js';
import { ManagementTableForm } from '../global/management-table-form.js';
import { Validation } from '../../global/validation.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { isDaumPostcodeCanceled, openDaumPostcode } from '../../global/daumpostcode.js';

const CTX = (document.querySelector('meta[id="ctxPath"]')?.getAttribute('content') || '').replace(/\/$/, '');

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initShopRegisterValidation();
    initShopPostcode();
    initShopRegisterSubmit();
    initShopStatusButtons();
});

function initShopRegisterValidation() {
    const form = document.getElementById("shop-register-form");
    if (!form) return;

    ModalFormValidation.bind({
        form,
        validate: validateShopRegisterForm,
        isField: isShopRegisterField,
        ensureErrors: ensureShopRegisterErrors,
        validateOnOpen: true
    });
}

function ensureShopRegisterErrors(form) {
    FormValidation.ensureFieldErrors(
        form,
        `#shop-register-userid,
        #shop-register-pass,
        #shop-register-business-name,
        #shop-register-ceo,
        #shop-register-business-reg-num,
        #shop-register-mail-order-business-num,
        #shop-register-phone,
        #shop-register-fax,
        #shop-register-zip-code,
        #shop-register-default-address,
        #shop-register-detail-address`
    );
}

function validateShopRegisterForm(form) {
    const errors = [];

    validatePatternField(form, "shop-register-userid", /^[A-Za-z0-9]{4,12}$/, "아이디는 영문, 숫자 4~12자로 입력해주세요.", errors);
    validatePatternField(form, "shop-register-pass", /^(?=.*[A-Za-z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,12}$/, "비밀번호는 영문, 숫자, 특수문자를 포함한 8~12자로 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "shop-register-business-name", "상호명을 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "shop-register-ceo", "대표자명을 입력해주세요.", errors);
    validatePatternField(form, "shop-register-business-reg-num", /^\d{3}-\d{2}-\d{5}$/, "사업자등록번호는 000-00-00000 형식으로 입력해주세요.", errors);
    validatePatternField(form, "shop-register-mail-order-business-num", /^\d{4}-\d{2}-\d{6}$/, "통신판매업번호는 0000-00-000000 형식으로 입력해주세요.", errors);
    validatePatternField(form, "shop-register-phone", /^\d{2,3}-\d{3,4}-\d{4}$/, "전화번호는 하이픈을 포함해 입력해주세요.", errors);
    validatePatternField(form, "shop-register-fax", /^\d{2,3}-\d{3,4}-\d{4}$/, "팩스번호는 하이픈을 포함해 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "shop-register-zip-code", "우편번호를 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "shop-register-default-address", "기본주소를 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "shop-register-detail-address", "상세주소를 입력해주세요.", errors);

    return errors;
}

function validatePatternField(form, fieldId, pattern, message, errors) {
    const field = form.querySelector(`#${fieldId}`);
    if (!field) return;

    if (!Validation.pattern(field.value, pattern).valid) {
        errors.push({ fieldId, message });
    }
}

function isShopRegisterField(form, target) {
    return target.matches("input, select, textarea") && form.contains(target);
}

function initShopPostcode() {
    const searchButton = document.querySelector(".shop-address-search");
    const zipCodeField = document.getElementById("shop-register-zip-code");
    const addressField = document.getElementById("shop-register-default-address");
    const detailAddressField = document.getElementById("shop-register-detail-address");

    if (!searchButton || !zipCodeField || !addressField) return;

    searchButton.addEventListener("click", async function () {
        try {
            const { zipCode, address } = await openDaumPostcode();
            zipCodeField.value = zipCode;
            addressField.value = address;
            FormValidation.clearFieldError(document.getElementById("shop-register-form"), zipCodeField.id);
            FormValidation.clearFieldError(document.getElementById("shop-register-form"), addressField.id);
            detailAddressField?.focus();
        } catch (error) {
            if (isDaumPostcodeCanceled(error)) return;

            console.error(error);
            alert("우편번호 서비스를 불러오지 못했습니다.");
        }
    });
}

// 상점등록 폼 제출 -> API 호출 -> 성공 시 즉시 목록 새로고침
function initShopRegisterSubmit() {
    const form = document.getElementById("shop-register-form");
    if (!form) return;

    form.addEventListener("submit", async function (e) {
        const errors = validateShopRegisterForm(form);
        if (errors.length > 0) return; // FormValidation.bind가 이미 막고 메시지 표시함

        e.preventDefault();

        const payload = {
            userId: form.querySelector("#shop-register-userid").value,
            password: form.querySelector("#shop-register-pass").value,
            businessName: form.querySelector("#shop-register-business-name").value,
            ceo: form.querySelector("#shop-register-ceo").value,
            businessRegistrationNumber: form.querySelector("#shop-register-business-reg-num").value,
            mailOrderBusinessNumber: form.querySelector("#shop-register-mail-order-business-num").value,
            phone: form.querySelector("#shop-register-phone").value,
            fax: form.querySelector("#shop-register-fax").value,
            zipCode: form.querySelector("#shop-register-zip-code").value,
            defaultAddress: form.querySelector("#shop-register-default-address").value,
            detailAddress: form.querySelector("#shop-register-detail-address").value
        };

        try {
            const response = await fetch(`${CTX}/api/admin/shop`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const message = await response.text();
                alert(message || "상점 등록에 실패했습니다.");
                return;
            }

            alert("상점이 등록되었습니다.");
            location.reload();
        } catch (error) {
            console.error(error);
            alert("상점 등록 중 오류가 발생했습니다.");
        }
    });
}

// 관리 버튼 클릭 -> 모달에 대상 상점 표시
document.addEventListener("click", function (e) {
    const manageBtn = e.target.closest(".shop-manage-btn");
    if (!manageBtn) return;

    const modal = document.getElementById("shop-status-modal");
    if (!modal) return;

    modal.dataset.targetUid = manageBtn.dataset.uid;
    const nameEl = document.getElementById("shop-status-modal-name");
    if (nameEl) nameEl.textContent = manageBtn.dataset.name || "";
});

// 모달 안 승인/중단/재개 버튼 클릭 -> 즉시 DB 반영 후 새로고침
function initShopStatusButtons() {
    document.addEventListener("click", async function (e) {
        const button = e.target.closest(".shop-status-choice");
        if (!button) return;

        const modal = document.getElementById("shop-status-modal");
        const uid = modal?.dataset.targetUid;
        const status = button.dataset.status;
        if (!uid || !status) return;

        try {
            const response = await fetch(`${CTX}/api/admin/shop/${uid}/status`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ status })
            });

            if (!response.ok) {
                const message = await response.text();
                alert(message || "상태 변경에 실패했습니다.");
                return;
            }

            location.reload();
        } catch (error) {
            console.error(error);
            alert("상태 변경 중 오류가 발생했습니다.");
        }
    });
}