import { FormValidation } from './form-validation.js';

export const ModalFormValidation = (() => {
    function bind(options) {
        const form = options.form;
        if (!form) return;

        FormValidation.bind(options);
        bindModalState(form, options);
    }

    function bindModalState(form, options) {
        const modal = form.closest("[data-modal]");
        if (!modal) return;

        modal.addEventListener("modal:open", function () {
            FormValidation.resetForm(form);

            if (options.validateOnOpen) {
                FormValidation.renderFieldErrors(form, options.validate(form));
            }
        });

        modal.addEventListener("modal:close", function () {
            FormValidation.resetForm(form);
        });
    }

    return {
        bind
    };
})();
