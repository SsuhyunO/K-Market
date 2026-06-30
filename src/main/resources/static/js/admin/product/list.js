import { ManagementTableForm } from '../global/management-table-form.js';
import { initModals, openModal } from '../../global/modal-form.js';

const PRODUCT_DETAIL_FIELDS = [
    "productCode",
    "productName",
    "seller",
    "viewCount",
    "rootCategory",
    "subCategory",
    "description",
    "maker",
    "price",
    "discount",
    "point",
    "stock",
    "deliveryFee",
    "listImage",
    "mainImage",
    "detailImage",
    "detailInfoFile",
    "option1",
    "option1Value",
    "productCondition",
    "taxExempt",
    "receiptIssued",
    "businessType",
    "origin"
];

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initProductDetailModal();
});

function initProductDetailModal() {
    const modal = document.getElementById("product-detail-modal");
    if (!modal) return;

    document.querySelectorAll("[data-product-detail-button]").forEach(button => {
        button.addEventListener("click", function () {
            const row = button.closest("tr");
            if (!row) return;

            PRODUCT_DETAIL_FIELDS.forEach(field => {
                setText(`product-detail-${toKebabCase(field)}`, row.dataset[field]);
            });

            openModal(modal);
        });
    });
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (!target) return;

    target.textContent = value || "-";
}

function toKebabCase(value) {
    return value.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`);
}
