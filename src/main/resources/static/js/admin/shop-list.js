import { initModals } from '../global/modal-form.js';
import { ManagementTableForm } from './global/management-table-form.js';
import { openDaumPostcode } from '../global/daumpostcode.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initShopPostcode();
});

function initShopPostcode() {
    const searchButton = document.querySelector(".shop-address-search");
    const zipCodeField = document.getElementById("shop-register-zip-code");
    const addressField = document.getElementById("shop-register-default-address");
    const detailAddressField = document.getElementById("shop-register-detail-address");

    if (!searchButton || !zipCodeField || !addressField) return;

    searchButton.addEventListener("click", async function () {
        try {
            const { zipCode, address } = await openDaumPostcode();
            zipCodeField.value = zipCode;
            addressField.value = address;
            detailAddressField?.focus();
        } catch (error) {
            console.error(error);
            alert("우편번호 서비스를 불러오지 못했습니다.");
        }
    });
}
