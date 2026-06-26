 // ===== 모달 스택 관리 =====
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

    // ===== 비밀번호 수정 =====
    document.getElementById('pwChangeBtn').addEventListener('click', function () {
        alert('비밀번호가 수정되었습니다.');
        location.href = 'home.html';
    });

    // ===== 이메일 수정하기 =====
    const emailEditBtn = document.getElementById('emailEditBtn');
    const emailLocal = document.getElementById('emailLocal');
    const emailDomain = document.getElementById('emailDomain');
    const emailDomainSelect = document.getElementById('emailDomainSelect');
    let emailEditing = false;

    emailDomainSelect.addEventListener('change', function () {
        if (this.value === '') {
            emailDomain.value = '';
            emailDomain.readOnly = false;
            emailDomain.focus();
        } else {
            emailDomain.value = this.value;
        }
    });

    emailEditBtn.addEventListener('click', function () {
        if (!emailEditing) {
            // 수정 모드 진입
            emailLocal.disabled = false;
            emailDomain.disabled = false;
            emailDomainSelect.disabled = false;
            emailLocal.focus();
            emailEditing = true;
            this.textContent = '저장';
            this.classList.add('btn-info-save');
        } else {
            // 저장
            if (!emailLocal.value.trim() || !emailDomain.value.trim()) {
                alert('이메일을 올바르게 입력해주세요.');
                return;
            }
            emailLocal.disabled = true;
            emailDomain.disabled = true;
            emailDomainSelect.disabled = true;
            emailEditing = false;
            this.textContent = '수정하기';
            this.classList.remove('btn-info-save');
        }
    });

    // ===== 휴대폰 수정하기 =====
    const phoneEditBtn = document.getElementById('phoneEditBtn');
    const phone1 = document.getElementById('phone1');
    const phone2 = document.getElementById('phone2');
    const phone3 = document.getElementById('phone3');
    let phoneEditing = false;

    // 숫자만 입력되도록
    [phone1, phone2, phone3].forEach(function (input) {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/[^0-9]/g, '');
        });
    });

    phoneEditBtn.addEventListener('click', function () {
        if (!phoneEditing) {
            phone1.disabled = false;
            phone2.disabled = false;
            phone3.disabled = false;
            phone1.focus();
            phoneEditing = true;
            this.textContent = '저장';
            this.classList.add('btn-info-save');
        } else {
            if (!phone1.value || !phone2.value || !phone3.value) {
                alert('휴대폰 번호를 올바르게 입력해주세요.');
                return;
            }
            phone1.disabled = true;
            phone2.disabled = true;
            phone3.disabled = true;
            phoneEditing = false;
            this.textContent = '수정하기';
            this.classList.remove('btn-info-save');
        }
    });

    // ===== 카카오 주소 API =====
    document.getElementById('addrSearchBtn').addEventListener('click', function () {
        new daum.Postcode({
            oncomplete: function (data) {
                document.getElementById('postcode').value = data.zonecode;
                document.getElementById('addrBasic').value = data.roadAddress || data.jibunAddress;
                document.getElementById('addrDetail').value = '';
                document.getElementById('addrDetail').focus();
            }
        }).open();
    });

    // ===== 탈퇴 하기 =====
    document.getElementById('withdrawBtn').addEventListener('click', function () {
        openModal('withdrawModal');
    });

    document.getElementById('withdrawConfirmBtn').addEventListener('click', function () {
        closeModal('withdrawModal');
        alert('탈퇴가 완료되었습니다.');
        location.href = 'home.html';
    });

    // ===== 수정하기 (최종 제출) =====
    document.getElementById('infoSubmitBtn').addEventListener('click', function () {
        alert('수정이 완료되었습니다.');
        location.href = 'home.html';
    });