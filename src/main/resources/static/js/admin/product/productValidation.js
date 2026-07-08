document.addEventListener("DOMContentLoaded", function () {
    const productForm = document.querySelector("form[name='productRegisterForm'], form[name='productEditForm']");
    if (!productForm) return;

    productForm.addEventListener("submit", function (event) {
        clearValidation(productForm);

        const errors = [
            ...validateCategories(productForm),
            ...validateBasicInfo(productForm),
            ...validateFiles(productForm),
            ...validateNoticeFields(productForm),
            ...validateOptions(productForm)
        ];

        if (errors.length === 0) return;

        event.preventDefault();
        renderValidationErrors(errors);
        focusFirstError(errors);
    });
});

function validateCategories(form) {
    const errors = [];
    const rootCategory = form.querySelector("#root-category-selector");
    const subCategory = form.querySelector("#sub-category-selector");

    if (!rootCategory?.value) {
        errors.push({ element: rootCategory, message: "1차 카테고리를 선택해주세요." });
    }

    if (!subCategory?.value) {
        errors.push({ element: subCategory, message: "2차 카테고리를 선택해주세요." });
    }

    const selectedSubCategory = subCategory?.selectedOptions?.[0];
    if (rootCategory?.value && selectedSubCategory?.dataset.parent && selectedSubCategory.dataset.parent !== rootCategory.value) {
        errors.push({ element: subCategory, message: "선택한 1차 카테고리의 하위 카테고리를 선택해주세요." });
    }

    return errors;
}

function validateBasicInfo(form) {
    const errors = [];

    requireText(errors, form, "[name='prodName']", "상품명을 입력해주세요.");
    requireText(errors, form, "[name='description']", "기본설명을 입력해주세요.");
    requireText(errors, form, "[name='maker']", "제조사를 입력해주세요.");
    requireNumber(errors, form, "[name='price']", "상품금액은 0 이상이어야 합니다.", { min: 0 });
    requireNumber(errors, form, "[name='discount']", "할인율은 0 이상 100 이하이어야 합니다.", { min: 0, max: 100 });
    requireNumber(errors, form, "[name='point']", "포인트는 0 이상이어야 합니다.", { min: 0 });
    requireNumber(errors, form, "[name='deliveryFee']", "배송비는 0 이상이어야 합니다.", { min: 0 });

    return errors;
}

function validateFiles(form) {
    const errors = [];

    requireFile(errors, form, "[name='thumb1']", "상품 이미지1 파일을 선택해주세요.");
    requireFile(errors, form, "[name='thumb2']", "상품 이미지2 파일을 선택해주세요.");
    requireFile(errors, form, "[name='thumb3']", "상품 이미지3 파일을 선택해주세요.");
    requireFile(errors, form, "[name='detailInfoFile']", "상품 상세정보 이미지를 선택해주세요.", 1024 * 1024);

    return errors;
}

function validateNoticeFields(form) {
    const errors = [];
    const noticeType = form.querySelector("[name='infoNoticeType']");
    const templates = window.productInfoNoticeTemplates || {};
    const template = templates[noticeType?.value];

    if (!noticeType?.value || !template) {
        errors.push({ element: noticeType, message: "상품정보 제공고시 상품군이 올바르지 않습니다." });
        return errors;
    }

    template.fields
        .filter(field => field.required)
        .forEach(field => {
            const input = findNoticeInput(form, field.key);
            if (!input || isBlank(input.value)) {
                errors.push({ element: input || form.querySelector("#product-info-notice-fields"), message: `${field.label} 항목을 입력해주세요.` });
            }
        });

    return errors;
}

function validateOptions(form) {
    const errors = [];
    const optionError = form.querySelector("[data-option-error]:not(:empty)");
    const stockInputs = [...form.querySelectorAll("[data-option-stock-input]")];

    if (optionError) {
        errors.push({ element: optionError, message: optionError.textContent.trim() });
    }

    if (stockInputs.length === 0) {
        errors.push({ element: form.querySelector("[data-option-stock-body]"), message: "상품 조합 정보가 필요합니다." });
        return errors;
    }

    stockInputs.forEach(input => {
        const value = Number(input.value);
        if (!Number.isFinite(value) || value < 0) {
            errors.push({ element: input, message: "재고는 0 이상이어야 합니다." });
        }
    });

    form.querySelectorAll("[data-option-status-input]").forEach(select => {
        if (!["ON_SALE", "SOLD_OUT", "STOPPED"].includes(select.value)) {
            errors.push({ element: select, message: "판매상태가 올바르지 않습니다." });
        }
    });

    return errors;
}

function requireText(errors, form, selector, message) {
    const element = form.querySelector(selector);
    if (!element || isBlank(element.value)) {
        errors.push({ element, message });
    }
}

function requireNumber(errors, form, selector, message, { min = null, max = null } = {}) {
    const element = form.querySelector(selector);
    const value = Number(element?.value);

    if (!element || isBlank(element.value) || !Number.isFinite(value) || (min !== null && value < min) || (max !== null && value > max)) {
        errors.push({ element, message });
    }
}

function requireFile(errors, form, selector, message, maxSize = null) {
    const element = form.querySelector(selector);
    const file = element?.files?.[0];
    const hasExistingFile = Boolean(element?.dataset?.existingFileId);

    if (!file) {
        if (hasExistingFile) return;
        errors.push({ element, message });
        return;
    }

    if (!file.type || !file.type.startsWith("image/")) {
        errors.push({ element, message: "이미지 파일만 업로드할 수 있습니다." });
        return;
    }

    if (maxSize !== null && file.size > maxSize) {
        errors.push({ element, message: "상품 상세정보 이미지는 1MB 이하로 업로드해주세요." });
    }
}

function clearValidation(form) {
    form.querySelectorAll(".field-error[data-product-validation-error]").forEach(error => error.remove());
    form.querySelectorAll(".is-invalid").forEach(element => element.classList.remove("is-invalid"));
}

function renderValidationErrors(errors) {
    const rendered = new Set();

    errors.forEach(error => {
        if (!error.element || rendered.has(error.element)) return;

        error.element.classList?.add("is-invalid");

        const message = document.createElement("p");
        message.className = "field-error";
        message.dataset.productValidationError = "";
        message.textContent = error.message;

        const container = findErrorContainer(error.element);
        container.append(message);
        rendered.add(error.element);
    });
}

function findErrorContainer(element) {
    if (element.matches?.("td")) return element;
    return element.closest("td") || element.parentElement || element;
}

function findNoticeInput(form, key) {
    return [...form.querySelectorAll("[name^='informationNoticeValues[']")]
        .find(input => input.name === `informationNoticeValues[${key}]`);
}

function focusFirstError(errors) {
    const target = errors.find(error => error.element)?.element;
    if (!target) return;

    target.scrollIntoView({ block: "center", behavior: "smooth" });
    if (typeof target.focus === "function" && !target.disabled) {
        target.focus({ preventScroll: true });
    }
}

function isBlank(value) {
    return value == null || String(value).trim() === "";
}
