// ===== 모달 스택 관리 (home.html과 동일) =====
    const modalStack = [];

    function openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;

        const current = document.querySelector('.modal-overlay.active');
        if (current && current.id !== modalId) {
            current.classList.remove('active');
            modalStack.push(current.id);
        }

        modal.classList.add('active');
    }

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        modal.classList.remove('active');

        if (modalStack.length > 0) {
            const prevId = modalStack.pop();
            const prevModal = document.getElementById(prevId);
            if (prevModal) prevModal.classList.add('active');
        }
    }

    document.querySelectorAll('[data-close]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            closeModal(btn.getAttribute('data-close'));
        });
    });

    document.querySelectorAll('.modal-overlay').forEach(function (overlay) {
        overlay.addEventListener('click', function (e) {
            if (e.target === overlay) {
                closeModal(overlay.id);
            }
        });
    });

    // 문의하기(사이드바) 클릭 → 문의하기 모달

    // =====================================================
    // ===== 쿠폰내역: 더미 데이터 / 기간별조회 =====
    // =====================================================

    const today = new Date();

    function formatDate(d) {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + day;
    }

    function formatComma(num) {
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

    function formatMD(d) {
        const m = d.getMonth() + 1;
        const day = d.getDate();
        return m + '/' + day;
    }

    // ---- 더미 쿠폰 데이터 생성 ----
    // 발급일 기준 최근 5개월 내 분산 배치, 유효기간은 발급일로부터 1개월 (시작~종료 MM/DD 형식)
    const couponTemplates = [
        { name: '1 만원 할인 쿠폰', amount: 10000, condition: '10,000원 이상 구매시' },
        { name: '회원가입 축하 5천원 할인쿠폰', amount: 5000, condition: '5,000원 이상 구매시' },
        { name: '신규입점 기념 3천원 할인쿠폰', amount: 3000, condition: '3,000원 이상 구매시' },
        { name: '재구매 감사 2천원 할인쿠폰', amount: 2000, condition: '2,000원 이상 구매시' },
        { name: '여름맞이 7천원 할인쿠폰', amount: 7000, condition: '7,000원 이상 구매시' }
    ];

    const couponData = [];

    for (let i = 0; i < 15; i++) {
        const issueDate = new Date(today);
        issueDate.setDate(issueDate.getDate() - i * 9); // 발급일을 9일 간격으로 과거로 분산 (최근 5개월 커버)

        const expireDate = new Date(issueDate);
        expireDate.setMonth(expireDate.getMonth() + 1); // 유효기간 최대 1개월

        const template = couponTemplates[i % couponTemplates.length];
        const isExpired = expireDate < today;

        couponData.push({
            issueDate: formatDate(issueDate),
            name: template.name,
            amount: template.amount,
            condition: template.condition,
            status: isExpired ? '사용불가' : '사용가능',
            validFrom: formatMD(issueDate),
            validTo: formatMD(expireDate)
        });
    }

    let filteredData = couponData.slice(); // 기간조회 필터 적용 결과 (기본: 전체)

    function renderCouponRows() {
        const tbody = document.getElementById('couponListBody');
        tbody.innerHTML = '';

        if (filteredData.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="5" class="order-empty">조회된 쿠폰내역이 없습니다.</td>';
            tbody.appendChild(emptyRow);
            return;
        }

        filteredData.forEach(function (item) {
            const tr = document.createElement('tr');
            const statusClass = item.status === '사용가능' ? 'coupon-status-active' : 'coupon-status-expired';

            tr.innerHTML = `
                <td class="coupon-name">${item.name}</td>
                <td class="coupon-amount">${formatComma(item.amount)}원</td>
                <td class="coupon-condition">${item.condition}</td>
                <td class="${statusClass}">${item.status}</td>
                <td class="coupon-period">${item.validFrom} ~ ${item.validTo}</td>
            `;

            tbody.appendChild(tr);
        });
    }

    // ---- 기간별조회 동작 (쿠폰 발급일 기준) ----
    document.querySelectorAll('input[name="periodType"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            document.getElementById('periodMonthSelect').value = '0';
            document.getElementById('periodStartDate').value = '';
            document.getElementById('periodEndDate').value = '';
        });
    });

    document.getElementById('periodMonthSelect').addEventListener('change', function () {
        document.querySelectorAll('input[name="periodType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('periodStartDate').value = '';
        document.getElementById('periodEndDate').value = '';
    });

    document.querySelectorAll('.period-date-input').forEach(function (input) {
        input.addEventListener('change', function () {
            document.querySelectorAll('input[name="periodType"]').forEach(function (radio) {
                radio.checked = false;
            });
            document.getElementById('periodMonthSelect').value = '0';
        });
    });

    document.getElementById('periodSearchBtn').addEventListener('click', function () {
        const checkedPeriod = document.querySelector('input[name="periodType"]:checked');
        const monthSelectValue = document.getElementById('periodMonthSelect').value;
        const startDateValue = document.getElementById('periodStartDate').value;
        const endDateValue = document.getElementById('periodEndDate').value;

        let rangeStart = null;
        let rangeEnd = null;

        if (startDateValue || endDateValue) {
            // 시작~종료 날짜 조회 (최대 1년치, 둘 다 입력된 경우만 동작)
            if (!startDateValue || !endDateValue) {
                alert('시작일과 종료일을 모두 선택해주세요.');
                return;
            }

            rangeStart = new Date(startDateValue);
            rangeEnd = new Date(endDateValue);

            if (rangeStart > rangeEnd) {
                alert('시작일이 종료일보다 늦을 수 없습니다.');
                return;
            }

            const oneYearLater = new Date(rangeStart);
            oneYearLater.setFullYear(oneYearLater.getFullYear() + 1);
            if (rangeEnd > oneYearLater) {
                alert('조회 기간은 최대 1년까지 가능합니다.');
                return;
            }
        } else if (checkedPeriod) {
            // 1주일 / 15일 / 1개월 단위 조회 (쿠폰 발급일 기준)
            const days = parseInt(checkedPeriod.getAttribute('data-days'), 10);
            rangeEnd = new Date(today);
            rangeStart = new Date(today);
            rangeStart.setDate(rangeStart.getDate() - days);
        } else {
            // 현재 월에서 최신 5개월까지 조회 (개월 select)
            const monthsAgo = parseInt(monthSelectValue, 10);
            rangeEnd = new Date(today);
            rangeStart = new Date(today);
            rangeStart.setMonth(rangeStart.getMonth() - (monthsAgo + 1));
        }

        filteredData = couponData.filter(function (item) {
            const itemDate = new Date(item.issueDate);
            return itemDate >= rangeStart && itemDate <= rangeEnd;
        });

        renderCouponRows();
    });

    // ---- 초기 렌더링 ----
    renderCouponRows();