import { getContextPath } from '../../global/pathUtils.js';

export async function getOrders(options) {
    const params = new URLSearchParams();

    params.set('page', options.page);

    if (options.searchType) {
        params.set('type', options.searchType);
    }

    if (options.keyword) {
        params.set('keyword', options.keyword);
    }

    const response = await fetch(
        `${getContextPath()}admin/order/api/list?${params.toString()}`,
        { headers: { Accept: 'application/json' } }
    );

    if (!response.ok) {
        throw new Error(`Order list request failed: ${response.status}`);
    }

    return response.json();
}

export async function getOrderDetail(orderNo) {
    return fetchJson(`${getContextPath()}admin/order/api/${encodeURIComponent(orderNo)}`);
}

export async function getShippableItems(orderNo) {
    return fetchJson(`${getContextPath()}admin/order/api/${encodeURIComponent(orderNo)}/shippable-items`);
}

export async function registerDelivery(payload) {
    const response = await fetch(`${getContextPath()}admin/order/api/shipments`, {
        method: 'POST',
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    });

    const json = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(json.message || `Delivery register request failed: ${response.status}`);
    }

    return json;
}

async function fetchJson(url) {
    const response = await fetch(url, { headers: { Accept: 'application/json' } });
    const json = await response.json().catch(() => ({}));
    if (!response.ok) {
        throw new Error(json.message || `Request failed: ${response.status}`);
    }
    return json;
}
