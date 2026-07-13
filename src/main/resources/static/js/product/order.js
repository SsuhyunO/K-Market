import { formatWon } from './format.js';

let appliedPoint = 0;
let appliedCouponDiscount = 0;
let appliedCouponFreeShipping = false;

document.addEventListener('DOMContentLoaded', () => {
    initPointDiscount();
    initCouponDiscount();
    initPayment();
    initAddressSearch();
});

function initPointDiscount() {
    const button = document.getElementById('btnApplyPoint');
    if (!button) return;

    button.addEventListener('click', () => {
        const usePointInput = document.getElementById('usePoint');
        const availablePoint = parseInt(usePointInput.dataset.availablePoint, 10);
        const inputValue = parseInt(usePointInput.value, 10) || 0;

        if (inputValue < 0) {
            alert('포인트는 0 이상 입력해주세요.');
            return;
        }
        if (inputValue > availablePoint) {
            alert('보유 포인트를 초과했습니다.');
            return;
        }
        if (inputValue > 0 && inputValue < 5000) {
            alert('포인트는 5,000원 이상부터 사용할 수 있습니다.');
            return;
        }

        const { productTotal, discountTotal } = getBaseAmounts();
        const orderTotalBeforePoint = Math.max(productTotal - discountTotal - appliedCouponDiscount, 0);

        if (inputValue > orderTotalBeforePoint) {
            alert('전체주문금액을 초과하여 포인트를 사용할 수 없습니다.');
            return;
        }

        appliedPoint = inputValue;
        recalculateSummary();
    });
}

function initCouponDiscount() {
    const applyButton = document.getElementById('btnApplyCoupon');
    const couponSelect = document.getElementById('couponSelect');

    if (applyButton) {
        applyButton.addEventListener('click', applyCoupon);
    }
    if (couponSelect) {
        couponSelect.addEventListener('change', updateCouponTargetProducts);
    }
}

function applyCoupon() {
    const select = document.getElementById('couponSelect');
    const selected = select.options[select.selectedIndex];

    if (!selected.value) {
        appliedCouponDiscount = 0;
        appliedCouponFreeShipping = false;
        recalculateSummary();
        return;
    }

    const couponType = selected.dataset.couponType;
    const benefit = selected.dataset.benefit;

    if (couponType === 'PRODUCT') {
        applyProductCoupon(benefit);
        return;
    }

    const { productTotal, discountTotal } = getBaseAmounts();
    applyBenefit(productTotal - discountTotal, benefit);
    recalculateSummary();
}

function applyProductCoupon(benefit) {
    const targetSelect = document.getElementById('couponTargetProduct');

    if (!targetSelect.value) {
        alert('쿠폰을 적용할 상품을 선택해주세요.');
        return;
    }

    const targetItem = getOrderItems().find(
        item => String(item.prodVariantId) === String(targetSelect.value)
    );

    if (!targetItem) {
        alert('선택한 상품을 찾을 수 없습니다.');
        return;
    }

    const discountRate = targetItem.discountRate || 0;
    const discountedUnitPrice = Math.floor(targetItem.price * (100 - discountRate) / 100);
    applyBenefit(discountedUnitPrice * targetItem.quantity, benefit);
    recalculateSummary();
}

function applyBenefit(baseAmount, benefit) {
    if (benefit === 'DELIVERY_FREE') {
        appliedCouponDiscount = 0;
        appliedCouponFreeShipping = true;
        return;
    }

    if (benefit.length <= 2) {
        appliedCouponDiscount = Math.floor(baseAmount * parseInt(benefit, 10) / 100);
    } else {
        appliedCouponDiscount = parseInt(benefit, 10);
    }
    appliedCouponFreeShipping = false;
}

