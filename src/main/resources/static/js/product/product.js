document.addEventListener('DOMContentLoaded', function () {
    // 1. 상품 상세 화면(view.html) 스티키 탭 스무스 스크롤 기능 바인딩
    initViewTabsScroll();

    // 2. 상품 상세 화면(view.html) 수량 조절 및 총 금액 계산 기능 바인딩
    initQuantityControl();

    // 3. 상품 상세 화면(view.html) 리뷰 페이지네이션 기능 바인딩
    initReviewPagination();

    // 4. 상품 상세 화면(view.html) 장바구니 및 구매하기 버튼 페이지 이동 바인딩
    initCartAndBuyButtons();
});


/* ─────────────────────────────────────────────
   상품 상세(view.html) 탭 스무스 스크롤 및 Scroll Spy
───────────────────────────────────────────── */
function initViewTabsScroll() {
    const viewTabs = document.querySelectorAll('.view-tabs a');
    if (viewTabs.length === 0) return;

    // 1. 클릭 시 부드럽게 스크롤 이동
    viewTabs.forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            const targetSection = document.querySelector(targetId);

            if (!targetSection) return;

            // 클릭할 때 일단 즉각적으로 active 변경
            viewTabs.forEach(a => a.classList.remove('active'));
            this.classList.add('active');

            // 탭 메뉴 전체 컨테이너와 높이 가져오기
            const tabsEl = document.querySelector('.view-tabs');
            const tabsTop = parseInt(getComputedStyle(tabsEl).top, 10) || 0;
            const tabsHeight = tabsEl.offsetHeight;

            // 정확한 목표 스크롤 위치 계산
            const offsetTop = targetSection.getBoundingClientRect().top + window.scrollY - tabsTop - tabsHeight - 16;

            window.scrollTo({
                top: offsetTop,
                behavior: 'smooth'
            });
        });
    });

    // 2. 스크롤 감지 - IntersectionObserver를 활용하여 현재 섹션 불 켜기
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                // 진입한 섹션의 id (예: 'detail', 'notice', 'review')
                const id = entry.target.id;

                // 해당 id를 href('#id')로 가진 탭만 active 클래스 토글
                viewTabs.forEach(tab => {
                    tab.classList.toggle('active', tab.getAttribute('href') === `#${id}`);
                });
            }
        });
    }, { rootMargin: '-30% 0px -60% 0px' }); // 화면의 중앙 부분에 닿을 때 감지

    // 각 탭이 가리키는 섹션(article)들을 찾아서 관찰(observe) 대상에 추가
    viewTabs.forEach(tab => {
        const targetId = tab.getAttribute('href');
        const targetSection = document.querySelector(targetId);
        if (targetSection) {
            observer.observe(targetSection);
        }
    });
}


/* ─────────────────────────────────────────────
   상품 상세(view.html) 수량 조절 및 총 금액 계산
───────────────────────────────────────────── */
function initQuantityControl() {
    const countControl = document.querySelector('.count-control');
    // 해당 요소가 없으면(다른 페이지면) 함수 종료
    if (!countControl) return;

    // 💡 실제로는 서버에서 전달받은 단가를 넣어야 함
    const UNIT_PRICE = 62300;

    const minusBtn = countControl.querySelector('button:first-child');
    const plusBtn  = countControl.querySelector('button:last-child');
    const countInput = countControl.querySelector('input');
    const totalEl = document.querySelector('.total-price-wrap .total');

    function renderTotal(count) {
        const total = UNIT_PRICE * count;
        if (totalEl) {
            totalEl.innerHTML = total.toLocaleString() + '<span>원</span>';
        }
    }

    function getCount() {
        return parseInt(countInput.value, 10) || 1;
    }

    if (minusBtn && plusBtn && countInput) {
        minusBtn.addEventListener('click', () => {
            const next = Math.max(1, getCount() - 1);
            countInput.value = next;
            renderTotal(next);
        });

        plusBtn.addEventListener('click', () => {
            const next = getCount() + 1;
            countInput.value = next;
            renderTotal(next);
        });
    }
}


