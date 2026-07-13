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

    if (couponType === 'DELIVERY') {
        applyDeliveryCoupon();
        return;
    }

    // ORDER 타입: 전체 주문금액 기준 비율/정액 할인
    const { productTotal, discountTotal } = getBaseAmounts();
    applyOrderCoupon(productTotal - discountTotal, benefit);
}

function applyProductCoupon(benefit) {
    const targetSelect = document.getElementById('couponTargetProduct');
    const selectedProdNo = parseInt(targetSelect.value, 10);

    if (!selectedProdNo) {
        alert('쿠폰을 적용할 상품을 선택해주세요.');
        return;
    }

    // 💡 find 대신 filter를 사용하여 해당 prodNo를 가진 모든 옵션 상품을 배열로 가져옵니다.
    const targetItems = getOrderItems().filter(
        item => item.prodNo === selectedProdNo
    );

    if (!targetItems || targetItems.length === 0) {
        alert('선택한 상품을 찾을 수 없습니다.');
        return;
    }

    // 💡 PRODUCT 쿠폰은 배송비 무료 혜택이 없음 — 할인(비율/정액) 계산만 수행
    let baseAmount = 0;
    targetItems.forEach(item => {
        const discountRate = item.discountRate || 0;
        const discountedUnitPrice = Math.floor(item.price * (100 - discountRate) / 100);
        baseAmount += discountedUnitPrice * item.quantity;
    });

    appliedCouponDiscount = benefit.length <= 2
        ? Math.floor(baseAmount * parseInt(benefit, 10) / 100)
        : parseInt(benefit, 10);
    appliedCouponFreeShipping = false;

    recalculateSummary();
}

function applyOrderCoupon(baseAmount, benefit) {
    // 💡 ORDER 쿠폰도 배송비 무료 혜택이 없음 — 할인(비율/정액) 계산만 수행
    appliedCouponDiscount = benefit.length <= 2
        ? Math.floor(baseAmount * parseInt(benefit, 10) / 100)
        : parseInt(benefit, 10);
    appliedCouponFreeShipping = false;

    recalculateSummary();
}

function applyDeliveryCoupon() {
    // 💡 DELIVERY 쿠폰은 상품 할인 없이 배송비 전체를 무료로 만듦
    appliedCouponDiscount = 0;
    appliedCouponFreeShipping = true;

    recalculateSummary();
}

function updateCouponTargetProducts() {
    const selected = this.options[this.selectedIndex];
    const couponType = selected.getAttribute('data-coupon-type');
    const sellerUid = selected.getAttribute('data-seller-uid');
    const targetRow = document.getElementById('couponTargetProductRow');
    const targetSelect = document.getElementById('couponTargetProduct');

    targetSelect.innerHTML = '<option value="">적용할 상품 선택</option>';

    if (couponType !== 'PRODUCT' || this.value === '') {
        targetRow.style.display = 'none';
        return;
    }

    const candidates = getOrderItems().filter(item => item.sellerUid === sellerUid);

    // 💡 prodNo 중복 제거를 위한 맵 처리
    const uniqueProducts = [];
    const visitedProdNos = new Set();

    candidates.forEach(item => {
        if (!visitedProdNos.has(item.prodNo)) {
            visitedProdNos.add(item.prodNo);
            uniqueProducts.push(item);
        }
    });

    // 💡 옵션 단위가 아니라 상품 종류(prodNo) 단위로 option을 생성합니다.
    uniqueProducts.forEach(item => {
        const option = document.createElement('option');
        option.value = item.prodNo; // 👈 value를 prodNo로 지정!
        option.textContent = `[상품] ${item.productName}`;
        targetSelect.appendChild(option);
    });

    if (uniqueProducts.length === 1) {
        targetSelect.value = uniqueProducts[0].prodNo;
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

        // 💡 현재 targetSelect.value에는 prodNo가 들어있습니다.
        const selectedProdNo = targetSelect.value ? parseInt(targetSelect.value, 10) : null;
        let mappedVariantId = null;

        if (selectedProdNo) {
            // 장바구니 품목 중 해당 prodNo를 가진 첫 번째 아이템의 옵션 ID(prodVariantId)를 찾아냅니다.
            const matchedItem = getOrderItems().find(item => item.prodNo === selectedProdNo);
            if (matchedItem) {
                mappedVariantId = matchedItem.prodVariantId; // 👈 서버가 원하는 variantId 획득!
            }
        }

        const orderData = {
            receiver: document.getElementById('recvName').value,
            phone: document.getElementById('recvPhone').value,
            zipCode: document.getElementById('recvZip').value,
            addr1: document.getElementById('recvAddr1').value,
            addr2: document.getElementById('recvAddr2').value,
            orderNote: document.getElementById('recvMemo').value,
            couponIssueId: select.value ? parseInt(select.value, 10) : null,
            targetVariantId: mappedVariantId,
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