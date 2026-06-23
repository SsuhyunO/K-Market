document.addEventListener("DOMContentLoaded", function () {
    ManagementTableForm.init();
});

const ManagementTableForm = (() => {
    function init() {
        const form = document.getElementById("management-table-form");
        if (!form) return;

        bindListForm({
            form,
            selectAll: document.getElementById("select-all"),
            deleteButton: document.getElementById("delete-button"),
            itemSelector: getCheckboxSelector(form),
            deleteEmptyMessage: form.dataset.deleteEmptyMessage,
            deleteConfirmMessage: form.dataset.deleteConfirmMessage
        });
    }

    function bindListForm(options) {
        bindCheckboxGroup(options.form, options.selectAll, options.itemSelector);
        bindDeleteValidation(options);
    }

    function bindCheckboxGroup(form, selectAll, itemSelector) {
        if (!form || !selectAll || !itemSelector) return;

        selectAll.addEventListener("change", function () {
            getCheckboxes(form, itemSelector).forEach(checkbox => {
                checkbox.checked = selectAll.checked;
            });

            syncSelectAllState(form, selectAll, itemSelector);
        });

        form.addEventListener("change", function (e) {
            if (!e.target.matches(itemSelector)) return;
            syncSelectAllState(form, selectAll, itemSelector);
        });

        syncSelectAllState(form, selectAll, itemSelector);
    }

    function bindDeleteValidation(options) {
        const form = options.form;
        const deleteButton = options.deleteButton;
        const itemSelector = options.itemSelector;

        if (!form || !deleteButton || !itemSelector) return;

        deleteButton.addEventListener("click", function (e) {
            if (!hasChecked(form, itemSelector)) {
                alert(options.deleteEmptyMessage || "삭제할 항목을 선택해주세요.");
                e.preventDefault();
                return;
            }

            if (!confirm(options.deleteConfirmMessage || "선택한 항목을 삭제하시겠습니까?")) {
                e.preventDefault();
            }
        });
    }

    function getCheckboxSelector(form) {
        if (!form.dataset.checkboxName) return null;
        return `input[name="${form.dataset.checkboxName}"]`;
    }

    function getCheckboxes(form, itemSelector) {
        if (!form || !itemSelector) return [];
        return Array.from(form.querySelectorAll(itemSelector));
    }

    function getChecked(form, itemSelector) {
        return getCheckboxes(form, itemSelector).filter(checkbox => checkbox.checked);
    }

    function hasChecked(form, itemSelector) {
        return getChecked(form, itemSelector).length > 0;
    }

    function syncSelectAllState(form, selectAll, itemSelector) {
        const checkboxes = getCheckboxes(form, itemSelector);
        const checkedCount = getChecked(form, itemSelector).length;

        selectAll.checked = checkboxes.length > 0 && checkedCount === checkboxes.length;
        selectAll.indeterminate = checkedCount > 0 && checkedCount < checkboxes.length;
    }

    return {
        init,
        bindListForm,
        getChecked,
        hasChecked
    };
})();
