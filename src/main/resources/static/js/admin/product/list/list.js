import { ManagementTableForm } from '../../global/management-table-form.js';
import { initPagination } from '../../../global/pagination.js';
import { initProductDetailModal } from './productDetailModal.js';
import { renderProductRows } from './productTableRenderer.js';
import { getProducts, removeProducts } from '../productApi.js';

document.addEventListener('DOMContentLoaded', function () {
    ManagementTableForm.init();
    initProductDetailModal();
    initSearchForm();
    initRemoveForm();

    initPagination({
        fetchPage: loadProducts,
        onError: errorHandler
    });
});

async function loadProducts(page = 1) {
    const { type, keyword, uncategorizedOnly } = getSearchCondition();

    const pageData = await getProducts({
        page: page,
        searchType: type,
        keyword: keyword,
        uncategorizedOnly: uncategorizedOnly
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
        const { type, keyword, uncategorizedOnly } = getSearchCondition();

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

        if (uncategorizedOnly) {
            params.set('uncategorizedOnly', 'true');
        } else {
            params.delete('uncategorizedOnly');
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

function initRemoveForm() {
    const form = document.getElementById('management-table-form');
    if (!form) return;

    form.addEventListener('submit', async event => {
        event.preventDefault();

        const productNos = ManagementTableForm
            .getChecked(form, `input[name="${form.dataset.checkboxName}"]`)
            .map(checkbox => Number.parseInt(checkbox.value, 10))
            .filter(Number.isInteger);

        if (productNos.length === 0) {
            return;
        }

        try {
            const result = await removeProducts(productNos);
            alert(result.message || '상품이 삭제되었습니다.');
            window.dispatchEvent(new CustomEvent('pagination:refresh'));
        } catch (error) {
            console.error('Error:', error);
            alert(error.message || '상품 삭제에 실패했습니다.');
        }
    });
}

function getSearchCondition() {
    const searchForm = document.searchForm;
    const params = new URLSearchParams(window.location.search);
    const urlUncategorizedOnly = params.get('uncategorizedOnly') === 'true';

    return {
        type: searchForm?.searchType?.value || null,
        keyword: searchForm?.keyword?.value.trim()  || null,
        uncategorizedOnly: searchForm?.uncategorizedOnly?.checked ?? urlUncategorizedOnly
    };
}

function errorHandler(error) {
    console.error('Error:', error);
    alert('상품 목록을 불러오지 못했습니다.');
}
