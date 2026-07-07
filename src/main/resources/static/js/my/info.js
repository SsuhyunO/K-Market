// ===== 컨텍스트 패스 =====
const CTX = document.getElementById('ctxPath').content.replace(/\/$/, '');

// ===== 페이지 로드시 로그인한 회원 정보 조회 후 표시 =====
document.addEventListener('DOMContentLoaded', function () {
    fetch(CTX + '/api/member/me', { credentials: 'include' })
        .then(function (res) { return res.json(); })
        .then(function (member) {
            if (!member) {
                alert('로그인이 필요합니다.');
                location.href = CTX + '/member/login';
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

// ===== 비밀번호 수정 =====
const pwChangeBtn = document.getElementById('pwChangeBtn');
const pwEditSection = document.getElementById('pwEditSection');
const currentPwInput = document.getElementById('currentPwInput');
const pwVerifyBtn = document.getElementById('pwVerifyBtn');
const pwCheckMsg = document.getElementById('pwCheckMsg');
const pwNewSection = document.getElementById('pwNewSection');
const newPwInput = document.getElementById('newPwInput');
const newPwConfirmInput = document.getElementById('newPwConfirmInput');
const pwRuleMsg = document.getElementById('pwRuleMsg');
const pwMatchMsg = document.getElementById('pwMatchMsg');
const pwSaveBtn = document.getElementById('pwSaveBtn');

let pwVerified = false;

function setMsg(el, text, type) {
    el.textContent = text;
    el.classList.remove('pw-msg-success', 'pw-msg-error');
    if (type) el.classList.add(type === 'success' ? 'pw-msg-success' : 'pw-msg-error');
}

function resetPwSection() {
    currentPwInput.value = '';
    setMsg(pwCheckMsg, '', null);
    pwNewSection.style.display = 'none';
    newPwInput.value = '';
    newPwConfirmInput.value = '';
    setMsg(pwRuleMsg, '', null);
    setMsg(pwMatchMsg, '', null);
    pwVerified = false;
}

pwChangeBtn.addEventListener('click', function () {
    const isHidden = pwEditSection.style.display === 'none';
    pwEditSection.style.display = isHidden ? 'block' : 'none';
    if (!isHidden) resetPwSection();
});

// 입력값 바뀌면 재확인 필요 -> 초기화 (자동확인 아님, 버튼 눌러야 확인됨)
currentPwInput.addEventListener('input', function () {
    pwVerified = false;
    pwNewSection.style.display = 'none';
    setMsg(pwCheckMsg, '', null);
});

// 확인 버튼 눌렀을 때만 서버 검증
pwVerifyBtn.addEventListener('click', function () {
    const password = currentPwInput.value;
    if (!password) {
        setMsg(pwCheckMsg, '현재 비밀번호를 입력해주세요.', 'error');
        return;
    }

    fetch(CTX + '/api/member/mypage/password/verify', {
        method: 'POST',
        credentials: 'include',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'password=' + encodeURIComponent(password)
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                setMsg(pwCheckMsg, '비밀번호가 일치합니다.', 'success');
                pwVerified = true;
                pwNewSection.style.display = 'flex';
            } else {
                setMsg(pwCheckMsg, '비밀번호가 일치하지 않습니다.', 'error');
                pwVerified = false;
                pwNewSection.style.display = 'none';
            }
        })
        .catch(function () {
            setMsg(pwCheckMsg, '오류가 발생했습니다.', 'error');
        });
});

// 새 비밀번호 규칙 체크 (영문 + 특수문자 포함)
function checkPwRule() {
    const pw = newPwInput.value;
    if (!pw) { setMsg(pwRuleMsg, '', null); return; }
    const hasLetter = /[a-zA-Z]/.test(pw);
    const hasSpecial = /[^a-zA-Z0-9]/.test(pw);
    if (hasLetter && hasSpecial) {
        setMsg(pwRuleMsg, '', null);
    } else {
        setMsg(pwRuleMsg, '영문, 특수문자를 포함해주세요.', 'error');
    }
}

// 새 비밀번호 / 재입력란 실시간 일치여부 표시
function checkPwMatch() {
    const pw = newPwInput.value;
    const confirmPw = newPwConfirmInput.value;

    if (!confirmPw) {
        setMsg(pwMatchMsg, '', null);
        return;
    }
    if (pw === confirmPw) {
        setMsg(pwMatchMsg, '비밀번호가 동일합니다.', 'success');
    } else {
        setMsg(pwMatchMsg, '비밀번호가 일치하지 않습니다.', 'error');
    }
}

newPwInput.addEventListener('input', function () { checkPwRule(); checkPwMatch(); });
newPwConfirmInput.addEventListener('input', checkPwMatch);

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
    if (!/[a-zA-Z]/.test(newPwInput.value) || !/[^a-zA-Z0-9]/.test(newPwInput.value)) {
        alert('새 비밀번호는 영문과 특수문자를 포함해야 합니다.');
        return;
    }

    const body = 'newPassword=' + encodeURIComponent(newPwInput.value)
        + '&newPasswordConfirm=' + encodeURIComponent(newPwConfirmInput.value);

    fetch(CTX + '/api/member/mypage/password/change', {
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
    fetch(CTX + '/api/member/withdraw', { method: 'POST', credentials: 'include' })
        .then(function (res) {
            if (!res.ok) throw new Error('withdraw failed');
            return res.text();
        })
        .then(function () {
            closeModal('withdrawModal');
            alert('탈퇴가 완료되었습니다.');
            location.href = CTX + '/';
        })
        .catch(function () {
            closeModal('withdrawModal');
            alert('탈퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
});

// ===== 수정하기 (최종 제출) =====
document.getElementById('infoSubmitBtn').addEventListener('click', function () {
    const payload = {
        phone: phoneInput.value,
        zipCode: document.getElementById('postcode').value,
        addr1: document.getElementById('addrBasic').value,
        addr2: document.getElementById('addrDetail').value
    };

    fetch(CTX + '/api/member/mypage/update', {
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
            location.href = CTX + '/my/home';
        })
        .catch(function () {
            alert('수정 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
});