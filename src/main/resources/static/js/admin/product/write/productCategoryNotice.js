document.addEventListener("DOMContentLoaded", function () {
    const rootCategorySelector = document.getElementById("root-category-selector");
    const subCategorySelector = document.getElementById("sub-category-selector");
    const noticeTypeSelector = document.getElementById("product-info-notice-type");
    const noticeTypeValue = document.getElementById("product-info-notice-type-value");

    if (!rootCategorySelector || !subCategorySelector || !noticeTypeSelector || !noticeTypeValue) return;

    const subCategoryOptions = Array.from(subCategorySelector.options);

    rootCategorySelector.addEventListener("change", function () {
        filterSubCategories(rootCategorySelector.value, subCategorySelector, subCategoryOptions);
        applySelectedCategoryNoticeType(subCategorySelector, noticeTypeSelector, noticeTypeValue);
    });

    subCategorySelector.addEventListener("change", function () {
        applySelectedCategoryNoticeType(subCategorySelector, noticeTypeSelector, noticeTypeValue);
    });

    filterSubCategories(rootCategorySelector.value, subCategorySelector, subCategoryOptions);
    applySelectedCategoryNoticeType(subCategorySelector, noticeTypeSelector, noticeTypeValue);
});

function filterSubCategories(rootCategoryValue, subCategorySelector, subCategoryOptions) {
    subCategorySelector.disabled = !rootCategoryValue;

    subCategoryOptions.forEach(option => {
        const shouldShow = !option.dataset.parent || option.dataset.parent === rootCategoryValue;
        option.hidden = !shouldShow;
        option.disabled = !shouldShow;
    });

    if (subCategorySelector.selectedOptions[0]?.disabled) {
        subCategorySelector.value = "";
    }
}

function applySelectedCategoryNoticeType(subCategorySelector, noticeTypeSelector, noticeTypeValue) {
    const selectedOption = subCategorySelector.selectedOptions[0];
    const infoNoticeType = selectedOption?.dataset.infoNoticeType || "";

    if (!infoNoticeType) return;

    noticeTypeSelector.value = infoNoticeType;
    noticeTypeValue.value = infoNoticeType;
    noticeTypeSelector.dispatchEvent(new Event("change", { bubbles: true }));
}
