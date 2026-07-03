document.addEventListener("DOMContentLoaded", function () {
    const selector = document.getElementById("product-info-notice-type");
    const fieldsBody = document.getElementById("product-info-notice-fields");
    const templates = window.productInfoNoticeTemplates || {};

    if (!selector || !fieldsBody || Object.keys(templates).length === 0) return;

    selector.addEventListener("change", function () {
        const hiddenValue = document.getElementById("product-info-notice-type-value");
        const selectedTemplate = templates[selector.value];
        if (!selectedTemplate) return;

        if (hiddenValue) {
            hiddenValue.value = selector.value;
        }

        fieldsBody.replaceChildren(...selectedTemplate.fields.map(createFieldRow));
    });
});

function createFieldRow(field) {
    const row = document.createElement("tr");
    const header = document.createElement("th");
    const cell = document.createElement("td");
    const label = document.createElement("label");
    const input = document.createElement("input");

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
    input.required = Boolean(field.required);
    cell.append(input);

    row.append(header, cell);
    return row;
}
