  // ===== 모달 스택 관리 =====
    // 모달을 열 때 현재 열려있는 모달은 숨기고(스택에 쌓고),
    // 닫을 때는 스택에서 꺼내 이전 모달을 다시 보여준다.
    const modalStack = [];

    function openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (!modal) return;

        // 현재 열려있는 모달이 있으면 숨기고 스택에 저장
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

        // 스택에 이전 모달이 있으면 다시 열기
        if (modalStack.length > 0) {
            const prevId = modalStack.pop();
            const prevModal = document.getElementById(prevId);
            if (prevModal) prevModal.classList.add('active');
        }
    }

    // 닫기 버튼 공통 처리 (data-close 속성에 닫을 모달 id)
    document.querySelectorAll('[data-close]').forEach(function (btn) {
        btn.addEventListener('click', function () {
            closeModal(btn.getAttribute('data-close'));
        });
    });

    // 오버레이(어두운 배경) 클릭 시 닫기
    document.querySelectorAll('.modal-overlay').forEach(function (overlay) {
        overlay.addEventListener('click', function (e) {
            if (e.target === overlay) {
                closeModal(overlay.id);
            }
        });
    });

    // ===== 사이드바 메뉴 =====
    // 포인트내역 클릭 → 포인트내역 모달
    document.querySelectorAll('[data-menu="points"]').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('pointHistoryModal');
        });
    });

    // 문의하기(사이드바) 클릭 → 문의하기 모달
    document.getElementById('sidebarInquiryBtn').addEventListener('click', function (e) {
        e.preventDefault();
        openModal('inquiryModal');
    });

    // 문의내역 더보기 클릭 → 문의하기 모달
    document.getElementById('moreInquiryBtn').addEventListener('click', function (e) {
        e.preventDefault();
        openModal('inquiryModal');
    });

    // ===== 최근주문내역 영역 =====
    // 주문번호 클릭 → 주문상세 모달
    document.querySelectorAll('.js-order-detail').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('orderDetailModal');
        });
    });

    // 구매확정 버튼 → 구매확정 모달
    document.querySelectorAll('.js-confirm-purchase').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('confirmPurchaseModal');
        });
    });

    // 상품평쓰기 버튼 → 상품평쓰기 모달
    document.querySelectorAll('.js-write-review').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('writeReviewModal');
        });
    });

    // 반품신청 버튼 → 반품신청 모달 (클릭한 행의 상품정보를 모달에 채움)
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

    // 교환신청 버튼 → 교환신청 모달 (클릭한 행의 상품정보를 모달에 채움)
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

    // ===== 주문상세 모달 내부 =====
    // 상호명 클릭 → 판매자정보 모달 (주문상세 위에 스택)
    document.querySelectorAll('.js-seller-info').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('sellerInfoModal');
        });
    });

    // 문의하기 버튼(주문상세 안) → 문의하기 모달 (주문상세 위에 스택)
    document.querySelectorAll('.js-inquiry-from-order').forEach(function (el) {
        el.addEventListener('click', function (e) {
            e.preventDefault();
            openModal('inquiryModal');
        });
    });

    // ===== 구매확정 모달 내부 =====
    // 확인 버튼 → success 처리
    document.querySelectorAll('.js-confirm-ok').forEach(function (el) {
        el.addEventListener('click', function () {
            console.log('구매확정 결과: success');
            alert('success\n구매확정이 완료되었습니다.');
            closeModal('confirmPurchaseModal');
        });
    });

    // 취소 버튼 → fail 처리
    document.querySelectorAll('.js-confirm-cancel').forEach(function (el) {
        el.addEventListener('click', function () {
            console.log('구매확정 결과: fail');
            alert('fail\n구매확정이 취소되었습니다.');
        });
    });

    // ===== 상품평쓰기 모달 =====
    // 별점 레이팅 (클릭한 별까지 채워짐)
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

    // 파일선택 시 선택된 파일명 표시 (실제 업로드 처리는 Spring 연동 시 구현 예정)
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

    // 작성완료 버튼 → 내용 필수(10~200자) 검증 후 alert (실제 저장 처리는 Spring 연동 시 구현 예정)
    document.getElementById('reviewSubmitBtn').addEventListener('click', function () {
        const content = document.getElementById('reviewContent').value.trim();

        if (content.length < 10 || content.length > 200) {
            alert('내용을 10자 이상 200자 이내로 작성해주세요.');
            return;
        }

        alert('작성이 완료되었습니다.');

        // 입력값 초기화
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

    // ===== 반품신청 모달 =====
    // 반품신청 버튼 → 사유입력 필수 검증 후 alert (실제 처리는 Spring 연동 시 구현 예정)
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

        // 입력값 초기화
        document.getElementById('returnReason').value = '';
        document.querySelectorAll('input[name="returnType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('returnFileName').textContent = '선택된 파일없음';

        closeModal('returnRequestModal');
    });

    // ===== 교환신청 모달 =====
    // 교환신청 버튼 → 사유입력 필수 검증 후 alert (실제 처리는 Spring 연동 시 구현 예정)
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

        // 입력값 초기화
        document.getElementById('exchangeReason').value = '';
        document.querySelectorAll('input[name="exchangeType"]').forEach(function (radio) {
            radio.checked = false;
        });
        document.getElementById('exchangeFileName').textContent = '선택된 파일없음';

        closeModal('exchangeRequestModal');
    });