/* ─────────────────────────────────────────────
   상품 상세(view.html) 리뷰 페이지네이션 기능 (더미)
───────────────────────────────────────────── */
function initReviewPagination() {
    const pagination = document.querySelector('.review-pagination');
    if (!pagination) return;

    const allLinks = pagination.querySelectorAll('a');

    allLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();

            const numberLinks = Array.from(pagination.querySelectorAll('a:not(.page-nav)'));

            if (this.classList.contains('page-nav')) {
                const currentIndex = numberLinks.findIndex(l => l.classList.contains('active'));
                const isPrev = this.textContent.includes('이전');
                let nextIndex = isPrev ? currentIndex - 1 : currentIndex + 1;
                nextIndex = Math.max(0, Math.min(nextIndex, numberLinks.length - 1));

                numberLinks.forEach(l => l.classList.remove('active'));
                numberLinks[nextIndex].classList.add('active');
            } else {
                numberLinks.forEach(l => l.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });
}

/* ─────────────────────────────────────────────
   상품 결제(order.html)
───────────────────────────────────────────── */

// ===== 상태 저장 =====
let appliedPoint = 0;
let appliedCouponDiscount = 0;
let appliedCouponFreeShipping = false;

// ===== 유틸: 콤마 포맷 =====
function formatWon(num) {
    return num.toLocaleString('ko-KR') + '원';
}

// ===== 기준 금액 읽기 (서버가 내려준 raw 값) =====
function getBaseAmounts() {
    const productTotal = parseInt(document.getElementById('summaryProductPrice').dataset.raw, 10);
    const discountTotal = parseInt(document.getElementById('summaryDiscount').dataset.raw, 10);
    const shippingTotal = parseInt(document.getElementById('summaryShipping').dataset.raw, 10);
    return { productTotal, discountTotal, shippingTotal };
}

// ===== 포인트 사용하기 =====
document.getElementById('btnApplyPoint').addEventListener('click', () => {
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

    // 사용하려면 5,000원 이상, 아예 안 쓰는 건(0) 허용
    if (inputValue > 0 && inputValue < 5000) {
        alert('포인트는 5,000원 이상부터 사용할 수 있습니다.');
        return;
    }

    const { productTotal, discountTotal } = getBaseAmounts();

    // 전체주문금액(배송비 제외, 쿠폰할인 반영) 기준
    let orderTotalBeforePoint = productTotal - discountTotal - appliedCouponDiscount;
    if (orderTotalBeforePoint < 0) orderTotalBeforePoint = 0;

    if (inputValue > orderTotalBeforePoint) {
        alert('전체주문금액을 초과하여 포인트를 사용할 수 없습니다.');
        return;
    }

    appliedPoint = inputValue;
    recalculateSummary();
});

