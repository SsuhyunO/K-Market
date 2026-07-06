// 비밀번호 특수문자 포함 + 길이 실시간 체크
document.getElementById('userPw').addEventListener('input', function() {
    const pw = this.value;
    const msg = document.getElementById('pw-msg');
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    const validLength = pw.length >= 8 && pw.length <= 12;

    if (pw.length === 0) {
        msg.textContent = '';
        return;
    }
    if (!validLength) {
        msg.style.color = '#c0392b';
        msg.textContent = '비밀번호는 8~12자로 입력해 주세요.';
    } else if (!hasSpecialChar) {
        msg.style.color = '#c0392b';
        msg.textContent = '특수문자를 포함해 주세요.';
    } else {
        msg.style.color = '#4CAF50';
        msg.textContent = '사용 가능한 비밀번호입니다.';
    }
    checkPwMatch();
});

// 비밀번호 일치여부 실시간 체크
document.getElementById('userPwConfirm').addEventListener('input', checkPwMatch);

function checkPwMatch() {
    const pw = document.getElementById('userPw').value;
    const pwCf = document.getElementById('userPwConfirm').value;
    const msg = document.getElementById('pwConfirm-msg');

    if (pwCf.length === 0) {
        msg.textContent = '';
        return;
    }
    if (pw === pwCf) {
        msg.style.color = '#4CAF50';
        msg.textContent = '비밀번호가 일치합니다.';
    } else {
        msg.style.color = '#c0392b';
        msg.textContent = '비밀번호가 일치하지 않습니다.';
    }
}

let idChecked = false;
let bizChecked = false;

async function checkUserId() {
    const id = document.getElementById('userId').value.trim();
    const msg = document.getElementById('userId-msg');

    if (!id || id.length < 4 || id.length > 12) {
        msg.style.color = '#c0392b';
        msg.textContent = '아이디는 4~12자로 입력해 주세요.';
        idChecked = false;
        return;
    }

    try {
        const res = await fetch(`/K_Market/api/member/check-uid?uid=${encodeURIComponent(id)}`);
        const isDuplicate = await res.json();

        if (isDuplicate) {
            msg.style.color = '#c0392b';
            msg.textContent = '이미 사용 중인 아이디입니다.';
            idChecked = false;
        } else {
            msg.style.color = '#4CAF50';
            msg.textContent = '사용 가능한 아이디입니다.';
            idChecked = true;
        }
    } catch (err) {
        console.error(err);
        msg.style.color = '#c0392b';
        msg.textContent = '중복확인 중 오류가 발생했습니다.';
        idChecked = false;
    }
}

async function checkBizNum() {
    const bizNum = document.getElementById('bizNum').value.trim();
    const msg = document.getElementById('bizNum-msg');

    if (!bizNum) {
        msg.style.color = '#c0392b';
        msg.textContent = '사업자등록번호를 입력해 주세요.';
        bizChecked = false;
        return;
    }

    try {
        const res = await fetch(`/K_Market/api/seller/check-bizregno?bizRegNo=${encodeURIComponent(bizNum)}`);
        const isDuplicate = await res.json();

        if (isDuplicate) {
            msg.style.color = '#c0392b';
            msg.textContent = '이미 등록된 사업자등록번호입니다.';
            bizChecked = false;
        } else {
            msg.style.color = '#4CAF50';
            msg.textContent = '사용 가능한 사업자등록번호입니다.';
            bizChecked = true;
        }
    } catch (err) {
        console.error(err);
        msg.style.color = '#c0392b';
        msg.textContent = '중복확인 중 오류가 발생했습니다.';
        bizChecked = false;
    }
}


function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('addrBase').value = data.roadAddress || data.jibunAddress;
            document.getElementById('addrDetail').focus();
        }
    }).open();
}

async function submitJoin() {
    const id      = document.getElementById('userId').value.trim();
    const pw      = document.getElementById('userPw').value;
    const pwCf    = document.getElementById('userPwConfirm').value;
    const company = document.getElementById('companyName').value.trim();
    const ceo     = document.getElementById('ceoName').value.trim();
    const bizNum  = document.getElementById('bizNum').value.trim();
    const mailNum = document.getElementById('mailOrderNum').value.trim();
    const tel     = document.getElementById('tel').value.trim();
    const fax     = document.getElementById('fax').value.trim();
    const email   = document.getElementById('userEmail').value.trim();

    if (!id || id.length < 4 || id.length > 12) {
        alert('아이디는 4~12자로 입력해 주세요.'); return;
    }
    if (!idChecked) {
        alert('아이디 중복확인을 해주세요.'); return;
    }
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    if (!pw || pw.length < 8 || pw.length > 12) {
        alert('비밀번호는 8~12자로 입력해 주세요.'); return;
    }
    if (!hasSpecialChar) {
        alert('비밀번호에 특수문자를 포함해 주세요.'); return;
    }
    if (pw !== pwCf) {
        alert('비밀번호가 일치하지 않습니다.'); return;
    }
    if (!company) {
        alert('회사명을 입력해 주세요.'); return;
    }
    if (!ceo) {
        alert('대표자명을 입력해 주세요.'); return;
    }
    if (!bizNum) {
        alert('사업자등록번호를 입력해 주세요.'); return;
    }
    if (!bizChecked) {
        alert('사업자등록번호 중복확인을 해주세요.'); return;
    }
    if (!mailNum) {
        alert('통신판매업번호를 입력해 주세요.'); return;
    }
    if (!tel) {
        alert('전화번호를 입력해 주세요.'); return;
    }
    if (!email) {
        alert('이메일을 입력해 주세요.'); return;
    }

    try {
        const signupRes = await fetch('/K_Market/api/seller/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                uid: id,
                password: pw,
                name: ceo,
                phone: tel,
                email: email,
                zipCode: document.getElementById('postcode').value,
                addr1: document.getElementById('addrBase').value,
                addr2: document.getElementById('addrDetail').value,
                companyName: company,
                bizRegNo: bizNum,
                onlineSalesNo: mailNum,
                tel: tel,
                fax: fax
            })
        });

        if (!signupRes.ok) {
            const errMsg = await signupRes.text();
            alert(errMsg || '판매자 회원가입 중 오류가 발생했습니다.');
            return;
        }

        alert('판매자 회원가입이 완료되었습니다!');
        window.location.href = indexUrl;

    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}

function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('addrBase').value = data.roadAddress || data.jibunAddress;
            document.getElementById('addrDetail').focus();
        }
    }).open();
}