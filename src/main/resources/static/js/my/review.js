import { initPagination } from '../global/pagination.js';
import { escapeHtml } from '../global/htmlUtils.js';

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
                    renderReviewDetailImage(row.getAttribute('data-file-id'));

                    const starWrap = document.getElementById('reviewDetailStars');
                    const rating = parseInt(row.getAttribute('data-rating'), 10);
                    starWrap.innerHTML = buildStarHtml(rating);
                }
                openModal('reviewDetailModal');
            });
        });
    }

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

    async function loadReviews(page) {
        const params = new URLSearchParams();
        params.set('page', page);

        const response = await fetch(`${getContextPath()}review/api/list?${params.toString()}`, {
            headers: { Accept: 'application/json' }
        });

        if (!response.ok) {
            throw new Error('리뷰 목록 조회 실패: ' + response.status);
        }

        const currentPageData = await response.json();
        renderReviewRows(currentPageData);
        return currentPageData;
    }

    // ---- 리뷰 행 렌더링 ----
    function renderReviewRows(pageData) {
        const tbody = document.getElementById('reviewListBody');
        tbody.innerHTML = '';

        const pageItems = pageData?.list || [];
        if (pageItems.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = '<td colspan="5">작성한 상품평이 없습니다.</td>';
            tbody.appendChild(tr);
            return;
        }

        pageItems.forEach(function (item) {
            const tr = document.createElement('tr');
            tr.className = 'js-review-row';
            tr.setAttribute('data-no', item.reviewNo);
            tr.setAttribute('data-product-code', item.productNo);
            tr.setAttribute('data-product-name', item.productName);
            tr.setAttribute('data-content', item.content);
            tr.setAttribute('data-rating', item.rating);
            tr.setAttribute('data-file-id', item.fileId || '');
            tr.setAttribute('data-date', item.createdAt);

            tr.innerHTML = `
                <td class="review-no">${escapeHtml(item.reviewNo)}</td>
                <td class="review-product"><a href="#" class="js-review-detail">${escapeHtml(item.productNo)} / ${escapeHtml(item.productName)}</a></td>
                <td class="review-content js-review-detail">${escapeHtml(item.content)}</td>
                <td class="review-rating">${buildStarHtml(item.rating)}</td>
                <td class="review-date">${escapeHtml(item.createdAt)}</td>
            `;

            tbody.appendChild(tr);
        });

        bindReviewDetailLinks();
    }

    function getContextPath() {
        return document.querySelector('meta[name="context-path"]')?.content || '/';
    }

    function renderReviewDetailImage(fileId) {
        const imageWrap = document.getElementById('reviewDetailImage');
        if (!imageWrap) return;

        if (!fileId) {
            imageWrap.textContent = '상품이미지';
            imageWrap.classList.remove('has-image');
            return;
        }

        imageWrap.classList.add('has-image');
        imageWrap.innerHTML = `<img src="${getContextPath()}files/${encodeURIComponent(fileId)}" alt="상품평 첨부 이미지">`;
    }

    function handleReviewLoadError(error) {
        console.error(error);
        const tbody = document.getElementById('reviewListBody');
        tbody.innerHTML = '<tr><td colspan="5">작성한 상품평이 없습니다.</td></tr>';
    }

    initPagination({
        selector: '#pagination',
        fetchPage: loadReviews,
        onError: handleReviewLoadError
    });