function updateCouponTargetProducts() {
    const selected = this.options[this.selectedIndex];
    const couponType = selected.getAttribute('data-coupon-type');
    const sellerUid = selected.getAttribute('data-seller-uid');
    const targetRow = document.getElementById('couponTargetProductRow');
    const targetSelect = document.getElementById('couponTargetProduct');

    targetSelect.innerHTML = '<option value="">상품 선택</option>';

    if (couponType !== 'PRODUCT' || this.value === '') {
        targetRow.style.display = 'none';
        return;
    }

    const candidates = getOrderItems().filter(item => item.sellerUid === sellerUid);
    candidates.forEach(item => {
        const option = document.createElement('option');
        option.value = item.prodVariantId;
        option.textContent = `${item.productName} (${item.optionText || ''}) - 개당 ${item.price.toLocaleString()}원`;
        targetSelect.appendChild(option);
    });

    if (candidates.length === 1) {
        targetSelect.value = candidates[0].prodVariantId;
    }

    targetRow.style.display = 'flex';
}

function recalculateSummary() {
    const { productTotal, discountTotal, shippingTotal } = getBaseAmounts();
    const orderTotal = Math.max(productTotal - discountTotal - appliedCouponDiscount - appliedPoint, 0);
    const effectiveShipping = appliedCouponFreeShipping ? 0 : shippingTotal;
    const finalPrice = orderTotal + effectiveShipping;

    document.getElementById('summaryShipping').textContent = formatWon(effectiveShipping);
    document.getElementById('summaryOrderTotal').textContent = formatWon(orderTotal);
    document.getElementById('summaryCouponDiscount').textContent = '-' + formatWon(appliedCouponDiscount);
    document.getElementById('summaryUsedPoint').textContent = '-' + formatWon(appliedPoint);
    document.getElementById('summaryFinalPrice').textContent = formatWon(finalPrice);

    document.getElementById('summaryCouponRow').classList.toggle('hidden', appliedCouponDiscount === 0);
    document.getElementById('summaryUsedPointRow').classList.toggle('hidden', appliedPoint === 0);
}

function initPayment() {
    const payButton = document.getElementById('btnPay');
    if (!payButton) return;

    payButton.addEventListener('click', async () => {
        const select = document.getElementById('couponSelect');
        const targetSelect = document.getElementById('couponTargetProduct');
        const checkedPayMethod = document.querySelector('input[name="payMethod"]:checked');

        const orderData = {
            receiver: document.getElementById('recvName').value,
            phone: document.getElementById('recvPhone').value,
            zipCode: document.getElementById('recvZip').value,
            addr1: document.getElementById('recvAddr1').value,
            addr2: document.getElementById('recvAddr2').value,
            orderNote: document.getElementById('recvMemo').value,
            couponIssueId: select.value ? parseInt(select.value, 10) : null,
            targetVariantId: targetSelect.value ? parseInt(targetSelect.value, 10) : null,
            usedPoints: appliedPoint,
            payMethod: checkedPayMethod?.value,
            items: getOrderItems().map(item => ({
                prodVariantId: item.prodVariantId,
                count: item.quantity
            }))
        };

        try {
            const response = await fetch(`${getContextPath()}product/api/order`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderData)
            });

            if (!response.ok) {
                const errorBody = await response.json();
                alert(errorBody.message || '주문 처리 중 오류가 발생했습니다.');
                return;
            }

            const result = await response.json();
            window.location.href = `${getContextPath()}product/complete?orderNo=${result.orderNo}`;
        } catch (error) {
            console.error(error);
            alert('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
        }
    });
}

function initAddressSearch() {
    const button = document.getElementById('btnSearchAddr');
    if (!button) return;

    button.addEventListener('click', openPostcode);
}

function openPostcode() {
    new daum.Postcode({
        oncomplete(data) {
            const addr = data.userSelectedType === 'R' ? data.roadAddress : data.jibunAddress;

            document.getElementById('recvZip').value = data.zonecode;
            document.getElementById('recvAddr1').value = addr;
            document.getElementById('recvAddr2').focus();
        }
    }).open();
}

function getBaseAmounts() {
    return {
        productTotal: parseInt(document.getElementById('summaryProductPrice').dataset.raw, 10),
        discountTotal: parseInt(document.getElementById('summaryDiscount').dataset.raw, 10),
        shippingTotal: parseInt(document.getElementById('summaryShipping').dataset.raw, 10)
    };
}

function getOrderItems() {
    return window.orderItems || [];
}

function getContextPath() {
    return window.CONTEXT_PATH || '/';
}
