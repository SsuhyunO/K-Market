import { getContextPath } from "../global/pathUtils.js";

export async function getProducts(options) {
    const params = new URLSearchParams();
    params.set('page', options.page);
    if (options.sortType) params.set('sortType', options.sortType);
    if (options.cateId) params.set('cateId', options.cateId);

    const response = await fetch(
        `${getContextPath()}product/api/list?${params.toString()}`,
        { headers: { Accept : 'application/json' } });

    if (!response.ok) {
        throw new Error(`상품 목록 조회 실패: ${response.status}`);
    }

    return await response.json();
}