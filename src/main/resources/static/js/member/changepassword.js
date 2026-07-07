const resetUid = sessionStorage.getItem('resetUid');
if (!resetUid) {
    alert('잘못된 접근입니다. 비밀번호 찾기를 다시 진행해 주세요.');
    window.location.href = passwordUrl;
}

document.addEventListener('DOMContentLoaded', function () {
    const uidCell = document.getElementById('resetUidDisplay');
    if (uidCell && resetUid) uidCell.textContent = resetUid;
});

document.getElementById('newPw').addEventListener('input', function() {
    const pw = this.value;
    const msg = document.getElementById('pw-msg');
    const hasEnglish = /[A-Za-z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    const validLength = pw.length >= 8 && pw.length <= 12;

    if (pw.length === 0) {
        msg.textContent = '';
        return;
    }
    if (!validLength) {
        msg.style.color = '#c0392b';
        msg.textContent = '비밀번호는 8~12자로 입력해 주세요.';
    } else if (!hasEnglish || !hasNumber || !hasSpecialChar) {
        msg.style.color = '#c0392b';
        msg.textContent = '영문, 숫자, 특수문자를 모두 포함해 주세요.';
    } else {
        msg.style.color = '#4CAF50';
        msg.textContent = '사용 가능한 비밀번호 입니다.';
    }
});

async function submitPw() {
    const pw   = document.getElementById('newPw').value;
    const pwCf = document.getElementById('newPwConfirm').value;
    const hasEnglish = /[A-Za-z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);

    if (!pw || pw.length < 8 || pw.length > 12) {
        alert('비밀번호는 8~12자로 입력해 주세요.'); return;
    }
    if (!hasEnglish || !hasNumber || !hasSpecialChar) {
        alert('비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.'); return;
    }
    if (pw !== pwCf) {
        alert('비밀번호가 일치하지 않습니다.'); return;
    }

    try {
        const res = await fetch('/K_Market/api/member/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ uid: resetUid, newPassword: pw })
        });
        if (!res.ok) {
            const errMsg = await res.text();
            alert(errMsg || '비밀번호 변경 중 오류가 발생했습니다.');
            return;
        }
        sessionStorage.removeItem('resetUid');
        alert('비밀번호가 변경되었습니다.');
        window.location.href = loginUrl;
    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}
