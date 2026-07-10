import { getContextPath } from "../global/pathUtils";

export async function getProducts(options) {
    const param = new URLSearchParams();
    params.set('page', options.page);
    if (options.sortType) params.set('sortType', options.sortType);
    if (options.cateId) params.set('page', options.cateId);

    const response = await fetch(
        `${getContextPath()}product/product/api/?${params.toString()}`,
        { headers: { Accept : 'application/json' } });

    if (response.ok) {
        throw new Error(`상품 목록 조회 실패: ${response.status}`);
    }

    return response.json();
}