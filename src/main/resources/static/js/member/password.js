let currentTab = 'email';
let emailVerified = false;

function switchTab(event, tab) {
    currentTab = tab;
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-panel').forEach(panel => panel.classList.remove('active'));
    event.target.classList.add('active');
    document.getElementById('panel-' + tab).classList.add('active');
    document.getElementById('guide-email').style.display = tab === 'email' ? 'block' : 'none';
    document.getElementById('guide-phone').style.display = tab === 'phone' ? 'block' : 'none';
}

async function sendEmailCode() {
    const email = document.getElementById('email-addr').value.trim();
    if (!email) { alert('이메일을 입력해 주세요.'); return; }
    try {
        const res = await fetch('/K_Market/api/member/email/send-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });
        if (!res.ok) { alert('인증번호 발송에 실패했습니다.'); return; }
        alert('인증번호를 발송했습니다.');
    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}

async function confirmEmailCode() {
    const email = document.getElementById('email-addr').value.trim();
    const code = document.getElementById('email-code').value.trim();
    if (!code) { alert('인증번호를 입력해 주세요.'); return; }
    try {
        const res = await fetch('/K_Market/api/member/email/verify-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, authCode: code })
        });
        const ok = await res.json();
        if (ok) {
            emailVerified = true;
            alert('이메일 인증이 완료되었습니다.');
        } else {
            emailVerified = false;
            alert('인증번호가 일치하지 않습니다.');
        }
    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}

function sendPhoneCode() {
    alert('휴대폰 인증은 준비 중입니다.');
}
function confirmPhoneCode() {
    alert('휴대폰 인증은 준비 중입니다.');
}

async function goNext() {
    if (currentTab !== 'email') {
        alert('휴대폰 인증은 준비 중입니다. 이메일 인증을 이용해 주세요.');
        return;
    }

    const uid   = document.getElementById('email-id').value.trim();
    const email = document.getElementById('email-addr').value.trim();
    if (!uid)   { alert('아이디를 입력해 주세요.'); return; }
    if (!email) { alert('이메일을 입력해 주세요.'); return; }
    if (!emailVerified) { alert('이메일 인증을 완료해 주세요.'); return; }

    try {
        const res = await fetch('/K_Market/api/member/find-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ uid, email })
        });
        const matched = await res.json();
        if (!matched) {
            alert('일치하는 회원 정보가 없습니다.');
            return;
        }
        sessionStorage.setItem('resetUid', uid);
        window.location.href = changePasswordUrl;
    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}
