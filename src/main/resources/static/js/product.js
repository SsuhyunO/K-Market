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
   상품 상세(view.html) 장바구니/바로구매 버튼 이동
───────────────────────────────────────────── */
function initCartAndBuyButtons() {
    const btnCart = document.querySelector('.btn-cart');
    const btnBuy = document.querySelector('.btn-buy');
    const btnOrder = document.querySelector('.btn-order')
    const btnPay = document.querySelector('.btn-pay')

    if (btnCart) {
        btnCart.addEventListener('click', function() {
            // 장바구니 버튼 클릭 시 alert 띄운 후 경로 이동
            alert('상품이 장바구니에 담겼습니다.');
            window.location.href = '/K_Market/product/cart'; // 장바구니 화면으로 이동
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

    if (btnPay){
        btnPay.addEventListener('click', function (){
            window.location.href = '/K_Market/product/complete';
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