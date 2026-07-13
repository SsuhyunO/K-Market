import { getContextPath, getFileUrl } from "../global/pathUtils.js";
import { escapeHtml } from "../global/htmlUtils.js";

export function renderProductRows(products) {
    const productListRow = document.getElementsByClassName('product-list-row')[0];

    if (!products.list.length) {
        productListRow.innerHTML = '<p class="product-empty">조건에 맞는 상품이 없습니다.</p>';
        return;
    }

    productListRow.innerHTML = products
        .list
        .map(createProductRow)
        .join('');
}

function createProductRow(product) {
    const hasThumbnail = Boolean(product.thumb1FileId);
    const hasDiscount = Number(product.discount) > 0;
    const isFreeShipping = Boolean(product.freeShipping);
    const sellerGrade = getSellerGradeInfo(product.sellerGrade);

    return `
        <a href="${getContextPath()}product/view?prodNo=${encodeURIComponent(product.no)}" class="product-row-item">
            <div class="row-thumb">
                ${hasThumbnail 
                    ? `<img src="${getFileUrl(product.thumb1FileId)}" alt="${escapeHtml(product.name)} 상품 썸네일">`
                    : `<span class="thumbnail-placeholder">🖼️</span>`
                }
            </div>

            <div class="row-info">
                <div class="row-product-name">
                    ${escapeHtml(product.name)}
                </div>

                <div class="row-product-desc">
                    ${escapeHtml(product.description ?? '')}
                </div>

                <div class="row-price-area">
                    <span class="row-price">
                        ${product.salePrice.toLocaleString('ko-KR') + '원'}
                    </span>

                    ${hasDiscount 
                        ? `<span class="row-price-del">${product.price.toLocaleString('ko-KR') + '원'}</span>
                                <span class="row-sale-rate">
                                    ${Number(product.discount)}%↓
                                </span>
                            `
                        : ''
                    }
                </div>

                <div class="row-shipping">
                    ${isFreeShipping
                        ? `<span class="free">무료배송</span>`
                        : `<span class="paid">
                                배송비 ${product.deliveryFee.toLocaleString('ko-KR') + '원'}
                            </span>`
                    }
                </div>
            </div>

            <div class="row-meta">
                <div class="row-seller">
                    <span class="seller-badge">🏠</span>
                    <span>${escapeHtml(product.seller ?? '')}</span>
                </div>

                <div class="row-seller-grade ${sellerGrade.className}">
                    <span class="grade-icon">🏅</span>
                    <span>${sellerGrade.label}</span>
                </div>

                <div class="row-rating">
                    <span class="rating-label">상품평</span>
                    <span class="stars"> ${product.ratingStars}</span>
                </div>
            </div>
        </a>
    `;
}

function getSellerGradeInfo(grade) {
    switch (grade) {
        case 'EXCELLENT':
            return {
                className: 'grade-excellent',
                label: '고객만족우수'
            };
        case 'GOOD':
            return {
                className: 'grade-good',
                label: '우수판매자'
            };
        case 'NORMAL':
        default:
            return {
                className: 'grade-normal',
                label: '일반판매자'
            };
    }
}
