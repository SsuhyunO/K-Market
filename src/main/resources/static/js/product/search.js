import { renderProductRows } from './productRowRenderer.js';
import { searchProducts } from './productApi.js';
import { initPagination } from '../global/pagination.js';

document.addEventListener('DOMContentLoaded', () => {
    normalizeSearchUrl();
    initSearchForm();
    initSortTabs();
    updateSearchSummary();
    updateActiveSortTab();

    initPagination({
        selector: '.list-pagination',
        fetchPage: loadProducts,
        onError: errorHandler
    });
});

function loadProducts(page = 1) {
    const params = new URLSearchParams(window.location.search);

    return searchProducts({
        page,
        sortType: params.get('sortType') ?? 'SALES',
        keyword: params.get('keyword') ?? '',
        name: params.get('name') === 'true',
        description: params.get('description') === 'true',
        price: params.get('price') === 'true',
        minPrice: params.get('minPrice') ?? '',
        maxPrice: params.get('maxPrice') ?? ''
    }).then(response => {
        renderProductRows(response);
        updateSearchSummary(response);
        syncSearchForm();
        return response;
    });
}

function normalizeSearchUrl() {
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
        syncSearchForm();
        return;
    }

    window.history.replaceState(
        null,
        '',
        `${window.location.pathname}?${params.toString()}`
    );

    syncSearchForm();
}

function initSearchForm() {
    const form = document.querySelector('.search-filter form');

    if (!form) {
        return;
    }

    form.addEventListener('submit', event => {
        event.preventDefault();

        const formData = new FormData(form);

        updateSearchUrl({
            keyword: String(formData.get('keyword') ?? '').trim(),
            name: formData.has('name'),
            description: formData.has('description'),
            price: formData.has('price'),
            minPrice: String(formData.get('minPrice') ?? '').trim(),
            maxPrice: String(formData.get('maxPrice') ?? '').trim(),
            page: 1
        });

        refreshPagination(1);
    });
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

        updateSearchUrl({
            sortType: sortLink.dataset.sort,
            page: 1
        });

        updateActiveSortTab();
        refreshPagination(1);
    });
}

function updateSearchUrl(changes) {
    const params = new URLSearchParams(window.location.search);

    if (Object.hasOwn(changes, 'keyword')) {
        setOrDelete(params, 'keyword', changes.keyword);
    }

    if (Object.hasOwn(changes, 'name')) {
        setBoolean(params, 'name', changes.name);
    }

    if (Object.hasOwn(changes, 'description')) {
        setBoolean(params, 'description', changes.description);
    }

    if (Object.hasOwn(changes, 'price')) {
        setBoolean(params, 'price', changes.price);
    }

    if (Object.hasOwn(changes, 'minPrice')) {
        setOrDelete(params, 'minPrice', changes.minPrice);
    }

    if (Object.hasOwn(changes, 'maxPrice')) {
        setOrDelete(params, 'maxPrice', changes.maxPrice);
    }

    if (changes.sortType != null) {
        params.set('sortType', changes.sortType);
    }

    if (changes.page != null) {
        params.set('page', String(changes.page));
    }

    const queryString = params.toString();
    window.history.pushState(
        null,
        '',
        queryString ? `${window.location.pathname}?${queryString}` : window.location.pathname
    );
}

function setOrDelete(params, name, value) {
    if (value == null || value === '') {
        params.delete(name);
        return;
    }

    params.set(name, value);
}

function setBoolean(params, name, value) {
    if (value === true) {
        params.set(name, 'true');
        return;
    }

    if (value === false) {
        params.delete(name);
    }
}

function syncSearchForm() {
    const form = document.querySelector('.search-filter form');

    if (!form) {
        return;
    }

    const params = new URLSearchParams(window.location.search);

    form.elements.keyword.value = params.get('keyword') ?? '';
    form.elements.name.checked = params.get('name') === 'true';
    form.elements.description.checked = params.get('description') === 'true';
    form.elements.price.checked = params.get('price') === 'true';
    form.elements.minPrice.value = params.get('minPrice') ?? '';
    form.elements.maxPrice.value = params.get('maxPrice') ?? '';
}

function updateSearchSummary(response) {
    const params = new URLSearchParams(window.location.search);
    const keyword = params.get('keyword') ?? '';
    const keywordElement = document.querySelector('.search-result-header .keyword');
    const countElement = document.querySelector('.search-result-header strong');

    if (keywordElement) {
        keywordElement.textContent = keyword || '전체상품';
    }

    if (countElement && response) {
        countElement.textContent = Number(response.totalElements).toLocaleString('ko-KR');
    }
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

function refreshPagination(page) {
    window.dispatchEvent(
        new CustomEvent('pagination:refresh', {
            detail: {
                page
            }
        })
    );
}

window.addEventListener('popstate', () => {
    updateActiveSortTab();
    syncSearchForm();
});

function errorHandler(error) {
    console.error('상품 검색 결과를 불러올 수 없습니다.', error);
    alert('상품 검색 결과를 불러올 수 없습니다.');
}
