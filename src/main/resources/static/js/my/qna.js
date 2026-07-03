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
            if (e.target === overlay) closeModal(overlay.id);
        });
    });

    // "문의 등록" 버튼 클릭 → 문의하기 모달
    document.getElementById('openInquiryBtn').addEventListener('click', function () {
        openModal('inquiryModal');
    });

    // 문의하기 등록
    document.getElementById('inquirySubmitBtn').addEventListener('click', function () {
        const title = document.getElementById('inquiryTitle').value.trim();
        const content = document.getElementById('inquiryContent').value.trim();
        if (!title) { alert('제목을 입력해주세요.'); return; }
        if (!content) { alert('내용을 입력해주세요.'); return; }
        alert('문의가 등록되었습니다.');
        document.getElementById('inquiryTitle').value = '';
        document.getElementById('inquiryContent').value = '';
        document.querySelectorAll('input[name="inquiryType"]').forEach(r => r.checked = false);
        closeModal('inquiryModal');
    });

    // =====================================================
    // ===== 문의하기: 더미 데이터 / 페이지네이션 =====
    // =====================================================

    const PAGE_SIZE = 10;
    const TOTAL_QNA = 100;
    const TOTAL_PAGES = Math.ceil(TOTAL_QNA / PAGE_SIZE);
    const PAGES_PER_GROUP = 5;

    const today = new Date();

    function formatDate(d) {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return y + '-' + m + '-' + day;
    }

    const channels = ['고객센터', '고객센터', '판매자 게시판'];
    const types = ['주문 / 결제', '배송', '반품 / 취소', '교환', '상품 문의'];
    const titleTemplates = [
        '신용카드 결제 중 오류가 난 경우 어떻게 하나요 ?',
        '배송기간이 보통 얼마나 걸리나요 ?',
        '주문한 상품을 아직 못 받았습니다.',
        '반품 신청 방법을 알고 싶습니다.',
        '교환 가능한지 문의드립니다.'
    ];
    const contentTemplates = [
        '결제 중 오류가 발생하여 문의드립니다. 카드사에 확인했지만 정상 처리된 것으로 나오는데 주문 내역에는 없습니다. 확인 부탁드립니다.',
        '주문한 지 5일이 지났는데 아직 배송이 시작되지 않았습니다. 언제쯤 받을 수 있을지 알 수 있을까요?',
        '3일 전 주문한 상품인데 아직 배송 예정일이 나오지 않습니다. 확인 부탁드립니다.',
        '상품을 수령했는데 불량이 있어 반품 신청하고 싶습니다. 절차를 알려주세요.',
        '구매한 상품의 사이즈가 맞지 않아 교환을 원합니다. 교환 가능한지 문의드립니다.'
    ];
    const answerTemplates = [
        '안녕하세요, K-Market 고객센터입니다. 결제 오류 건은 담당팀에서 확인 후 24시간 이내에 처리해 드리겠습니다.',
        '배송은 영업일 기준 3~5일 소요됩니다. 배송 현황은 마이페이지에서 확인하실 수 있습니다.',
        '주문하신 상품의 배송이 지연되고 있습니다. 불편을 드려 죄송합니다. 빠른 시일 내 처리하겠습니다.',
        '반품 신청은 마이페이지 > 전체주문내역 > 반품신청 버튼을 통해 가능합니다.',
        '교환 신청은 마이페이지 > 전체주문내역 > 교환신청 버튼을 통해 가능합니다.'
    ];

    const qnaData = [];

    for (let i = 0; i < TOTAL_QNA; i++) {
        const d = new Date(today);
        d.setDate(d.getDate() - i * 3);

        const answerDate = new Date(d);
        answerDate.setDate(answerDate.getDate() + 1);

        // 1~3번은 검토 중, 나머지는 전부 답변완료
        const isAnswered = i >= 3;

        qnaData.push({
            no: i + 1,
            channel: channels[i % channels.length],
            type: types[i % types.length],
            title: titleTemplates[i % titleTemplates.length],
            content: contentTemplates[i % contentTemplates.length],
            date: formatDate(d),
            status: isAnswered ? '답변완료' : '검토 중',
            answerDate: formatDate(answerDate),
            answer: answerTemplates[i % answerTemplates.length]
        });
    }

    let currentPage = 1;

    function renderQnaRows() {
        const tbody = document.getElementById('qnaListBody');
        tbody.innerHTML = '';

        const startIdx = (currentPage - 1) * PAGE_SIZE;
        const pageItems = qnaData.slice(startIdx, startIdx + PAGE_SIZE);

        if (pageItems.length === 0) {
            const emptyRow = document.createElement('tr');
            emptyRow.innerHTML = '<td colspan="6" class="order-empty">조회된 문의내역이 없습니다.</td>';
            tbody.appendChild(emptyRow);
            return;
        }

        pageItems.forEach(function (item) {
            const tr = document.createElement('tr');
            tr.className = 'js-qna-row';
            tr.setAttribute('data-no', item.no);
            tr.setAttribute('data-channel', item.channel);
            tr.setAttribute('data-type', item.type);
            tr.setAttribute('data-title', item.title);
            tr.setAttribute('data-content', item.content);
            tr.setAttribute('data-date', item.date);
            tr.setAttribute('data-status', item.status);
            tr.setAttribute('data-answer-date', item.answerDate);
            tr.setAttribute('data-answer', item.answer);

            const statusHtml = item.status === '답변완료'
                ? `<a href="#" class="qna-status-done js-open-answer">답변완료</a>`
                : `<span class="qna-status-pending">검토 중</span>`;

            tr.innerHTML = `
                <td class="qna-no">${item.no}</td>
                <td class="qna-channel">${item.channel}</td>
                <td class="qna-type">${item.type}</td>
                <td class="qna-title"><a href="#" class="js-open-detail">${item.title}</a></td>
                <td class="qna-date">${item.date}</td>
                <td class="qna-status">${statusHtml}</td>
            `;

            tbody.appendChild(tr);
        });

        bindQnaEvents();
    }

    function bindQnaEvents() {
        // 제목 클릭 → 문의상세 모달
        document.querySelectorAll('.js-open-detail').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-qna-row');
                document.getElementById('qnaDetailChannel').textContent = row.getAttribute('data-channel');
                document.getElementById('qnaDetailType').textContent = row.getAttribute('data-type');
                document.getElementById('qnaDetailDate').textContent = row.getAttribute('data-date');
                document.getElementById('qnaDetailTitle').textContent = row.getAttribute('data-title');
                document.getElementById('qnaDetailContent').textContent = row.getAttribute('data-content');
                document.getElementById('qnaDetailStatus').textContent = row.getAttribute('data-status');
                openModal('qnaDetailModal');
            });
        });

        // 답변완료 클릭 → 답변내용 모달
        document.querySelectorAll('.js-open-answer').forEach(function (el) {
            el.addEventListener('click', function (e) {
                e.preventDefault();
                const row = el.closest('.js-qna-row');
                document.getElementById('answerChannel').textContent = row.getAttribute('data-channel');
                document.getElementById('answerType').textContent = row.getAttribute('data-type');
                document.getElementById('answerDate').textContent = row.getAttribute('data-date');
                document.getElementById('answerTitle').textContent = row.getAttribute('data-title');
                document.getElementById('answerQuestion').textContent = row.getAttribute('data-content');
                document.getElementById('answerAnswerDate').textContent = row.getAttribute('data-answer-date');
                document.getElementById('answerContent').textContent = row.getAttribute('data-answer');
                openModal('qnaAnswerModal');
            });
        });
    }

    // ---- 페이지네이션 (5개씩 그룹 이동, 10페이지) ----
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
                renderQnaRows();
                renderPagination();
            });
            pageNumbersEl.appendChild(a);
        }

        document.getElementById('pagePrev').classList.toggle('disabled', groupStart === 1);
        document.getElementById('pageNext').classList.toggle('disabled', groupEnd === TOTAL_PAGES);
    }

    document.getElementById('pagePrev').addEventListener('click', function (e) {
        e.preventDefault();
        const currentGroup = Math.ceil(currentPage / PAGES_PER_GROUP);
        const groupStart = (currentGroup - 1) * PAGES_PER_GROUP + 1;
        if (groupStart === 1) return;
        currentPage = groupStart - 1;
        renderQnaRows();
        renderPagination();
    });

    document.getElementById('pageNext').addEventListener('click', function (e) {
        e.preventDefault();
        const currentGroup = Math.ceil(currentPage / PAGES_PER_GROUP);
        const groupStart = (currentGroup - 1) * PAGES_PER_GROUP + 1;
        const groupEnd = Math.min(groupStart + PAGES_PER_GROUP - 1, TOTAL_PAGES);
        if (groupEnd === TOTAL_PAGES) return;
        currentPage = groupEnd + 1;
        renderQnaRows();
        renderPagination();
    });

    // ---- 초기 렌더링 ----
    renderQnaRows();
    renderPagination();