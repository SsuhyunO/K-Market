document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form[name='productRegisterForm'], form[name='productEditForm']");
    const selector = document.getElementById("product-info-notice-type");
    const fieldsBody = document.getElementById("product-info-notice-fields");
    const templates = window.productInfoNoticeTemplates || {};
    const editNoticeValues = getEditNoticeValues();

    if (!selector || !fieldsBody || Object.keys(templates).length === 0) return;

    const renderFields = function () {
        renderNoticeFields(form, selector, fieldsBody, templates, editNoticeValues);
    };

    selector.addEventListener("change", function () {
        renderFields();
    });

    bindAutoNoticeSources(form, fieldsBody);
    renderFields();
});

const AUTO_NOTICE_SOURCES = {
    productName: "[name='prodName']",
    modelName: "[name='prodName']",
    title: "[name='prodName']",
    manufacturer: "[name='maker']",
    producer: "[name='maker']",
    origin: "[name='origin']",
    brand: "[name='brand']"
};

const DEFAULT_NOTICE_VALUES = {
    certification: "해당 없음",
    importDeclaration: "해당 없음",
    geneticallyModified: "해당 없음",
    imported: "해당 없음",
    functional: "해당 없음",
    releasedAt: "상세페이지 참조",
    manufacturedAt: "상세페이지 참조",
    warranty: "관련 법 및 소비자분쟁해결기준에 따름",
    asContact: "고객센터 문의",
    customerCare: "고객센터 문의"
};

function renderNoticeFields(form, selector, fieldsBody, templates, editNoticeValues) {
    const hiddenValue = document.getElementById("product-info-notice-type-value");
    const selectedTemplate = templates[selector.value];
    if (!selectedTemplate) return;

    if (hiddenValue) {
        hiddenValue.value = selector.value;
    }

    fieldsBody.replaceChildren(...selectedTemplate.fields.map(field => createFieldRow(field, editNoticeValues, form)));
}

function createFieldRow(field, values = {}, form = null) {
    const row = document.createElement("tr");
    const header = document.createElement("th");
    const cell = document.createElement("td");
    const label = document.createElement("label");
    const input = document.createElement("input");
    const autoSourceSelector = AUTO_NOTICE_SOURCES[field.key];

    label.htmlFor = `notice-${field.key}`;
    label.textContent = field.label;
    header.append(label);

    if (field.required) {
        const requiredMark = document.createElement("span");
        requiredMark.className = "notice-required-mark";
        requiredMark.textContent = "*";
        header.append(requiredMark);
    }

    input.id = `notice-${field.key}`;
    input.type = "text";
    input.name = `informationNoticeValues[${field.key}]`;
    input.placeholder = field.placeholder || "";
    input.value = resolveNoticeFieldValue(field.key, values, form);
    input.required = Boolean(field.required);
    input.dataset.noticeKey = field.key;

    if (autoSourceSelector) {
        input.readOnly = true;
        input.dataset.autoNoticeSource = autoSourceSelector;
        input.title = "기본정보 입력값이 자동 반영됩니다.";
    }

    cell.append(input);

    row.append(header, cell);
    return row;
}

function resolveNoticeFieldValue(key, values, form) {
    const sourceSelector = AUTO_NOTICE_SOURCES[key];
    if (sourceSelector) {
        return form?.querySelector(sourceSelector)?.value || "";
    }

    return values[key] || DEFAULT_NOTICE_VALUES[key] || "";
}

function bindAutoNoticeSources(form, fieldsBody) {
    if (!form || !fieldsBody) return;

    Object.values(AUTO_NOTICE_SOURCES)
        .filter((selector, index, selectors) => selectors.indexOf(selector) === index)
        .forEach(selector => {
            const source = form.querySelector(selector);
            if (!source) return;

            source.addEventListener("input", function () {
                fieldsBody
                    .querySelectorAll(`[data-auto-notice-source="${selector}"]`)
                    .forEach(input => input.value = source.value);
            });
        });
}

function getEditNoticeValues() {
    const values = {};
    const noticeValues = window.productEditData?.noticeValues || [];

    noticeValues.forEach(notice => {
        if (notice.key) {
            values[notice.key] = notice.value || "";
        }
    });

    return values;
}
