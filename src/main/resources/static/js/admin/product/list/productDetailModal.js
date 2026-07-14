import { initModals, openModal } from "../../../global/modal-form.js";
import { escapeHtml } from "../../../global/htmlUtils.js";
import { getFileUrl, getContextPath } from "../../../global/pathUtils.js";
import {
    PRODUCT_DETAIL_FIELDS,
    formatFileSize,
    formatNumber,
    formatDiscount,
    formatDeliveryFee,
    formatVariantItems,
    formatVariantStatus
} from "./productFormatters.js";
import { getProductDetail } from "../productApi.js";

export function initProductDetailModal() {
    initModals();

    const modal = document.getElementById("product-detail-modal");
    if (!modal) return;

    document.addEventListener("click", function (event) {
        const detailButton = event.target.closest("[data-product-detail-button]");
        if (detailButton) {
            getProductDetail(detailButton.dataset.productNo)
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
}

function renderProductDetail(product) {
    const detailValues = {
        productCode: product.prodNo,
        productName: product.prodName,
        seller: product.sellerUid,
        viewCount: formatNumber(product.hit),
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
    renderProductDetailCommonNoticeValues(product);

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
                <dd>등록된 상품군별 상품정보 제공고시가 없습니다.</dd>
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

function renderProductDetailCommonNoticeValues(product) {
    const listTarget = document.getElementById("product-detail-common-notice-values");
    if (!listTarget) return;

    const rows = [
        ["상품번호", product.prodNo],
        ["상품상태", formatProductStatus(product.status)],
        ["부가세 면세여부", product.taxType],
        ["영수증 발행", product.receiptIssueType],
        ["사업자 구분", product.businessType],
        ["브랜드", product.brand],
        ["원산지", product.origin]
    ];

    listTarget.innerHTML = rows.map(([label, value]) => `
        <div>
            <dt>${escapeHtml(label)}</dt>
            <dd>${escapeHtml(value || "-")}</dd>
        </div>
    `).join("");
}

function formatProductStatus(status) {
    const labels = {
        ON_SALE: "새상품",
        STOPPED: "판매중지",
        DELETED: "삭제"
    };

    return labels[status] || status || "-";
}


function getFileLabel(fileId, file = null) {
    if (file?.originalName) {
        return file.originalName;
    }
    return fileId ? `파일 #${fileId}` : "-";
}

function setText(id, value) {
    const target = document.getElementById(id);
    if (!target) return;

    target.textContent = value || "-";
}

function toKebabCase(value) {
    return value.replace(/[A-Z]/g, letter => `-${letter.toLowerCase()}`);
}