// ===== 쿠폰 사용하기 =====
document.getElementById('btnApplyCoupon').addEventListener('click', () => {
    const select = document.getElementById('couponSelect');
    const selected = select.options[select.selectedIndex];

    console.log('value:', selected.value);
    console.log('couponType:', selected.dataset.couponType);
    console.log('benefit:', selected.dataset.benefit);

    if (!selected.value) {
        appliedCouponDiscount = 0;
        appliedCouponFreeShipping = false;
        recalculateSummary();
        return;
    }

    const couponType = selected.dataset.couponType; // "ALL", "PRODUCT" 등
    const benefit = selected.dataset.benefit; // "1000", "10", "DELIVERY_FREE"

    // ===== PRODUCT 타입: 선택한 상품 1개 수량분만 기준으로 계산 =====
    if (couponType === 'PRODUCT') {
        const targetSelect = document.getElementById('couponTargetProduct');

        if (!targetSelect.value) {
            alert('쿠폰을 적용할 상품을 선택해주세요.');
            return;
        }

        const targetVariantId = targetSelect.value;
        const targetItem = orderItems.find(
            item => String(item.prodVariantId) === String(targetVariantId)
        );

        if (!targetItem) {
            alert('선택한 상품을 찾을 수 없습니다.');
            return;
        }

        // 상품 자체 할인율을 반영한 실제 판매가(할인 후 단가)
        const itemDiscountRate = targetItem.discountRate || 0;
        const discountedUnitPrice = Math.floor(targetItem.price * (100 - itemDiscountRate) / 100);

        // 수량 전체 기준으로 계산 (개당 X, 전체 수량분)
        const baseAmount = discountedUnitPrice * targetItem.quantity;

        if (benefit === 'DELIVERY_FREE') {
            appliedCouponDiscount = 0;
            appliedCouponFreeShipping = true;
        } else if (benefit.length <= 2) {
            const rate = parseInt(benefit, 10);
            appliedCouponDiscount = Math.floor(baseAmount * rate / 100);
            appliedCouponFreeShipping = false;
        } else {
            appliedCouponDiscount = parseInt(benefit, 10);
            appliedCouponFreeShipping = false;
        }

        recalculateSummary();
        return;
    }

    // ===== ALL 등 기존 로직 =====
    const { productTotal, discountTotal } = getBaseAmounts();
    const priceAfterProductDiscount = productTotal - discountTotal;

    if (benefit === 'DELIVERY_FREE') {
        appliedCouponDiscount = 0;
        appliedCouponFreeShipping = true;
    } else if (benefit.length <= 2) {
        const rate = parseInt(benefit, 10);
        appliedCouponDiscount = Math.floor(priceAfterProductDiscount * rate / 100);
        appliedCouponFreeShipping = false;
    } else {
        appliedCouponDiscount = parseInt(benefit, 10);
        appliedCouponFreeShipping = false;
    }

    recalculateSummary();
});

document.getElementById('couponSelect').addEventListener('change', function () {
    const selected = this.options[this.selectedIndex];
    const couponType = selected.getAttribute('data-coupon-type');
    const sellerUid = selected.getAttribute('data-seller-uid');

    const targetRow = document.getElementById('couponTargetProductRow');
    const targetSelect = document.getElementById('couponTargetProduct');

    targetSelect.innerHTML = '<option value="">상품 선택</option>';

    if (couponType === 'PRODUCT' && this.value !== '') {
        // 같은 sellerUid를 가진 주문 상품만 필터링
        const candidates = orderItems.filter(item => item.sellerUid === sellerUid);

        candidates.forEach(item => {
            const opt = document.createElement('option');
            opt.value = item.prodVariantId; // 또는 prodNo
            opt.textContent = `${item.productName} (${item.optionText || ''}) - 개당 ${item.price.toLocaleString()}원`;
            targetSelect.appendChild(opt);
        });

        // 후보가 1개뿐이면 자동 선택
        if (candidates.length === 1) {
            targetSelect.value = candidates[0].prodVariantId;
        }

        targetRow.style.display = 'flex';
    } else {
        targetRow.style.display = 'none';
    }
});

function recalculateSummary() {
    const { productTotal, discountTotal, shippingTotal } = getBaseAmounts();

    // 배송비 제외한 순수 주문금액 (상품금액 - 상품할인 - 쿠폰할인 - 포인트)
    let orderTotal = productTotal - discountTotal - appliedCouponDiscount - appliedPoint;
    if (orderTotal < 0) orderTotal = 0;

    const effectiveShipping = appliedCouponFreeShipping ? 0 : shippingTotal;

    // 최종결제금액 = 순수 주문금액 + 배송비
    const finalPrice = orderTotal + effectiveShipping;

    document.getElementById('summaryShipping').textContent = formatWon(effectiveShipping);
    document.getElementById('summaryOrderTotal').textContent = formatWon(orderTotal);
    document.getElementById('summaryCouponDiscount').textContent = '-' + formatWon(appliedCouponDiscount);
    document.getElementById('summaryUsedPoint').textContent = '-' + formatWon(appliedPoint);
    document.getElementById('summaryFinalPrice').textContent = formatWon(finalPrice);

    // 쿠폰/포인트 행 각각 숨김 토글
    document.getElementById('summaryCouponRow').classList.toggle('hidden', appliedCouponDiscount === 0);
    document.getElementById('summaryUsedPointRow').classList.toggle('hidden', appliedPoint === 0);

    // 적립포인트는 상품 자체 point 합계라 여기선 안 바뀜 (그대로 둠)
}

