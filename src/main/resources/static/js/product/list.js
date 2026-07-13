import { renderProductRows } from './productRowRenderer.js';
import { getProducts } from './productApi.js';
import { initPagination } from '../global/pagination.js';

document.addEventListener('DOMContentLoaded', () => {
    normalizeProductListUrl();
    initSortTabs();
    updateActiveSortTab();

    initPagination({
        selector: '.list-pagination',
        fetchPage: loadProducts,
        onError: errorHandler
    });
});

function loadProducts(page = 1) {
    const params = new URLSearchParams(window.location.search);

    const cateId = params.get('category');
    const sortType = params.get('sortType') ?? 'SALES';

    return getProducts({
        page,
        sortType,
        cateId
    }).then(response => {
        renderProductRows(response);
        return response;
    });
}

function normalizeProductListUrl() {
    const params = new URLSearchParams(window.location.search);
    let changed = false;

    if (!params.has('sortType')) {
        params.set('sortType', 'SALES');
        changed = true;
    }

    if (!params.has('page')) {
        params.set('page', '1');
        changed = true;
    }

    if (!changed) {
        return;
    }

    const queryString = params.toString();

    window.history.replaceState(
        null,
        '',
        `${window.location.pathname}?${queryString}`
    );
}

function initSortTabs() {
    const sortTabs = document.querySelector('.sort-tabs');

    if (!sortTabs) {
        return;
    }

    sortTabs.addEventListener('click', event => {
        const sortLink = event.target.closest('[data-sort]');

        if (!sortLink) {
            return;
        }

        event.preventDefault();

        const sortType = sortLink.dataset.sort;

        updateListUrl({
            sortType,
            page: 1
        });

        updateActiveSortTab();

        window.dispatchEvent(
            new CustomEvent('pagination:refresh', {
                detail: {
                    page: 1
                }
            })
        );
    });
}

function updateListUrl({ page, sortType, category }) {
    const params = new URLSearchParams(window.location.search);

    if (page != null) {
        params.set('page', String(page));
    }

    if (sortType != null) {
        params.set('sortType', sortType);
    }

    if (category != null) {
        params.set('category', String(category));
    }

    const queryString = params.toString();

    const newUrl = queryString
        ? `${window.location.pathname}?${queryString}`
        : window.location.pathname;

    window.history.pushState(null, '', newUrl);
}

function updateActiveSortTab() {
    const params = new URLSearchParams(window.location.search);
    const currentSortType = params.get('sortType') ?? 'SALES';

    document.querySelectorAll('.sort-tabs [data-sort]')
        .forEach(link => {
            link.classList.toggle(
                'active',
                link.dataset.sort === currentSortType
            );
        });
}

window.addEventListener('popstate', () => {
    updateActiveSortTab();
});

function errorHandler(error) {
    console.error('상품 목록을 불러올 수 없습니다.', error);
    alert('상품 목록을 불러올 수 없습니다.');
}