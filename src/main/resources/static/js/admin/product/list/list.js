import { ManagementTableForm } from "../../global/management-table-form.js";
import { initPagination } from "../../global/pagination.js";
import { initProductDetailModal } from "./productDetailModal.js";
import { renderProductRows } from "./productTableRenderer.js";
import { getProductList } from "./productApi.js";

document.addEventListener("DOMContentLoaded", function () {
    ManagementTableForm.init();
    initProductDetailModal();
    initPagination({
        fetchPage: loadProducts,
        onError: error => {
            console.error(error);
            alert("상품 목록을 불러오지 못했습니다.");
        }
    });
});

async function loadProducts(page = 1) {
    const pageData = await getProductList(page);
    renderProductRows(pageData);
    return pageData;
}