import { getContextPath } from '../../global/pathUtils.js';

export async function getProducts(options) {
    const params = new URLSearchParams();

    params.set('page', options.page);
    if (options.searchType) {
        params.set('type', options.searchType)
    }

    if (options.keyword) {
        params.set('keyword', options.keyword)
    }
    if (options.uncategorizedOnly) {
        params.set('uncategorizedOnly', 'true');
    }

    const response = await fetch(
        `${getContextPath()}admin/product/api/list?${params.toString()}`,
        { headers: { Accept: 'application/json' } }
    );

    if (!response.ok) {
        throw new Error(`상품 목록 조회 실패: ${response.status}`);
    }

    return response.json();
}

export async function getProductDetail(prodNo) {
    const response = await fetch(
        `${getContextPath()}admin/product/api/${encodeURIComponent(prodNo)}`,
        { headers: { Accept: 'application/json' } }
    );

    if (!response.ok) {
        throw new Error(`상품 상세 조회 실패: ${response.status}`);
    }

    const json = await response.json();
    console.log(JSON.stringify(json));
    return json;
}

export async function removeProducts(productNos) {
    const params = new URLSearchParams();
    productNos.forEach(productNo => params.append('productNo', productNo));

    const response = await fetch(
        `${getContextPath()}admin/product/api?${params.toString()}`,
        {
            method: 'DELETE',
            headers: { Accept: 'application/json' }
        }
    );

    const json = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(json.message || `상품 삭제 실패: ${response.status}`);
    }

    return json;
}
