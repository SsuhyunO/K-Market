// ===== 페이지 로드시 로그인한 회원 정보 조회 후 표시 =====
document.addEventListener('DOMContentLoaded', function () {
    fetch('/api/member/me', { credentials: 'include' })
        .then(function (res) { return res.json(); })
        .then(function (member) {
            if (!member) {
                // 세션 없음 -> 로그인 페이지로
                alert('로그인이 필요합니다.');
                location.href = '/member/login';
                return;
            }
            document.getElementById('infoUid').textContent = member.uid || '-';
            document.getElementById('infoName').textContent = member.name || '-';
            document.getElementById('infoBirth').textContent = member.birthDate || '-';
            document.getElementById('infoEmail').textContent = member.email || '-';

            const phoneInput = document.getElementById('phoneInput');
            if (phoneInput) phoneInput.value = member.phone || '';

            if (member.zipCode) document.getElementById('postcode').value = member.zipCode;
            if (member.addr1) document.getElementById('addrBasic').value = member.addr1;
            if (member.addr2) document.getElementById('addrDetail').value = member.addr2;
        })
        .catch(function () {
            console.error('회원 정보를 불러오지 못했습니다.');
        });
});

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

// ===== 비밀번호 수정: 버튼 누르면 숨겨진 영역 표시 -> 실시간 현재비번 확인 -> 통과시 새 비번 입력칸 표시 =====
const pwChangeBtn = document.getElementById('pwChangeBtn');
const pwEditSection = document.getElementById('pwEditSection');
const currentPwInput = document.getElementById('currentPwInput');
const pwCheckMsg = document.getElementById('pwCheckMsg');
const pwNewSection = document.getElementById('pwNewSection');
const newPwInput = document.getElementById('newPwInput');
const newPwConfirmInput = document.getElementById('newPwConfirmInput');
const pwSaveBtn = document.getElementById('pwSaveBtn');

let pwVerified = false;
let pwCheckTimer = null;

function resetPwSection() {
    currentPwInput.value = '';
    pwCheckMsg.textContent = '';
    pwNewSection.style.display = 'none';
    newPwInput.value = '';
    newPwConfirmInput.value = '';
    pwVerified = false;
}

pwChangeBtn.addEventListener('click', function () {
    const isHidden = pwEditSection.style.display === 'none';
    pwEditSection.style.display = isHidden ? 'block' : 'none';
    if (!isHidden) resetPwSection();
});

// 입력할 때마다 (디바운스 400ms) 실시간으로 현재 비밀번호 확인
currentPwInput.addEventListener('input', function () {
    const password = this.value;
    pwVerified = false;
    pwNewSection.style.display = 'none';

    if (pwCheckTimer) clearTimeout(pwCheckTimer);

    if (!password) {
        pwCheckMsg.textContent = '';
        return;
    }

    pwCheckTimer = setTimeout(function () {
        fetch('/api/member/mypage/password/verify', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: 'password=' + encodeURIComponent(password)
        })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    pwCheckMsg.textContent = '비밀번호가 일치합니다.';
                    pwCheckMsg.style.color = 'green';
                    pwVerified = true;
                    pwNewSection.style.display = 'block';
                } else {
                    pwCheckMsg.textContent = '비밀번호가 일치하지 않습니다.';
                    pwCheckMsg.style.color = 'red';
                    pwVerified = false;
                    pwNewSection.style.display = 'none';
                }
            })
            .catch(function () {
                pwCheckMsg.textContent = '오류가 발생했습니다.';
                pwCheckMsg.style.color = 'red';
            });
    }, 400);
});

pwSaveBtn.addEventListener('click', function () {
    if (!pwVerified) {
        alert('현재 비밀번호를 먼저 확인해주세요.');
        return;
    }
    if (!newPwInput.value || !newPwConfirmInput.value) {
        alert('새 비밀번호를 입력해주세요.');
        return;
    }
    if (newPwInput.value !== newPwConfirmInput.value) {
        alert('새 비밀번호가 일치하지 않습니다.');
        return;
    }

    const body = 'newPassword=' + encodeURIComponent(newPwInput.value)
        + '&newPasswordConfirm=' + encodeURIComponent(newPwConfirmInput.value);

    fetch('/api/member/mypage/password/change', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                alert('비밀번호가 변경되었습니다.');
                pwEditSection.style.display = 'none';
                resetPwSection();
            } else {
                alert(data.message || '변경에 실패했습니다.');
            }
        })
        .catch(function () {
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        });
});

// ===== 이메일: 수정 금지, 읽기전용 표시만 (인라인 수정 로직 제거됨) =====

// ===== 휴대폰 수정하기 =====
const phoneEditBtn = document.getElementById('phoneEditBtn');
const phoneInput = document.getElementById('phoneInput');
let phoneEditing = false;

phoneInput.addEventListener('input', function () {
    this.value = this.value.replace(/[^0-9-]/g, '');
});

phoneEditBtn.addEventListener('click', function () {
    if (!phoneEditing) {
        phoneInput.disabled = false;
        phoneInput.focus();
        phoneEditing = true;
        this.textContent = '저장';
        this.classList.add('btn-info-save');
    } else {
        if (!phoneInput.value) {
            alert('휴대폰 번호를 올바르게 입력해주세요.');
            return;
        }
        phoneInput.disabled = true;
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
    fetch('/api/member/withdraw', { method: 'POST', credentials: 'include' })
        .then(function (res) {
            if (!res.ok) throw new Error('withdraw failed');
            return res.text();
        })
        .then(function () {
            closeModal('withdrawModal');
            alert('탈퇴가 완료되었습니다.');
            // 서버에서 이미 session.invalidate() 처리됨 -> 홈으로 이동
            location.href = '/';
        })
        .catch(function () {
            closeModal('withdrawModal');
            alert('탈퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
});

// ===== 수정하기 (최종 제출 - 휴대폰/주소만 저장, 이메일은 절대 전송하지 않음) =====
document.getElementById('infoSubmitBtn').addEventListener('click', function () {
    const payload = {
        phone: phoneInput.value,
        zipCode: document.getElementById('postcode').value,
        addr1: document.getElementById('addrBasic').value,
        addr2: document.getElementById('addrDetail').value
    };

    fetch('/api/member/mypage/update', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(function (res) {
            if (!res.ok) throw new Error('update failed');
            return res.text();
        })
        .then(function () {
            alert('수정이 완료되었습니다.');
            location.href = '/my/home';
        })
        .catch(function () {
            alert('수정 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
});