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

    // 상호명 클릭 → 판매자정보 모달
    document.querySelectorAll('.js-seller-info').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('sellerInfoModal');
        });
    });

    // 문의하기 버튼(주문상세 안) → 문의하기 모달
    document.querySelectorAll('.js-inquiry-from-order').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('inquiryModal');
        });
    });

    // 구매확정 모달 확인/취소
    document.querySelectorAll('.js-confirm-ok').forEach(function (el) {
        el.addEventListener('click', function () {
            alert('success\n구매확정이 완료되었습니다.');
            closeModal('confirmPurchaseModal');
        });
    });
    document.querySelectorAll('.js-confirm-cancel').forEach(function (el) {
        el.addEventListener('click', function () {
            alert('fail\n구매확정이 취소되었습니다.');
        });
    });

    // 상품평쓰기 모달 - 별점
    let selectedRating = 0;
    const starEls = document.querySelectorAll('#reviewStarRating .star');

    function paintStars(value) {
        starEls.forEach(function (star) {
            const starValue = parseInt(star.getAttribute('data-value'), 10);
            star.classList.toggle('star-filled', starValue <= value);
        });
    }

    starEls.forEach(function (star) {
        star.addEventListener('click', function () {
            selectedRating = parseInt(star.getAttribute('data-value'), 10);
            paintStars(selectedRating);
        });
    });

    document.querySelectorAll('.review-file-input').forEach(function (input) {
        input.addEventListener('change', function () {
            const targetId = input.getAttribute('data-target');
            const nameEl = document.getElementById(targetId);
            if (input.files && input.files.length > 0) {
                nameEl.textContent = input.files[0].name;
            } else {
                nameEl.textContent = '선택된파일없음';
            }
        });
    });

    document.getElementById('reviewSubmitBtn').addEventListener('click', function () {
        const content = document.getElementById('reviewContent').value.trim();

        if (content.length < 10 || content.length > 200) {
            alert('내용을 10자 이상 200자 이내로 작성해주세요.');
            return;
        }

        alert('작성이 완료되었습니다.');

        selectedRating = 0;
        paintStars(0);
        document.getElementById('reviewContent').value = '';
        document.querySelectorAll('.review-file-input').forEach(function (input) {
            input.value = '';
        });
        document.querySelectorAll('.review-file-name').forEach(function (el) {
            el.textContent = '선택된파일없음';
        });

        closeModal('writeReviewModal');
    });

    // 반품신청 모달
    document.getElementById('returnSubmitBtn').addEventListener('click', function () {
        const reason = document.getElementById('returnReason').value.trim();
        const returnType = document.querySelector('input[name="returnType"]:checked');

        if (!returnType) {
            alert('반품유형을 선택해주세요.');
            return;
        }
        if (reason.length === 0) {
            alert('반품사유를 입력해주세요.');
            return;
        }

        alert('반품신청이 완료되었습니다.');

        document.getElementById('returnReason').value = '';
        document.querySelectorAll('input[name="returnType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('returnFileName').textContent = '선택된 파일없음';

        closeModal('returnRequestModal');
    });

    // 교환신청 모달
    document.getElementById('exchangeSubmitBtn').addEventListener('click', function () {
        const reason = document.getElementById('exchangeReason').value.trim();
        const exchangeType = document.querySelector('input[name="exchangeType"]:checked');

        if (!exchangeType) {
            alert('교환유형을 선택해주세요.');
            return;
        }
        if (reason.length === 0) {
            alert('교환사유를 입력해주세요.');
            return;
        }

        alert('교환신청이 완료되었습니다.');

        document.getElementById('exchangeReason').value = '';
        document.querySelectorAll('input[name="exchangeType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('exchangeFileName').textContent = '선택된 파일없음';

        closeModal('exchangeRequestModal');
    });


    // =====================================================
    // ===== 전체주문내역: 더미 데이터 / 기간조회 / 페이지네이션 =====
    // =====================================================

    // ---- 더미 주문 데이터 50건 생성 (페이지당 10건 x 5페이지) ----
    const PAGE_SIZE = 10;
    const TOTAL_PAGES = 5;
    const today = new Date();

    function formatDate(d) {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + day;
    }

    const statusList = ['배송완료', '배송중', '결제완료'];
    const orderData = [];

    for (let i = 0; i < PAGE_SIZE * TOTAL_PAGES; i++) {
        // 최근 5개월 범위 안에서 날짜를 역순으로 분산 배치
        const d = new Date(today);
        d.setDate(d.getDate() - i * 3); // 3일 간격으로 과거로 이동 (최근 5개월 커버)

        const orderNo = '10122' + String(11341 - i).padStart(5, '0');
        const status = statusList[i % statusList.length];

        orderData.push({
            date: formatDate(d),
            orderNo: orderNo,
            company: '(주) 상호명',
            product: '상품명',
            qty: 1,
            price: '34,000원',
            status: status
        });
    }

    // ---- 상태 행 렌더링 ----
    let currentPage = 1;
    let filteredData = orderData.slice(); // 기간조회 필터 적용 결과 (기본: 전체)

    function getActionButtonsHtml(item) {
        // 상태에 따라 버튼 노출 여부를 다르게 하지 않고
        // 피그마 기준표에 명시된 4개 버튼을 동일하게 출력 (기준표에 상태별 분기 명시 없음)
        return `
            <a href="#" class="btn-blue js-confirm-purchase">구매확정</a>
            <a href="#" class="btn-blue js-write-review">상품평쓰기</a>
            <a href="#" class="btn-white js-return-request">반품신청</a>
            <a href="#" class="btn-white js-exchange-request">교환신청</a>
        `;
    }

    function renderOrderRows() {
        const tbody = document.getElementById('orderListBody');
        tbody.innerHTML = '';

        const startIdx = (currentPage - 1) * PAGE_SIZE;
        const pageItems = filteredData.slice(startIdx, startIdx + PAGE_SIZE);

        if (pageItems.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="4" class="order-empty">조회된 주문내역이 없습니다.</td>';
            tbody.appendChild(emptyRow);
            return;
        }

        pageItems.forEach(function (item) {
            const tr = document.createElement('tr');
            tr.className = 'js-order-row';
            tr.setAttribute('data-date', item.date);
            tr.setAttribute('data-order-no', item.orderNo);
            tr.setAttribute('data-company', item.company);
            tr.setAttribute('data-product', item.product);
            tr.setAttribute('data-qty', item.qty);
            tr.setAttribute('data-price', item.price);

            tr.innerHTML = `
                <td class="order-date">${item.date}</td>
                <td class="order-product">
                    <div class="product-inner">
                        <div class="product-img">상품이미지</div>
                        <div class="product-info">
                            <p class="order-no"><a href="#" class="js-order-detail" data-order="${item.orderNo}">주문번호 : ${item.orderNo}</a></p>
                            <p class="company-name">${item.company}</p>
                            <p class="product-name">${item.product}</p>
                            <p class="product-qty">수량 : ${item.qty} 개</p>
                            <p class="product-price">${item.price}</p>
                        </div>
                    </div>
                </td>
                <td class="order-status">${item.status}</td>
                <td class="order-action">
                    <div class="action-btn-group">
                        ${getActionButtonsHtml(item)}
                    </div>
                </td>
            `;

            tbody.appendChild(tr);
        });

        attachRowEventListeners();
    }

    // ---- 행 단위 이벤트 바인딩 (렌더링마다 재바인딩 필요) ----
    function attachRowEventListeners() {
        document.querySelectorAll('.js-order-detail').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-order-row');
                if (row) {
                    const orderNo = row.getAttribute('data-order-no');
                    const date = row.getAttribute('data-date');
                    document.getElementById('detailDate').textContent = date;
                    document.getElementById('detailDate2').textContent = date;
                    document.getElementById('detailOrderNo').textContent = '주문번호 : ' + orderNo;
                    document.getElementById('detailCompany').textContent = row.getAttribute('data-company');
                    document.getElementById('detailProduct').textContent = row.getAttribute('data-product');
                    document.getElementById('detailQty').textContent = '수량 : ' + row.getAttribute('data-qty') + ' 개';
                    document.getElementById('detailPrice').textContent = row.getAttribute('data-price');
                    document.getElementById('detailStatus').textContent = row.querySelector('.order-status') ? row.querySelector('.order-status').textContent : '';
                }
                openModal('orderDetailModal');
            });
        });

        document.querySelectorAll('.js-confirm-purchase').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                openModal('confirmPurchaseModal');
            });
        });

        document.querySelectorAll('.js-write-review').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-order-row');
                if (row) {
                    document.getElementById('reviewProductName').textContent = row.getAttribute('data-product');
                }
                openModal('writeReviewModal');
            });
        });

        document.querySelectorAll('.js-return-request').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-order-row');
                if (row) {
                    document.getElementById('returnDate').textContent = row.getAttribute('data-date');
                    document.getElementById('returnOrderNo').textContent = '주문번호 : ' + row.getAttribute('data-order-no');
                    document.getElementById('returnCompany').textContent = row.getAttribute('data-company');
                    document.getElementById('returnProduct').textContent = row.getAttribute('data-product');
                    document.getElementById('returnQty').textContent = '수량 : ' + row.getAttribute('data-qty') + ' 개';
                    document.getElementById('returnPrice').textContent = row.getAttribute('data-price');
                }
                openModal('returnRequestModal');
            });
        });

        document.querySelectorAll('.js-exchange-request').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-order-row');
                if (row) {
                    document.getElementById('exchangeDate').textContent = row.getAttribute('data-date');
                    document.getElementById('exchangeOrderNo').textContent = '주문번호 : ' + row.getAttribute('data-order-no');
                    document.getElementById('exchangeCompany').textContent = row.getAttribute('data-company');
                    document.getElementById('exchangeProduct').textContent = row.getAttribute('data-product');
                    document.getElementById('exchangeQty').textContent = '수량 : ' + row.getAttribute('data-qty') + ' 개';
                    document.getElementById('exchangePrice').textContent = row.getAttribute('data-price');
                }
                openModal('exchangeRequestModal');
            });
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
                renderOrderRows();
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
            renderOrderRows();
            renderPagination();
        }
    });

    document.getElementById('pageNext').addEventListener('click', function (e) {
        e.preventDefault();
        const totalFilteredPages = Math.max(1, Math.ceil(filteredData.length / PAGE_SIZE));
        if (currentPage < totalFilteredPages) {
            currentPage++;
            renderOrderRows();
            renderPagination();
        }
    });

    // ---- 기간별조회 동작 ----
    // 1주일 / 15일 / 1개월 라디오 선택 시 해당 일수만큼 최근 데이터 필터링
    document.querySelectorAll('input[name="periodType"]').forEach(function (radio) {
        radio.addEventListener('change', function () {
            // 다른 조회방식(개월선택, 기간선택) 선택값 초기화
            document.getElementById('periodMonthSelect').value = '0';
            document.getElementById('periodStartDate').value = '';
            document.getElementById('periodEndDate').value = '';
        });
    });

    // 5개월 단위 select 변경 시 라디오/날짜 초기화
    document.getElementById('periodMonthSelect').addEventListener('change', function () {
        document.querySelectorAll('input[name="periodType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('periodStartDate').value = '';
        document.getElementById('periodEndDate').value = '';
    });

    // 시작/종료 날짜 입력 시 라디오/개월선택 초기화
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
            // 3) 시작~종료 날짜 조회 (최대 1년치, 둘 다 입력된 경우만 동작)
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
            // 1) 1주일 / 15일 / 1개월 단위 조회
            const days = parseInt(checkedPeriod.getAttribute('data-days'), 10);
            rangeEnd = new Date(today);
            rangeStart = new Date(today);
            rangeStart.setDate(rangeStart.getDate() - days);
        } else {
            // 2) 현재 월에서 최신 5개월까지 조회 (개월 select)
            const monthsAgo = parseInt(monthSelectValue, 10);
            rangeEnd = new Date(today);
            rangeStart = new Date(today);
            rangeStart.setMonth(rangeStart.getMonth() - (monthsAgo + 1));
        }

        filteredData = orderData.filter(function (item) {
            const itemDate = new Date(item.date);
            return itemDate >= rangeStart && itemDate <= rangeEnd;
        });

        currentPage = 1;
        renderOrderRows();
        renderPagination();
    });

    // ---- 초기 렌더링 ----
    renderOrderRows();
    renderPagination();