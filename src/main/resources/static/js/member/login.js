// 로그인 처리
async function submitLogin(event) {
    event.preventDefault();

    const uid = document.getElementById('userId').value.trim();
    const pw = document.getElementById('userPw').value;

    if (!uid || !pw) {
        alert('아이디와 비밀번호를 입력해 주세요.');
        return;
    }

    try {
        const res = await fetch('/K_Market/api/member/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ uid: uid, password: pw })
        });

        if (!res.ok) {
            if (res.status === 401 || res.status === 400) {
                alert('아이디 또는 비밀번호가 일치하지 않습니다.');
            } else {
                alert('로그인 중 오류가 발생했습니다.');
            }
            return;
        }

        // 로그인 성공 -> 메인 페이지로 이동
        window.location.href = '/K_Market/';

    } catch (err) {
        console.error(err);
        alert('통신 오류가 발생했습니다.');
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.querySelector('form');
    if (loginForm) {
        loginForm.addEventListener('submit', submitLogin);
    }
});