import { Validation } from '../../global/validation.js';

export const FormValidation = (() => {
    function bind(options) {
        const form = options.form;
        if (!form) return;

        if (options.ensureErrors) options.ensureErrors(form);
        bindModalState(form, options);
        bindSubmitValidation(form, options);
        bindLiveValidation(form, options);
    }

    function bindModalState(form, options) {
        const modal = form.closest("[data-modal]");
        if (!modal) return;

        modal.addEventListener("modal:open", function () {
            resetForm(form);

            if (options.validateOnOpen) {
                renderFieldErrors(form, options.validate(form));
            }
        });

        modal.addEventListener("modal:close", function () {
            resetForm(form);
        });
    }

    function bindSubmitValidation(form, options) {
        form.addEventListener("submit", function (e) {
            const errors = options.validate(form);

            clearFieldErrors(form);

            if (errors.length <= 0) return;

            e.preventDefault();
            renderFieldErrors(form, errors);
            focusFirstInvalidField(form, errors);
            alert(errors.map(error => error.message).join("\n"));
        });
    }

    function bindLiveValidation(form, options) {
        form.addEventListener("input", function (e) {
            if (!options.isField(form, e.target)) return;
            clearFieldError(form, e.target.id);
        });

        form.addEventListener("change", function (e) {
            if (!options.isField(form, e.target)) return;
            clearFieldError(form, e.target.id);
        });

        form.addEventListener("focusout", function (e) {
            if (!options.isField(form, e.target)) return;
            validateTouchedField(form, e.target.id, options);
        });
    }

    function validateTouchedField(form, fieldId, options) {
        const fieldIds = options.getRelatedFieldIds
            ? options.getRelatedFieldIds(fieldId)
            : [fieldId];
        const errors = options.validate(form)
            .filter(error => fieldIds.includes(error.fieldId));

        fieldIds.forEach(id => clearFieldError(form, id));
        renderFieldErrors(form, errors);
    }

    function resetForm(form) {
        form.reset();
        clearFieldErrors(form);
    }

    function addRequiredError(form, fieldId, message, errors) {
        const field = form.querySelector(`#${fieldId}`);
        if (!field || Validation.required(field.value).valid) return;

        errors.push({ fieldId, message });
    }

    function ensureFieldErrors(form, fieldSelector) {
        form.querySelectorAll(fieldSelector).forEach(field => {
            if (form.querySelector(`[data-error-for="${field.id}"]`)) return;

            const error = document.createElement("p");
            error.className = "field-error";
            error.dataset.errorFor = field.id;
            field.insertAdjacentElement("afterend", error);
        });
    }

    function clearFieldErrors(form) {
        form.querySelectorAll(".field-error").forEach(error => {
            error.textContent = "";
        });

        form.querySelectorAll(".is-invalid").forEach(field => {
            field.classList.remove("is-invalid");
        });
    }

    function clearFieldError(form, fieldId) {
        const error = form.querySelector(`[data-error-for="${fieldId}"]`);
        const field = form.querySelector(`#${fieldId}`);

        if (error) error.textContent = "";
        if (field) field.classList.remove("is-invalid");
    }

    function renderFieldErrors(form, errors) {
        errors.forEach(error => {
            const field = form.querySelector(`#${error.fieldId}`);
            const errorText = form.querySelector(`[data-error-for="${error.fieldId}"]`);

            if (field) field.classList.add("is-invalid");
            if (errorText) errorText.textContent = error.message;
        });
    }

    function focusFirstInvalidField(form, errors) {
        const firstField = form.querySelector(`#${errors[0].fieldId}`);
        if (firstField) firstField.focus();
    }

    return {
        bind,
        addRequiredError,
        ensureFieldErrors,
        clearFieldErrors,
        clearFieldError,
        renderFieldErrors,
        focusFirstInvalidField
    };
})();
