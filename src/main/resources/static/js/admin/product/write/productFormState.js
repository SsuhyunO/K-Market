document.addEventListener("DOMContentLoaded", function () {
    const productForm = document.querySelector("form[name='productRegisterForm'], form[name='productEditForm']");
    if (!productForm) return;

    const submitButton = productForm.querySelector(".section-submit-button");
    const stateMessage = productForm.querySelector("[data-product-form-state]");
    const initialState = serializeForm(productForm);

    function updateState() {
        const hasChanges = serializeForm(productForm) !== initialState;

        if (submitButton) {
            submitButton.disabled = !hasChanges;
        }

        if (stateMessage) {
            stateMessage.textContent = hasChanges ? "변경사항이 있습니다." : "변경사항이 없습니다.";
            stateMessage.classList.toggle("is-dirty", hasChanges);
        }
    }

    productForm.addEventListener("input", updateState);
    productForm.addEventListener("change", updateState);
    productForm.addEventListener("product-option-change", updateState);

    updateState();
});

function serializeForm(form) {
    const values = [];
    const controls = Array.from(form.elements).filter(control => {
        return control.name && !["button", "submit", "reset"].includes(control.type);
    });

    controls.forEach(control => {
        if (control.type === "checkbox" || control.type === "radio") {
            values.push([control.name, control.type, control.checked, control.value]);
            return;
        }

        if (control.type === "file") {
            values.push([
                control.name,
                control.type,
                Array.from(control.files).map(file => `${file.name}:${file.size}:${file.lastModified}`)
            ]);
            return;
        }

        values.push([control.name, control.type, control.value]);
    });

    return JSON.stringify(values);
}
