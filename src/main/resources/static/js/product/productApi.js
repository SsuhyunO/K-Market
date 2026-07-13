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

export async function searchProducts(options) {
    const params = new URLSearchParams();
    params.set('page', options.page);
    if (options.sortType) params.set('sortType', options.sortType);
    if (options.keyword) params.set('keyword', options.keyword);
    if (options.name) params.set('name', 'true');
    if (options.description) params.set('description', 'true');
    if (options.price) params.set('price', 'true');
    if (options.minPrice) params.set('minPrice', options.minPrice);
    if (options.maxPrice) params.set('maxPrice', options.maxPrice);

    const response = await fetch(
        `${getContextPath()}product/api/search?${params.toString()}`,
        { headers: { Accept : 'application/json' } });

    if (!response.ok) {
        throw new Error(`상품 검색 실패: ${response.status}`);
    }

    return await response.json();
}
