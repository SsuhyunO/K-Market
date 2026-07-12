import { renderProductRows } from './productRowRenderer.js';
import { getProducts } from './productApi.js'

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);

    const cateId = params.get('category');

    getProducts({
            page: 1,
            sortType: 'SALES',
            cateId: cateId
        })
        .then(renderProductRows)
        .catch(errorHandler);
});

function errorHandler(error) {
    console.log(`상품 목록을 불러올 수 없습니다. ${error}`);
    alert('상품 목록을 불러올 수 없습니다.');
}