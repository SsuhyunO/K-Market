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

    // ---- 상품명 클릭 → 리뷰상세 모달 ----
    function bindReviewDetailLinks() {
        document.querySelectorAll('.js-review-detail').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-review-row');
                if (row) {
                    document.getElementById('reviewDetailNo').textContent = row.getAttribute('data-no');
                    document.getElementById('reviewDetailProductCode').textContent = '상품번호 : ' + row.getAttribute('data-product-code');
                    document.getElementById('reviewDetailProductName').textContent = row.getAttribute('data-product-name');
                    document.getElementById('reviewDetailDate').textContent = row.getAttribute('data-date');
                    document.getElementById('reviewDetailWriteDate').textContent = row.getAttribute('data-date');
                    document.getElementById('reviewDetailContent').textContent = row.getAttribute('data-content');

                    const starWrap = document.getElementById('reviewDetailStars');
                    const rating = parseInt(row.getAttribute('data-rating'), 10);
                    starWrap.innerHTML = buildStarHtml(rating);
                }
                openModal('reviewDetailModal');
            });
        });
    }

    // =====================================================
    // ===== 나의리뷰: 더미 데이터 / 페이지네이션(5개 그룹 이동) =====
    // =====================================================

    const PAGE_SIZE = 10;
    const TOTAL_REVIEWS = 150; // 총 15페이지 (10건씩)
    const TOTAL_PAGES = Math.ceil(TOTAL_REVIEWS / PAGE_SIZE);
    const PAGES_PER_GROUP = 5; // 페이지번호 5개씩 그룹 이동

    const today = new Date();

    function formatDate(d) {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + day;
    }

    const reviewContents = [
        '배송이 빠릅니다. 잘 사용하겠습니다.',
        '배송이 빠릅니다. 잘 사용하 겠습니다 .',
        '생각보다 품질이 좋아서 만족합니다.',
        '가격대비 괜찮은 것 같아요.',
        '재구매 의사 있습니다.'
    ];

    // ---- 더미 리뷰 데이터 150건 생성 ----
    const reviewData = [];

    for (let i = 0; i < TOTAL_REVIEWS; i++) {
        const d = new Date(today);
        d.setDate(d.getDate() - i * 2); // 2일 간격으로 과거 분산

        const rating = 3 + (i % 3); // 3~5점 사이 분산
        const productCode = String(100000 + i);

        reviewData.push({
            no: i + 1,
            productCode: productCode,
            productName: '상품명',
            content: reviewContents[i % reviewContents.length],
            rating: rating,
            date: formatDate(d)
        });
    }

    let currentPage = 1;

    // ---- 별점 HTML 생성 (채워진 별 노란색 / 빈 별 회색) ----
    function buildStarHtml(rating) {
        let html = '';
        for (let s = 1; s <= 5; s++) {
            html += s <= rating
                ? '<span class="star-filled">★</span>'
                : '<span class="star-empty">★</span>';
        }
        return html;
    }

    // ---- 리뷰 행 렌더링 ----
    function renderReviewRows() {
        const tbody = document.getElementById('reviewListBody');
        tbody.innerHTML = '';

        const startIdx = (currentPage - 1) * PAGE_SIZE;
        const pageItems = reviewData.slice(startIdx, startIdx + PAGE_SIZE);

        pageItems.forEach(function (item) {
            const tr = document.createElement('tr');
            tr.className = 'js-review-row';
            tr.setAttribute('data-no', item.no);
            tr.setAttribute('data-product-code', item.productCode);
            tr.setAttribute('data-product-name', item.productName);
            tr.setAttribute('data-content', item.content);
            tr.setAttribute('data-rating', item.rating);
            tr.setAttribute('data-date', item.date);

            tr.innerHTML = `
                <td class="review-no">${item.no}</td>
                <td class="review-product"><a href="#" class="js-review-detail">상품번호 / 상품명</a></td>
                <td class="review-content js-review-detail">${item.content}</td>
                <td class="review-rating">${buildStarHtml(item.rating)}</td>
                <td class="review-date">${item.date}</td>
            `;

            tbody.appendChild(tr);
        });

        bindReviewDetailLinks();
    }

    // ---- 페이지네이션 렌더링 (5개씩 그룹 이동) ----
    function renderPagination() {
        const pageNumbersEl = document.getElementById('pageNumbers');
        pageNumbersEl.innerHTML = '';

        const currentGroup = Math.ceil(currentPage / PAGES_PER_GROUP);
        const groupStart = (currentGroup - 1) * PAGES_PER_GROUP + 1;
        const groupEnd = Math.min(groupStart + PAGES_PER_GROUP - 1, TOTAL_PAGES);

        for (let p = groupStart; p <= groupEnd; p++) {
            const a = document.createElement('a');
            a.href = '#';
            a.textContent = p;
            a.className = 'page-num' + (p === currentPage ? ' active' : '');
            a.addEventListener('click', function (e) {
                e.preventDefault();
                currentPage = p;
                renderReviewRows();
                renderPagination();
            });
            pageNumbersEl.appendChild(a);
        }

        const isFirstGroup = groupStart === 1;
        const isLastGroup = groupEnd === TOTAL_PAGES;

        document.getElementById('pagePrev').classList.toggle('disabled', isFirstGroup);
        document.getElementById('pageNext').classList.toggle('disabled', isLastGroup);
    }

    // "이전" → 현재 그룹의 첫 페이지 이전 그룹 마지막 페이지로 이동 (그룹 단위 이동)
    document.getElementById('pagePrev').addEventListener('click', function (e) {
        e.preventDefault();
        const currentGroup = Math.ceil(currentPage / PAGES_PER_GROUP);
        const groupStart = (currentGroup - 1) * PAGES_PER_GROUP + 1;

        if (groupStart === 1) return; // 이미 첫 그룹

        currentPage = groupStart - 1; // 이전 그룹의 마지막 페이지
        renderReviewRows();
        renderPagination();
    });

    // "다음" → 다음 그룹의 첫 페이지로 이동
    document.getElementById('pageNext').addEventListener('click', function (e) {
        e.preventDefault();
        const currentGroup = Math.ceil(currentPage / PAGES_PER_GROUP);
        const groupStart = (currentGroup - 1) * PAGES_PER_GROUP + 1;
        const groupEnd = Math.min(groupStart + PAGES_PER_GROUP - 1, TOTAL_PAGES);

        if (groupEnd === TOTAL_PAGES) return; // 이미 마지막 그룹

        currentPage = groupEnd + 1; // 다음 그룹의 첫 페이지
        renderReviewRows();
        renderPagination();
    });

    // ---- 초기 렌더링 ----
    renderReviewRows();
    renderPagination();