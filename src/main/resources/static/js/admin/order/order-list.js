import { Validation } from '../../global/validation.js';
import { initModals, openModal } from '../../global/modal-form.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { isDaumPostcodeCanceled, openDaumPostcode } from '../../global/daumpostcode.js';

document.addEventListener('DOMContentLoaded', function() {
    initModals();
    initOrderStatusModal();
    initDeliveryRegisterValidation();
    initDeliveryPostcode();
});

function initOrderStatusModal() {
    const orderModal = document.getElementById("order-detail-modal");
    const deliveryModal = document.getElementById("delivery-register-modal");
    const orderDetailBtns = document.getElementsByClassName('order-detail-btn');
    const registerDeliveryBtns = document.getElementsByClassName('register-delivery-btn');

    if (!orderModal || !deliveryModal || !orderDetailBtns || !registerDeliveryBtns) return;

    [...orderDetailBtns].forEach(btn => btn.addEventListener('click', e => openModal(orderModal)));
    [...registerDeliveryBtns].forEach(btn => btn.addEventListener('click', e => openModal(deliveryModal)));
}

function initDeliveryRegisterValidation() {
    const regForm = document.deliveryRegisterForm;
    if (!regForm) return;

    ModalFormValidation.bind({
        form: regForm,
        validate: validateDeliveryRegisterForm,
        isField: isDeliveryRegisterField,
        ensureErrors: ensureDeliveryRegisterErrors,
        validateOnOpen: true
    });

    function ensureDeliveryRegisterErrors(form) {
        FormValidation.ensureFieldErrors(
            form,
            `#recipient, 
                        #zip-code, 
                        #default-addr, 
                        #detail-addr, 
                        #delivery-company, 
                        #tracking-number`);
    }

    function validateDeliveryRegisterForm(form) {
        const errors = [];

        FormValidation.addRequiredError(form, 'recipient', '수령인을 입력해주세요.', errors);
        FormValidation.addRequiredError(form, 'zip-code', '우편번호를 입력해주세요.', errors);
        FormValidation.addRequiredError(form, 'default-addr', '기본주소를 입력해주세요.', errors);
        FormValidation.addRequiredError(form, 'detail-addr', '상세주소를 입력해주세요.', errors);
        FormValidation.addRequiredError(form, 'delivery-company', '택배사를 선택해주세요.', errors);

        validateTrackingNumber(form, errors);

        return errors;
    }

    function isDeliveryRegisterField(form, target) {
        return target.matches("input, select, textarea") && form.contains(target);
    }

    function validateTrackingNumber(form, errors) {
        const field = form.trackingNumber;
        if (!field) return;

        const value = field.value;

        if (!Validation.required(value).valid) {
            errors.push({
                fieldId: field.id,
                message: '운송장번호를 입력해주세요.'
            });
            return;
        }

        if (!Validation.pattern(value, /^[0-9-]{6,30}$/).valid) {
            errors.push({
                fieldId: field.id,
                message: '운송장번호는 숫자와 하이픈만 입력해주세요.'
            })
        }
    }
}

function initDeliveryPostcode() {
    const form = document.deliveryRegisterForm;
    const zipCodeField = form.zipCode;
    const defaultAddrField = form.defaultAddr;

    if (!form || !zipCodeField || !defaultAddrField) return;

    zipCodeField.addEventListener('click', fetchAddress);
    defaultAddrField.addEventListener('click', fetchAddress);

    async function fetchAddress(){
        try {
            const { zipCode, address } = await openDaumPostcode();

            zipCodeField.value = zipCode;
            defaultAddrField.value = address;

            FormValidation.clearFieldError(form, zipCodeField.id);
            FormValidation.clearFieldError(form, defaultAddrField.id);
        } catch (err) {
            if (isDaumPostcodeCanceled(err)) return;

            console.error(err);
            alert('우편번호 검색을 불러오지 못했습니다.');
        }
    }
}
