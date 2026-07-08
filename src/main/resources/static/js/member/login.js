// 로그인 처리
async function submitLogin(event) {
    event.preventDefault();

    const uid = document.getElementById('userId').value.trim();
    const pw = document.getElementById('userPw').value;

    // ===== 추가된 부분: 자동로그인 체크박스 값 =====
    const autoLoginCheckbox = document.querySelector('input[name="autoLogin"]');
    const autoLogin = autoLoginCheckbox ? autoLoginCheckbox.checked : false;

    if (!uid || !pw) {
        alert('아이디와 비밀번호를 입력해 주세요.');
        return;
    }

    try {
        const res = await fetch('/K_Market/api/member/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ uid: uid, password: pw, autoLogin: autoLogin })
        });

        if (!res.ok) {
            if (res.status === 401 || res.status === 400) {
                alert('아이디 또는 비밀번호가 일치하지 않습니다.');
            } else {
                alert('아이디 또는 비밀번호가 일치하지 않습니다.');
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

// ================================
// 입력 아이콘 애니메이션
// ================================

document.addEventListener('DOMContentLoaded', function () {

    const userInput = document.getElementById('userId');
    const pwInput = document.getElementById('userPw');

    const userIcon = document.getElementById('userIcon');
    const pwIcon = document.getElementById('pwIcon');

    // 아이디 입력
    userInput.addEventListener('input', function () {

        if (this.value.trim() !== '') {

            userIcon.classList.remove('fa-user');
            userIcon.classList.add('fa-circle-check');

        } else {

            userIcon.classList.remove('fa-circle-check');
            userIcon.classList.add('fa-user');

        }

    });

    // 비밀번호 입력
    pwInput.addEventListener('input', function () {

        if (this.value.trim() !== '') {

            pwIcon.classList.remove('fa-lock');
            pwIcon.classList.add('fa-lock-open');

        } else {

            pwIcon.classList.remove('fa-lock-open');
            pwIcon.classList.add('fa-lock');

        }

    });

});