import { formatWon } from './format.js';
import { escapeHtml } from '../global/htmlUtils.js';
import { initPagination } from '../global/pagination.js';

document.addEventListener('DOMContentLoaded', () => {
    initViewTabsScroll();
    initVariantSelection();
    initQuantityControl();
    initProductReviews();
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
            const response = await fetch(`${getContextPath()}cart/api`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    prodVariantId: selectedVariant.id,
                    count
                })
            });

            if (response.status === 401) {
                window.location.href = `${getContextPath()}cart`;
                return;
            }

            const result = await response.json().catch(() => ({}));
            if (!response.ok) {
                alert(result.message || '장바구니 담기에 실패했습니다.');
                return;
            }

            if (confirm('장바구니에 담았습니다. 장바구니로 이동하시겠습니까?')) {
                window.location.href = `${getContextPath()}cart`;
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

function initProductReviews() {
    const reviewSection = document.getElementById('review');
    const reviewList = document.querySelector('[data-review-list]');
    const pagination = document.querySelector('[data-review-pagination]');
    const productNo = Number(reviewSection?.dataset.productNo || 0);

    if (!reviewSection || !reviewList || !pagination || !productNo) return;

    initProductReviewDetailModal(reviewList);

    initPagination({
        selector: '[data-review-pagination]',
        fetchPage: page => loadProductReviews(productNo, page, reviewList),
        updateUrl: false,
        onError: error => {
            console.error(error);
            alert('상품평을 불러오지 못했습니다.');
            reviewList.innerHTML = '<div class="review-empty">상품평을 불러오지 못했습니다.</div>';
            pagination.innerHTML = '';
        }
    });
}

async function loadProductReviews(productNo, page, reviewList) {
    const params = new URLSearchParams();
    params.set('page', page);

    const response = await fetch(
        `${getContextPath()}product/api/${encodeURIComponent(productNo)}/reviews?${params.toString()}`,
        { headers: { Accept: 'application/json' } }
    );

    if (!response.ok) {
        throw new Error(`상품 리뷰 조회 실패: ${response.status}`);
    }

    const pageData = await response.json();
    renderProductReviews(pageData, reviewList);
    return pageData;
}

function renderProductReviews(pageData, reviewList) {
    const reviews = pageData?.list || [];

    if (reviews.length === 0) {
        reviewList.innerHTML = '<div class="review-empty">작성한 상품평이 없습니다.</div>';
        return;
    }

    reviewList.innerHTML = reviews
        .map(review => `
            <div class="review-item"
                 data-review-detail
                 data-review-no="${escapeHtml(review.reviewNo)}"
                 data-product-no="${escapeHtml(review.productNo)}"
                 data-product-name="${escapeHtml(review.productName)}"
                 data-member-uid="${escapeHtml(review.memberUid || '')}"
                 data-rating="${escapeHtml(review.rating)}"
                 data-created-at="${escapeHtml(review.createdAt)}"
                 data-content="${escapeHtml(review.content)}">
                <div class="thumb"><span class="dummy-img">📷</span></div>
                <div class="meta">
                    <span class="stars">${buildStars(review.rating)}</span>
                    <span>${escapeHtml(review.memberUid || '')}</span>
                    <span>${escapeHtml(review.createdAt)}</span>
                </div>
                <div class="text">${escapeHtml(review.content)}</div>
            </div>
        `)
        .join('');
}

function initProductReviewDetailModal(reviewList) {
    const modal = document.getElementById('productReviewDetailModal');
    if (!modal) return;

    reviewList.addEventListener('click', event => {
        const item = event.target.closest('[data-review-detail]');
        if (!item) return;

        document.getElementById('productReviewDetailStars').textContent = buildStars(item.dataset.rating);
        document.getElementById('productReviewDetailMember').textContent = item.dataset.memberUid || '';
        document.getElementById('productReviewDetailDate').textContent = item.dataset.createdAt || '';
        document.getElementById('productReviewDetailProductNo').textContent = item.dataset.productNo || '';
        document.getElementById('productReviewDetailProductName').textContent = item.dataset.productName || '';
        document.getElementById('productReviewDetailContent').textContent = item.dataset.content || '';

        modal.hidden = false;
        document.body.classList.add('modal-open');
    });

    modal.querySelector('[data-review-detail-close]')?.addEventListener('click', () => {
        closeProductReviewDetailModal(modal);
    });

    modal.addEventListener('click', event => {
        if (event.target === modal) {
            closeProductReviewDetailModal(modal);
        }
    });
}

function closeProductReviewDetailModal(modal) {
    modal.hidden = true;
    document.body.classList.remove('modal-open');
}

function buildStars(rating) {
    const score = Number(rating || 0);
    let stars = '';

    for (let index = 1; index <= 5; index++) {
        stars += index <= score ? '★' : '☆';
    }

    return stars;
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
