import { initModals } from '../global/modal-form.js';
import { ManagementTableForm } from './global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
});
