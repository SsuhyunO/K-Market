import { escapeHtml } from '../../../global/htmlUtils.js';
import { formatDiscount, formatNumber } from "./productFormatters.js";
import { getFileUrl } from "../../../global/pathUtils.js";

export function renderProductRows(pageData) {
    const table = document.getElementsByClassName("management-table")[0];
    if (!table) return;

    const headerRow = table.querySelector("tr");
    table.innerHTML = "";

    if (headerRow) {
        table.appendChild(headerRow);
    }

    resetSelectAll();

    const products = pageData.list || [];
    if (products.length === 0) {
        const emptyRow = document.createElement("tr");
        emptyRow.innerHTML = `<td colspan="11">등록된 상품이 없습니다.</td>`;
        table.appendChild(emptyRow);
        return;
    }

    products.forEach((product, index) =>
        table.appendChild(createProductRow(product)));
}

function createProductRow(product) {
    const row = document.createElement("tr");
    const price = formatNumber(product.price);
    const discount = formatDiscount(product.discount);
    const point = formatNumber(product.point);
    const sold = formatNumber(product.sold);
    const stock = formatNumber(product.stock);

    row.dataset.productNo = product.prodNo;

    row.innerHTML = `
        <td><label><input type="checkbox" name="productNo" value="${escapeHtml(product.prodNo)}"></label></td>
        <td>${renderThumbnail(product.thumb1FileId)}</td>
        <td>${escapeHtml(product.prodNo)}</td>
        <td>${escapeHtml(product.prodName)}</td>
        <td>${price}</td>
        <td>${discount}</td>
        <td>${point}</td>
        <td>${stock}</td>
        <td>${escapeHtml(product.sellerUid || "-")}</td>
        <td>${sold}</td>
        <td>
            <button class="product-control-button" type="button" data-product-detail-button data-product-no="${escapeHtml(product.prodNo)}">[ 상세 ]</button>
            <button class="product-control-button" type="button" data-product-edit-button data-product-no="${escapeHtml(product.prodNo)}">[ 수정 ]</button>
        </td>
    `;

    return row;
}

function renderThumbnail(fileId) {
    if (!fileId) {
        return `<span class="product-thumb" aria-hidden="true"></span>`;
    }

    return `<img class="product-thumb product-thumb-image" src="${getFileUrl(fileId)}" alt="">`;
}

function resetSelectAll() {
    const selectAll = document.getElementById("select-all");
    if (!selectAll) return;

    selectAll.checked = false;
    selectAll.indeterminate = false;
}