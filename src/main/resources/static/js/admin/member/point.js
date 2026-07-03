import { ManagementTableForm } from '../global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    ManagementTableForm.init();
    initPointDeletion();
});

function initPointDeletion() {
    const form = document.getElementById("management-table-form");
    const selectAll = document.getElementById("select-all");
    const itemSelector = 'input[name="pointNo"]';

    if (!form) return;

    form.addEventListener("submit", function (event) {
        event.preventDefault();

        ManagementTableForm.getChecked(form, itemSelector).forEach(checkbox => {
            checkbox.closest("tr")?.remove();
        });

        if (selectAll) {
            selectAll.checked = false;
            selectAll.indeterminate = false;
        }
    });
}
