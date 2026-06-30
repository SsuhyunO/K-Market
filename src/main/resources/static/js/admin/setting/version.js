import { initModals } from '../../global/modal-form.js';
import { FormValidation } from '../global/form-validation.js';
import { ManagementTableForm } from '../global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initVersionRegisterValidation();
});

function initVersionRegisterValidation() {
    const registerForm = document.getElementById("version-register-form");
    if (!registerForm) return;

    FormValidation.bind({
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
