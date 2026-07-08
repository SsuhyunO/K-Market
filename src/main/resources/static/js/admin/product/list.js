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
    "deliveryFee"
];

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    loadProducts().catch(error => {
        console.error(error);
        alert("상품 목록을 불러오지 못했습니다.");
    });
    initProductDetailModal();
});

async function loadProducts(page = getCurrentPage()) {
    const table = document.getElementsByClassName("management-table")[0];
    if (!table) return;

    const params = new URLSearchParams({
        page: String(page),
    });

    const response = await fetch(`${getContextPath()}api/admin/product/list?${params.toString()}`, {
        headers: {
            Accept: 'application/json',
        },
    });

    if (!response.ok) {
        throw new Error(`상품 목록 조회 실패: ${response.status}`);
    }

    const json = await response.json();
    renderProductRows(table, json);
    renderPagination(json);
    updatePageUrl(json.page);
}

function renderProductRows(table, pageData) {
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

    products.forEach((product, index) => {
        table.appendChild(createProductRow(product, pageData.startNo - index));
    });
}

function createProductRow(product, rowNo) {
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

function renderPagination(pageData) {
    const pagination = document.querySelector(".admin-pagination");
    if (!pagination) return;

    pagination.innerHTML = "";
    if (!pageData || pageData.totalPage <= 1) return;

    if (pageData.hasPrev) {
        pagination.appendChild(createPageLink("이전", pageData.startPage - 1));
    }

    for (let page = pageData.startPage; page <= pageData.lastPage; page++) {
        if (page === pageData.page) {
            const current = document.createElement("span");
            current.className = "current";
            current.textContent = page;
            pagination.appendChild(current);
            continue;
        }

        pagination.appendChild(createPageLink(page, page));
    }

    if (pageData.hasNext) {
        pagination.appendChild(createPageLink("다음", pageData.lastPage + 1));
    }
}

function createPageLink(label, page) {
    const link = document.createElement("a");
    link.href = buildPageUrl(page);
    link.dataset.page = page;
    link.textContent = label;
    return link;
}

function initProductDetailModal() {
    const modal = document.getElementById("product-detail-modal");
    if (!modal) return;

    document.addEventListener("click", function (event) {
        const detailButton = event.target.closest("[data-product-detail-button]");
        if (detailButton) {
            loadProductDetail(detailButton.dataset.productNo)
                .then(detail => {
                    renderProductDetail(detail);
                    openModal(modal);
                })
                .catch(error => {
                    console.error(error);
                    alert("상품 상세정보를 불러오지 못했습니다.");
                });
            return;
        }

        const editButton = event.target.closest("[data-product-edit-button]");
        if (editButton) {
            window.location.href = `${getContextPath()}admin/product/edit?prodNo=${encodeURIComponent(editButton.dataset.productNo)}`;
        }
    });

    const pagination = document.querySelector(".admin-pagination");
    if (!pagination) return;

    pagination.addEventListener("click", function (event) {
        const link = event.target.closest("[data-page]");
        if (!link) return;

        event.preventDefault();
        loadProducts(toPositiveInteger(link.dataset.page, 1)).catch(error => {
            console.error(error);
            alert("상품 목록을 불러오지 못했습니다.");
        });
    });
}

async function loadProductDetail(productNo) {
    const response = await fetch(`${getContextPath()}api/admin/product/${encodeURIComponent(productNo)}`, {
        headers: {
            Accept: "application/json",
        },
    });

    if (!response.ok) {
        throw new Error(`상품 상세 조회 실패: ${response.status}`);
    }

    return response.json();
}

function renderProductDetail(product) {
    const detailValues = {
        productCode: product.prodNo,
        productName: product.prodName,
        seller: product.sellerUid,
        viewCount: formatNumber(product.sold),
        rootCategory: product.rootCategoryName || "-",
        subCategory: product.subCategoryName || product.cateId || "-",
        description: product.description,
        maker: product.maker,
        price: formatNumber(product.price),
        discount: formatDiscount(product.discount),
        point: formatNumber(product.point),
        stock: formatNumber(product.stock),
        deliveryFee: formatDeliveryFee(product.deliveryFee)
    };

    PRODUCT_DETAIL_FIELDS.forEach(field => {
        setText(`product-detail-${toKebabCase(field)}`, detailValues[field]);
    });

    renderProductDetailHeroImage(product.thumb2FileId || product.thumb1FileId);
    renderProductDetailImages(product);
    renderProductDetailOptions(product);
    renderProductDetailNoticeValues(product);
}

function renderProductDetailHeroImage(fileId) {
    const media = document.querySelector("#product-detail-modal .product-detail-media");
    if (!media) return;

    media.innerHTML = fileId
        ? `<img class="product-detail-main-image" src="${getFileUrl(fileId)}" alt="">`
        : `<span class="product-detail-thumb" aria-hidden="true"></span>`;
}

function renderProductDetailImages(product) {
    const target = document.getElementById("product-detail-images");
    if (!target) return;

    const images = [
        { label: "목록 이미지", fileId: product.thumb1FileId, file: product.thumb1File },
        { label: "메인 이미지", fileId: product.thumb2FileId, file: product.thumb2File },
        { label: "상세 이미지", fileId: product.thumb3FileId, file: product.thumb3File },
        { label: "상세정보 이미지", fileId: product.detailInfoFileId, file: product.detailInfoFile }
    ];

    target.innerHTML = images.map(image => `
        <figure class="product-detail-image-card">
            <div class="product-detail-image-preview">
                ${image.fileId ? `<img src="${getFileUrl(image.fileId)}" alt="">` : `<span class="product-detail-thumb" aria-hidden="true"></span>`}
            </div>
            <figcaption>
                <strong>${escapeHtml(image.label)}</strong>
                <span>${escapeHtml(getFileLabel(image.fileId, image.file))}</span>
                ${image.file?.fileSize ? `<small>${escapeHtml(formatFileSize(image.file.fileSize))}</small>` : ""}
            </figcaption>
        </figure>
    `).join("");
}

function renderProductDetailOptions(product) {
    renderProductDetailOptionGroups(product.optionGroups || []);
    renderProductDetailVariants(product.variants || []);
}

function renderProductDetailOptionGroups(optionGroups) {
    const target = document.getElementById("product-detail-option-groups");
    if (!target) return;

    if (optionGroups.length === 0) {
        target.innerHTML = `<p class="product-detail-empty">등록된 옵션 항목이 없습니다.</p>`;
        return;
    }

    target.innerHTML = optionGroups.map(group => `
        <section class="product-detail-option-group">
            <h5>${escapeHtml(group.name || "-")}</h5>
            <div class="product-detail-chip-list">
                ${(group.items || []).map(item => `<span>${escapeHtml(item.value || "-")}</span>`).join("") || `<span>-</span>`}
            </div>
        </section>
    `).join("");
}

function renderProductDetailVariants(variants) {
    const target = document.getElementById("product-detail-variants");
    if (!target) return;

    if (variants.length === 0) {
        target.innerHTML = `<p class="product-detail-empty">등록된 옵션 조합이 없습니다.</p>`;
        return;
    }

    target.innerHTML = `
        <table class="product-detail-table">
            <thead>
            <tr>
                <th>옵션 조합</th>
                <th>재고수량</th>
                <th>판매상태</th>
            </tr>
            </thead>
            <tbody>
            ${variants.map(variant => `
                <tr>
                    <td>${escapeHtml(formatVariantItems(variant.items || []))}</td>
                    <td>${formatNumber(variant.stock)}</td>
                    <td>${escapeHtml(formatVariantStatus(variant.status))}</td>
                </tr>
            `).join("")}
            </tbody>
        </table>
    `;
}

function renderProductDetailNoticeValues(product) {
    const listTarget = document.getElementById("product-detail-notice-values");
    if (!listTarget) return;

    const noticeValues = product.noticeValues || [];
    const typeRow = `
        <div>
            <dt>상품군</dt>
            <dd>${escapeHtml(product.infoNoticeTypeName || product.infoNoticeType || "-")}</dd>
        </div>
    `;

    if (noticeValues.length === 0) {
        listTarget.innerHTML = typeRow + `
            <div>
                <dt>제공고시</dt>
                <dd>등록된 상품정보 제공고시가 없습니다.</dd>
            </div>
        `;
        return;
    }

    listTarget.innerHTML = typeRow + noticeValues.map(notice => `
        <div>
            <dt>${escapeHtml(notice.label || notice.key || "-")}</dt>
            <dd>${escapeHtml(notice.value || "-")}</dd>
        </div>
    `).join("");
}

function formatVariantItems(items) {
    if (!items || items.length === 0) {
        return "기본 상품";
    }

    return items.map(item => item.value || "-").join(" / ");
}

function formatVariantStatus(status) {
    const labels = {
        ON_SALE: "판매중",
        SOLD_OUT: "품절",
        STOPPED: "판매중지"
    };
    return labels[status] || status || "-";
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (!target) return;

    target.textContent = value || "-";
}

function toKebabCase(value) {
    return value.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`);
}

function getContextPath() {
    const contextPath = document.body.dataset.contextPath || "/";
    return contextPath.endsWith("/") ? contextPath : `${contextPath}/`;
}

function getCurrentPage() {
    const params = new URLSearchParams(window.location.search);
    return toPositiveInteger(params.get("page"), 1);
}

function updatePageUrl(page) {
    window.history.replaceState(null, "", buildPageUrl(page));
}

function buildPageUrl(page) {
    const params = new URLSearchParams(window.location.search);
    params.set("page", page);
    return `${window.location.pathname}?${params.toString()}`;
}

function renderThumbnail(fileId) {
    if (!fileId) {
        return `<span class="product-thumb" aria-hidden="true"></span>`;
    }

    return `<img class="product-thumb product-thumb-image" src="${getFileUrl(fileId)}" alt="">`;
}

function getFileLabel(fileId, file = null) {
    if (file?.originalName) {
        return file.originalName;
    }
    return fileId ? `파일 #${fileId}` : "-";
}

function getFileUrl(fileId) {
    return `${getContextPath()}files/${encodeURIComponent(fileId)}`;
}

function formatFileSize(value) {
    const bytes = Number(value || 0);
    if (!Number.isFinite(bytes) || bytes <= 0) {
        return "-";
    }

    if (bytes >= 1024 * 1024) {
        return `${(bytes / (1024 * 1024)).toFixed(1)}MB`;
    }
    if (bytes >= 1024) {
        return `${(bytes / 1024).toFixed(1)}KB`;
    }
    return `${bytes}B`;
}

function formatNumber(value) {
    const number = Number(value || 0);
    return Number.isFinite(number) ? number.toLocaleString("ko-KR") : "0";
}

function formatDiscount(value) {
    const number = Number(value || 0);
    return number > 0 ? `${number}%` : "-";
}

function formatDeliveryFee(value) {
    const number = Number(value || 0);
    return number > 0 ? formatNumber(number) : "무료배송";
}

function toPositiveInteger(value, fallback) {
    const number = Number.parseInt(value, 10);
    return Number.isInteger(number) && number > 0 ? number : fallback;
}

function resetSelectAll() {
    const selectAll = document.getElementById("select-all");
    if (!selectAll) return;

    selectAll.checked = false;
    selectAll.indeterminate = false;
}

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
