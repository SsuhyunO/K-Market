import { getContextPath } from "../../../global/pathUtils.js";

export async function getProductList(page) {
    const response = await fetch(
        `${getContextPath()}api/admin/product/list?page=${page}`,
        {
            headers: { Accept: "application/json" }
        }
    );

    if (!response.ok) {
        throw new Error(`상품 목록 조회 실패: ${response.status}`);
    }

    return response.json();
}

export async function getProductDetail(prodNo) {
    const response = await fetch(
        `${getContextPath()}api/admin/product/${encodeURIComponent(prodNo)}`,
        {
            headers: { Accept: "application/json" }
        }
    );

    if (!response.ok) {
        throw new Error(`상품 상세 조회 실패: ${response.status}`);
    }

    return response.json();
}