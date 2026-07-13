import { formatWon } from './format.js';

document.addEventListener('DOMContentLoaded', () => {
    initViewTabsScroll();
    initVariantSelection();
    initQuantityControl();
    initPurchaseButtons();
});

let selectedVariant = null;

function initViewTabsScroll() {
    const viewTabs = document.querySelectorAll('.view-tabs a');
    if (viewTabs.length === 0) return;

    viewTabs.forEach(anchor => {
        anchor.addEventListener('click', event => {
            event.preventDefault();

            const targetSection = document.querySelector(anchor.getAttribute('href'));
            if (!targetSection) return;

            viewTabs.forEach(tab => tab.classList.remove('active'));
            anchor.classList.add('active');

            const tabsEl = document.querySelector('.view-tabs');
            const tabsTop = parseInt(getComputedStyle(tabsEl).top, 10) || 0;
            const offsetTop = targetSection.getBoundingClientRect().top
                + window.scrollY
                - tabsTop
                - tabsEl.offsetHeight
                - 16;

            window.scrollTo({ top: offsetTop, behavior: 'smooth' });
        });
    });

    const observer = new IntersectionObserver(entries => {
        entries.forEach(entry => {
            if (!entry.isIntersecting) return;

            viewTabs.forEach(tab => {
                tab.classList.toggle('active', tab.getAttribute('href') === `#${entry.target.id}`);
            });
        });
    }, { rootMargin: '-30% 0px -60% 0px' });

    viewTabs.forEach(tab => {
        const targetSection = document.querySelector(tab.getAttribute('href'));
        if (targetSection) {
            observer.observe(targetSection);
        }
    });
}

function initQuantityControl() {
    const countControl = document.querySelector('.count-control');
    if (!countControl) return;

    const unitPrice = Number(countControl.dataset.unitPrice || 0);
    const minusButton = countControl.querySelector('button:first-child');
    const plusButton = countControl.querySelector('button:last-child');
    const countInput = countControl.querySelector('input');

    if (!minusButton || !plusButton || !countInput) return;

    const getCount = () => parseInt(countInput.value, 10) || 1;
    const getMaxStock = () => Number(countControl.dataset.stock || 0);

    minusButton.addEventListener('click', () => {
        const next = Math.max(1, getCount() - 1);
        countInput.value = String(next);
        renderTotalPrice(unitPrice, next);
    });

    plusButton.addEventListener('click', () => {
        const maxStock = getMaxStock();
        const next = maxStock > 0 ? Math.min(maxStock, getCount() + 1) : getCount();
        countInput.value = String(next);
        renderTotalPrice(unitPrice, next);
    });
}

function initVariantSelection() {
    const variants = getProductVariants();
    const optionSelects = Array.from(document.querySelectorAll('[data-option-select]'));
    const hasOptions = optionSelects.length > 0;
    const defaultVariant = variants.find(variant => getVariantItemIds(variant).length === 0) || variants[0] || null;

    if (!hasOptions) {
        selectedVariant = defaultVariant;
        applyVariantState(defaultVariant, true);
        return;
    }

    applyVariantState(null, false);

    optionSelects.forEach(select => {
        select.addEventListener('change', () => {
            const selectedItemIds = optionSelects
                .map(optionSelect => Number(optionSelect.value))
                .filter(id => Number.isInteger(id) && id > 0)
                .sort((left, right) => left - right);

            if (selectedItemIds.length !== optionSelects.length) {
                selectedVariant = null;
                applyVariantState(null, false);
                return;
            }

            selectedVariant = variants.find(variant =>
                sameIds(getVariantItemIds(variant), selectedItemIds)
            ) || null;

            applyVariantState(selectedVariant, true);
        });
    });
}

function applyVariantState(variant, selectionComplete) {
    const stockEl = document.getElementById('variantStock');
    const countControl = document.querySelector('.count-control');
    const countInput = countControl?.querySelector('input');
    const cartButton = document.querySelector('.btn-cart');
    const buyButton = document.querySelector('.btn-buy');
    const optionMessage = document.querySelector('[data-option-message]');
    const stock = Number(variant?.stock || 0);
    const isOnSale = variant?.status == null || variant.status === 'ON_SALE';
    const purchasable = selectionComplete && variant != null && isOnSale && stock > 0;

    if (stockEl) {
        if (!selectionComplete) {
            stockEl.textContent = '옵션을 선택해주세요.';
        } else if (!variant) {
            stockEl.textContent = '선택 가능한 조합이 없습니다.';
        } else if (!isOnSale || stock <= 0) {
            stockEl.textContent = '품절';
        } else {
            stockEl.textContent = `${stock.toLocaleString('ko-KR')}개`;
        }
    }

    if (countControl) {
        countControl.dataset.stock = String(purchasable ? stock : 0);
    }
    if (countInput) {
        countInput.value = '1';
        renderTotalPrice(getUnitPrice(), 1);
    }

    [cartButton, buyButton].forEach(button => {
        if (button) {
            button.disabled = !purchasable;
        }
    });

    if (optionMessage) {
        optionMessage.hidden = selectionComplete && variant != null;
    }
}

function initPurchaseButtons() {
    const cartButton = document.querySelector('.btn-cart');
    const buyButton = document.querySelector('.btn-buy');

    if (cartButton) {
        cartButton.addEventListener('click', async () => {
            if (cartButton.disabled) return;
            if (!selectedVariant) {
                alert('옵션을 선택해주세요.');
                return;
            }

            const count = Number(document.querySelector('.count-control input')?.value || 1);
            const response = await fetch(`${getContextPath()}product/api/cart`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    prodVariantId: selectedVariant.id,
                    count
                })
            });

            if (response.status === 401) {
                window.location.href = `${getContextPath()}product/cart`;
                return;
            }

            const result = await response.json().catch(() => ({}));
            if (!response.ok) {
                alert(result.message || '장바구니 담기에 실패했습니다.');
                return;
            }

            if (confirm('장바구니에 담았습니다. 장바구니로 이동하시겠습니까?')) {
                window.location.href = `${getContextPath()}product/cart`;
            }
        });
    }

    if (buyButton) {
        buyButton.addEventListener('click', () => {
            if (buyButton.disabled) return;
            if (!selectedVariant) {
                alert('옵션을 선택해주세요.');
                return;
            }

            const count = document.querySelector('.count-control input')?.value || '1';
            window.location.href = `${getContextPath()}product/order?prodVariantId=${selectedVariant.id}&count=${count}`;
        });
    }
}

function getProductVariants() {
    return Array.isArray(window.productVariants) ? window.productVariants : [];
}

function getUnitPrice() {
    return Number(document.querySelector('.count-control')?.dataset.unitPrice || 0);
}

function renderTotalPrice(unitPrice, count) {
    const totalEl = document.querySelector('.total-price-wrap .total');
    if (!totalEl) return;

    totalEl.innerHTML = `${formatWon(unitPrice * count).replace('원', '')}<span>원</span>`;
}

function getVariantItemIds(variant) {
    return (variant?.items || [])
        .map(item => Number(item.id))
        .filter(id => Number.isInteger(id) && id > 0)
        .sort((left, right) => left - right);
}

function sameIds(left, right) {
    return left.length === right.length
        && left.every((id, index) => id === right[index]);
}

function getContextPath() {
    const contextPath = document.body.dataset.contextPath;
    if (contextPath) return contextPath;

    const path = window.location.pathname;
    const productIndex = path.indexOf('/product/');
    return productIndex >= 0 ? path.substring(0, productIndex + 1) : '/';
}