document.getElementById('btnPay').addEventListener('click', async () => {
    const select = document.getElementById('couponSelect');
    const couponIssueId = select.value ? parseInt(select.value, 10) : null;

    const targetSelect = document.getElementById('couponTargetProduct');
    const targetVariantId = targetSelect.value ? parseInt(targetSelect.value, 10) : null;

    const usedPoints = appliedPoint;
    const payMethod = document.querySelector('input[name="payMethod"]:checked').value;

    const items = orderItems.map(item => ({
        prodVariantId: item.prodVariantId,
        count: item.quantity
    }));

    const orderData = {
        receiver: document.getElementById('recvName').value,
        phone: document.getElementById('recvPhone').value,
        zipCode: document.getElementById('recvZip').value,
        addr1: document.getElementById('recvAddr1').value,
        addr2: document.getElementById('recvAddr2').value,
        orderNote: document.getElementById('recvMemo').value,
        couponIssueId: couponIssueId,
        targetVariantId: targetVariantId,
        usedPoints: usedPoints,
        payMethod: payMethod,
        items: items
    };

    try {
        const response = await fetch(CONTEXT_PATH + 'product/api/order', {
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
        window.location.href = `/K_Market/product/complete?orderNo=${result.orderNo}`;

    } catch (err) {
        console.error(err);
        alert('네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
    }
});

/* ─────────────────────────────────────────────
   상품 상세(view.html) 장바구니/바로구매 버튼 이동
───────────────────────────────────────────── */
function initCartAndBuyButtons() {
    const btnCart = document.querySelector('.btn-cart');
    const btnBuy = document.querySelector('.btn-buy');
    const btnOrder = document.querySelector('.btn-order')
    const btnPay = document.querySelector('.btn-pay')

    if (btnCart) {
        btnCart.addEventListener('click', function() {
            fetch(CONTEXT_PATH + 'product/api/member/me')
                .then(res => {
                    if (!res.ok) throw new Error('unauthorized');
                    return res.json();
                })
                .then(() => {
                    // 로그인 상태 확인됨 → 정상적으로 장바구니 담기 처리
                    alert('상품이 장바구니에 담겼습니다.');
                    window.location.href = '/K_Market/product/cart';
                })
                .catch(() => {
                    // 비로그인 상태 → 인터셉터와 동일한 방식으로 이동
                    window.location.href = '/K_Market/product/cart';
                });
        });
    }

    if (btnBuy) {
        btnBuy.addEventListener('click', function() {
            // 바로 구매 버튼 클릭 시 바로 경로 이동
            window.location.href = '/K_Market/product/order'; // 주문결제 화면으로 이동
        });
    }

    if (btnOrder){
        btnOrder.addEventListener('click', function (){
            window.location.href = '/K_Market/product/order';
        })
    }
}

/**
 * 카카오 우편번호 함수
 */
function postcode() {

    new kakao.Postcode({

        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if(data.userSelectedType === 'R'){
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if(data.bname !== '' && /[동로가]$/.test(data.bname)){
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                //document.getElementById("sample6_extraAddress").value = extraAddr;

            } else {
                //document.getElementById("sample6_extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('recvZip').value = data.zonecode;
            document.getElementById("recvAddr1").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("recvAddr2").focus();
        }
    }).open();
}