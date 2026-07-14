document.addEventListener('DOMContentLoaded', () => {
    initSelectAll();
    initDeleteSelected();
    initOrderSelected();
});

function initSelectAll() {
    const checkAll = document.getElementById('chk-all');
    const itemChecks = getItemChecks();
    if (!checkAll || itemChecks.length === 0) return;

    checkAll.addEventListener('change', () => {
        itemChecks.forEach(check => {
            check.checked = checkAll.checked;
        });
    });

    itemChecks.forEach(check => {
        check.addEventListener('change', () => {
            checkAll.checked = getItemChecks().every(itemCheck => itemCheck.checked);
        });
    });
}

function initDeleteSelected() {
    const deleteButton = document.querySelector('.btn-delete-sel');
    if (!deleteButton) return;

    deleteButton.addEventListener('click', async () => {
        const cartNos = getSelectedCartNos();
        if (cartNos.length === 0) {
            alert('삭제할 상품을 선택해주세요.');
            return;
        }

        if (!confirm('선택한 상품을 장바구니에서 삭제하시겠습니까?')) {
            return;
        }

        const params = new URLSearchParams();
        cartNos.forEach(cartNo => params.append('cartNo', cartNo));

        const response = await fetch(`${getContextPath()}cart/api?${params.toString()}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const result = await response.json().catch(() => ({}));
            alert(result.message || '장바구니 상품 삭제에 실패했습니다.');
            return;
        }

        window.location.reload();
    });
}

function initOrderSelected() {
    const orderButton = document.getElementById('btnOrder');
    if (!orderButton) return;

    orderButton.addEventListener('click', () => {
        const cartNos = getSelectedCartNos();
        if (cartNos.length === 0) {
            alert('주문할 상품을 선택해주세요.');
            return;
        }

        const params = new URLSearchParams();
        cartNos.forEach(cartNo => params.append('cartNoList', cartNo));
        window.location.href = `${getContextPath()}product/order?${params.toString()}`;
    });
}

function getItemChecks() {
    return Array.from(document.querySelectorAll('.chk-item'));
}

function getSelectedCartNos() {
    return getItemChecks()
        .filter(check => check.checked)
        .map(check => Number(check.value))
        .filter(cartNo => Number.isInteger(cartNo) && cartNo > 0);
}

function getContextPath() {
    const path = window.location.pathname;
    const productIndex = path.indexOf('/product/');
    return productIndex >= 0 ? path.substring(0, productIndex + 1) : '/';
}
