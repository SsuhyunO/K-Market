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
    // ===== 포인트내역: 더미 데이터 / 기간조회 / 페이지네이션 =====
    // =====================================================

    const PAGE_SIZE = 10;
    const TOTAL_PAGES = 5;
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

    // ---- 더미 포인트내역 데이터 50건 생성 (페이지당 10건 x 5페이지) ----
    // 적립/사용 패턴이 섞여 나오도록 구성 (구분: 적립 / 사용)
    const pointTypeList = [
        { type: '적립', remark: '상품 구매확정', amountSign: 1 },
        { type: '적립', remark: '상품 구매확정', amountSign: 1 },
        { type: '사용', remark: '상품 구매 시 사용', amountSign: -1 },
        { type: '적립', remark: '리뷰 작성 적립', amountSign: 1 }
    ];

    const pointData = [];

    for (let i = 0; i < PAGE_SIZE * TOTAL_PAGES; i++) {
        const d = new Date(today);
        d.setDate(d.getDate() - i * 3); // 3일 간격으로 과거로 이동 (최근 5개월 커버)

        const expireDate = new Date(d);
        expireDate.setFullYear(expireDate.getFullYear() + 2); // 적립일 기준 2년 유효

        const pattern = pointTypeList[i % pointTypeList.length];
        const baseAmount = 100 + (i % 5) * 60; // 100~340 사이 금액 분산
        const amount = baseAmount * pattern.amountSign;
        const orderNo = '2022' + String(121001 - i).padStart(6, '0');

        pointData.push({
            date: formatDate(d),
            type: pattern.type,
            orderNo: orderNo,
            amount: amount,
            remark: pattern.remark,
            expire: formatDate(expireDate)
        });
    }

    // ---- 상태 ----
    let currentPage = 1;
    let filteredData = pointData.slice(); // 기간조회 필터 적용 결과 (기본: 전체)

    function renderPointRows() {
        const tbody = document.getElementById('pointListBody');
        tbody.innerHTML = '';

        const startIdx = (currentPage - 1) * PAGE_SIZE;
        const pageItems = filteredData.slice(startIdx, startIdx + PAGE_SIZE);

        if (pageItems.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="6" class="order-empty">조회된 포인트내역이 없습니다.</td>';
            tbody.appendChild(emptyRow);
            return;
        }

        pageItems.forEach(function (item) {
            const tr = document.createElement('tr');
            const isPlus = item.amount >= 0;
            const amountText = (isPlus ? '+' : '-') + formatComma(Math.abs(item.amount));
            const amountClass = isPlus ? 'point-plus' : 'point-minus';

            tr.innerHTML = `
                <td>${item.date}</td>
                <td>${item.type}</td>
                <td>${item.orderNo}</td>
                <td class="${amountClass}">${amountText}</td>
                <td>${item.remark}</td>
                <td>${item.expire}</td>
            `;

            tbody.appendChild(tr);
        });
    }

    // ---- 페이지네이션 렌더링 ----
    function renderPagination() {
        const totalFilteredPages = Math.max(1, Math.ceil(filteredData.length / PAGE_SIZE));
        const pageNumbersEl = document.getElementById('pageNumbers');
        pageNumbersEl.innerHTML = '';

        for (let p = 1; p <= totalFilteredPages; p++) {
            const span = document.createElement('a');
            span.href = '#';
            span.textContent = p;
            span.className = 'page-num' + (p === currentPage ? ' active' : '');
            span.addEventListener('click', function (e) {
                e.preventDefault();
                currentPage = p;
                renderPointRows();
                renderPagination();
            });
            pageNumbersEl.appendChild(span);
        }

        document.getElementById('pagePrev').classList.toggle('disabled', currentPage === 1);
        document.getElementById('pageNext').classList.toggle('disabled', currentPage === totalFilteredPages);
    }

    document.getElementById('pagePrev').addEventListener('click', function (e) {
        e.preventDefault();
        if (currentPage > 1) {
            currentPage--;
            renderPointRows();
            renderPagination();
        }
    });

    document.getElementById('pageNext').addEventListener('click', function (e) {
        e.preventDefault();
        const totalFilteredPages = Math.max(1, Math.ceil(filteredData.length / PAGE_SIZE));
        if (currentPage < totalFilteredPages) {
            currentPage++;
            renderPointRows();
            renderPagination();
        }
    });

    // ---- 기간별조회 동작 ----
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
            // 1주일 / 15일 / 1개월 단위 조회
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

        filteredData = pointData.filter(function (item) {
            const itemDate = new Date(item.date);
            return itemDate >= rangeStart && itemDate <= rangeEnd;
        });

        currentPage = 1;
        renderPointRows();
        renderPagination();
    });

    // ---- 초기 렌더링 ----
    renderPointRows();
    renderPagination();