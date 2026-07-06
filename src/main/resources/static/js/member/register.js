// 회원가입 제출 (유효성 검사 + 서버 전송)
async function submitJoin() {
    const id    = document.getElementById('userId').value.trim();
    const pw    = document.getElementById('userPw').value;
    const pwCf  = document.getElementById('userPwConfirm').value;
    const name  = document.getElementById('userName').value.trim();
    const birth = document.getElementById('userBirth').value;
    const email = document.getElementById('userEmail').value.trim();
    const phone = document.getElementById('userPhone').value.trim();

    if (!id || id.length < 4 || id.length > 12) {
        alert('아이디는 4~12자로 입력해 주세요.'); return;
    }
    if (!pw || pw.length < 8 || pw.length > 12) {
        alert('비밀번호는 8~12자로 입력해 주세요.'); return;
    }
    if (pw !== pwCf) {
        alert('비밀번호가 일치하지 않습니다.'); return;
    }
    if (!name) {
        alert('이름을 입력해 주세요.'); return;
    }
    if (!birth) {
        alert('생년월일을 선택해 주세요.'); return;
    }
    if (!email) {
        alert('이메일을 입력해 주세요.'); return;
    }
    if (!phone) {
        alert('휴대폰 번호를 입력해 주세요.'); return;
    }

    try {
        // 1) 아이디 중복확인
        const checkRes = await fetch(`/K_Market/api/member/check-uid?uid=${encodeURIComponent(id)}`);
        const isDuplicate = await checkRes.json();
        if (isDuplicate) {
            alert('이미 사용 중인 아이디입니다.');
            return;
        }

        // 2) 회원가입 요청
        const signupRes = await fetch('/K_Market/api/member/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                uid: id,
                password: pw,
                name: name,
                birthDate: birth,
                email: email,
                phone: phone
            })
        });

        if (!signupRes.ok) {
            const errMsg = await signupRes.text();
            alert(errMsg || '회원가입 중 오류가 발생했습니다.');
            return;
        }

        alert('회원가입이 완료되었습니다!');
        window.location.href = indexUrl;

    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }


}