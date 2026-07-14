export const PRODUCT_DETAIL_FIELDS = [
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

export function formatVariantItems(items) {
    if (!items || items.length === 0) {
        return "기본 상품";
    }

    return items.map(item => item.value || "-").join(" / ");
}

export function formatVariantStatus(status) {
    const labels = {
        ON_SALE: "판매중",
        SOLD_OUT: "품절",
        STOPPED: "판매중지"
    };
    return labels[status] || status || "-";
}

export function formatFileSize(value) {
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

export function formatNumber(value) {
    const number = Number(value || 0);
    return Number.isFinite(number) ? number.toLocaleString("ko-KR") : "0";
}

export function formatDiscount(value) {
    const number = Number(value || 0);
    return number > 0 ? `${number}%` : "-";
}

export function formatDeliveryFee(value) {
    const number = Number(value || 0);
    return number > 0 ? formatNumber(number) : "무료배송";
}
