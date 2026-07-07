// 비밀번호 영문/숫자/특수문자 포함 + 길이 실시간 체크
document.getElementById('userPw').addEventListener('input', function() {
    const pw = this.value;
    const msg = document.getElementById('pw-msg');
    const hasEnglish = /[A-Za-z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    const validLength = pw.length >= 8 && pw.length <= 12;

    if (pw.length === 0) {
        msg.textContent = '';
        checkPwMatch();
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

const ID_PATTERN = /^[A-Za-z0-9]{4,12}$/;
const PW_LENGTH_OK = pw => pw.length >= 8 && pw.length <= 12;
const PW_HAS_ENGLISH = pw => /[A-Za-z]/.test(pw);
const PW_HAS_NUMBER = pw => /[0-9]/.test(pw);
const PW_HAS_SPECIAL = pw => /[!@#$%^&*(),.?":{}|<>]/.test(pw);
const BIZ_NUM_PATTERN = /^\d{3}-\d{2}-\d{5}$/;
const MAIL_ORDER_PATTERN = /^제\d{4}-[가-힣]+-\d{4,5}호$/;

async function checkUserId() {
    const id = document.getElementById('userId').value.trim();
    const msg = document.getElementById('userId-msg');

    if (!ID_PATTERN.test(id)) {
        msg.style.color = '#c0392b';
        msg.textContent = '아이디는 영문, 숫자 4~12자로 입력해 주세요.';
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

    if (!BIZ_NUM_PATTERN.test(bizNum)) {
        msg.style.color = '#c0392b';
        msg.textContent = '사업자등록번호 형식이 올바르지 않습니다. (예: 123-45-67890)';
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

// 통신판매업번호 형식 실시간 체크 (별도 중복확인 API가 없어 형식만 검사)
document.getElementById('mailOrderNum').addEventListener('blur', function() {
    const val = this.value.trim();
    const msg = document.getElementById('mailOrderNum-msg');

    if (!val) {
        msg.textContent = '';
        return;
    }
    if (!MAIL_ORDER_PATTERN.test(val)) {
        msg.style.color = '#c0392b';
        msg.textContent = '형식이 올바르지 않습니다. (예: 제2024-서울강남-01234호)';
    } else {
        msg.style.color = '#4CAF50';
        msg.textContent = '올바른 형식입니다.';
    }
});

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

    if (!ID_PATTERN.test(id)) {
        alert('아이디는 영문, 숫자 4~12자로 입력해 주세요.'); return;
    }
    if (!idChecked) {
        alert('아이디 중복확인을 해주세요.'); return;
    }
    if (!PW_LENGTH_OK(pw)) {
        alert('비밀번호는 8~12자로 입력해 주세요.'); return;
    }
    if (!PW_HAS_ENGLISH(pw) || !PW_HAS_NUMBER(pw) || !PW_HAS_SPECIAL(pw)) {
        alert('비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다.'); return;
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
    if (!BIZ_NUM_PATTERN.test(bizNum)) {
        alert('사업자등록번호 형식이 올바르지 않습니다. (예: 123-45-67890)'); return;
    }
    if (!bizChecked) {
        alert('사업자등록번호 중복확인을 해주세요.'); return;
    }
    if (!mailNum) {
        alert('통신판매업번호를 입력해 주세요.'); return;
    }
    if (!MAIL_ORDER_PATTERN.test(mailNum)) {
        alert('통신판매업번호 형식이 올바르지 않습니다. (예: 제2024-서울강남-01234호)'); return;
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
