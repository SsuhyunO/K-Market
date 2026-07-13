import { ManagementTableForm } from '../../global/management-table-form.js';
import { initPagination } from '../../../global/pagination.js';
import { initProductDetailModal } from './productDetailModal.js';
import { renderProductRows } from './productTableRenderer.js';
import { getProducts } from '../productApi.js';

document.addEventListener('DOMContentLoaded', function () {
    ManagementTableForm.init();
    initProductDetailModal();
    initSearchForm();

    initPagination({
        fetchPage: loadProducts,
        onError: errorHandler
    });
});

async function loadProducts(page = 1) {
    const { type, keyword } = getSearchCondition();

    const pageData = await getProducts({
        page: page,
        searchType: type,
        keyword: keyword
    });

    renderProductRows(pageData);

    return pageData;
}

function initSearchForm() {
    const searchForm = document.searchForm;
    if (!searchForm) return;

    searchForm.addEventListener('submit', event => {
        event.preventDefault();

        const params = new URLSearchParams(window.location.search);
        const { type, keyword } = getSearchCondition();

        params.set('page', '1');

        if (type) {
            params.set('type', type);
        } else {
            params.delete('type');
        }

        if (keyword) {
            params.set('keyword', keyword);
        } else {
            params.delete('keyword');
        }

        window.history.pushState(
            null,
            '',
            `${window.location.pathname}?${params.toString()}`
        );

        window.dispatchEvent(
            new CustomEvent('pagination:refresh', {
                detail: {
                    page: 1
                }
            })
        );
    });
}

function getSearchCondition() {
    const searchForm = document.searchForm;

    return {
        type: searchForm?.searchType?.value || null,
        keyword: searchForm?.keyword?.value.trim()  || null
    };
}

function errorHandler(error) {
    console.error('Error:', error);
    alert('상품 목록을 불러오지 못했습니다.');
}