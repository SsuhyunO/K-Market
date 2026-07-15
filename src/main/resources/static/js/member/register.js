// 카카오 주소 검색
function openPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('addrBase').value = data.roadAddress || data.jibunAddress;
            document.getElementById('addrDetail').focus();
        }
    }).open();
}

// 이메일 인증 여부
let emailVerified = false;
let emailChecked = false;

// 아이디 중복확인 여부
let idChecked = false;

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PHONE_PATTERN = /^01[016789]-?\d{3,4}-?\d{4}$/;
// 영문 + 숫자를 각각 최소 1개 이상 포함, 영문/숫자 외 문자 불가, 4~12자
const USERID_PATTERN = /^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{4,12}$/;
// 영문 + 숫자 + 특수문자 각각 최소 1개 이상 포함, 8~12자
const USERPW_PATTERN = /^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z0-9!@#$%^&*(),.?":{}|<>]{8,12}$/;

// 이메일 형식 검증 + 중복확인
async function checkEmailDuplicate() {
    const email = document.getElementById('userEmail').value.trim();
    const msg = document.getElementById('email-msg');

    emailChecked = false;

    if (!EMAIL_PATTERN.test(email)) {
        msg.style.color = '#c0392b';
        msg.textContent = '이메일 형식이 올바르지 않습니다.';
        return false;
    }

    try {
        const res = await fetch(
            '/K_Market/api/member/check-email?email=' + encodeURIComponent(email),
            { credentials: 'include' }
        );

        const isDuplicate = await res.json();

        if (isDuplicate) {
            msg.style.color = '#c0392b';
            msg.textContent = '이미 사용 중인 이메일입니다.';
            return false;
        }

        msg.style.color = '#4CAF50';
        msg.textContent = '사용 가능한 이메일입니다.';
        emailChecked = true;
        return true;
    } catch (err) {
        console.error(err);
        msg.style.color = '#c0392b';
        msg.textContent = '중복확인 중 오류가 발생했습니다.';
        return false;
    }
}

// 이메일 인증번호 발송
async function sendEmailCode() {
    const email = document.getElementById('userEmail').value.trim();

    if (!email) {
        alert('이메일을 입력해 주세요.');
        return;
    }

    const ok = await checkEmailDuplicate();
    if (!ok) {
        return;
    }

    try {
        const res = await fetch(
            '/K_Market/api/member/send-email-code?email=' + encodeURIComponent(email),
            {
                method: 'POST',
                credentials: 'include'
            }
        );

        if (!res.ok) {
            const errorText = await res.text();
            throw new Error('이메일 발송 실패: ' + errorText);
        }

        document.getElementById('emailCodeRow').style.display = 'table-row';
        alert('인증번호가 발송되었습니다.');

    } catch (err) {
        console.error(err);
        alert('이메일 발송 중 오류가 발생했습니다.');
    }
}

// 이메일 인증번호 확인
async function verifyEmailCode() {
    const code = document.getElementById('emailCode').value.trim();
    const msg = document.getElementById('emailCode-msg');

    if (!code) {
        alert('인증번호를 입력해 주세요.');
        return;
    }

    try {
        const res = await fetch(
            '/K_Market/api/member/verify-email-code?code=' + encodeURIComponent(code),
            {
                method: 'POST',
                credentials: 'include'
            }
        );

        if (!res.ok) {
            throw new Error('인증 실패');
        }

        const isValid = await res.json();

        if (isValid) {
            msg.style.color = '#4CAF50';
            msg.textContent = '인증이 완료되었습니다.';
            emailVerified = true;
        } else {
            msg.style.color = '#c0392b';
            msg.textContent = '인증번호가 일치하지 않습니다.';
            emailVerified = false;
        }

    } catch (err) {
        console.error(err);
        msg.style.color = '#c0392b';
        msg.textContent = '오류가 발생했습니다.';
    }
}

// 아이디 값이 바뀌면 기존 중복확인 결과 무효화
document.getElementById('userId').addEventListener('input', function () {
    idChecked = false;
    const msg = document.getElementById('userId-msg');
    msg.style.color = '#c0392b';
    msg.textContent = '아이디 중복확인을 다시 해주세요.';
});

// 이메일 값이 바뀌면 기존 중복확인/인증 결과 무효화
document.getElementById('userEmail').addEventListener('input', function () {
    emailChecked = false;
    emailVerified = false;
    const msg = document.getElementById('email-msg');
    msg.style.color = '#c0392b';
    msg.textContent = '이메일 중복확인을 다시 해주세요.';
    document.getElementById('emailCodeRow').style.display = 'none';
});

// 아이디 중복확인
async function checkUserId() {
    const id = document.getElementById('userId').value.trim();
    const msg = document.getElementById('userId-msg');

    if (!id || id.length < 4 || id.length > 12) {
        msg.style.color = '#c0392b';
        msg.textContent = '아이디는 4~12자로 입력해 주세요.';
        idChecked = false;
        return;
    }

    if (!USERID_PATTERN.test(id)) {
        msg.style.color = '#c0392b';
        msg.textContent = '아이디는 영문, 숫자를 모두 포함해야 합니다.';
        idChecked = false;
        return;
    }

    try {
        const res = await fetch(
            '/K_Market/api/member/check-uid?uid=' + encodeURIComponent(id),
            { credentials: 'include' }
        );

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
    }
}

// 비밀번호 유효성 검사
document.getElementById('userPw').addEventListener('input', function () {
    const pw = this.value;
    const msg = document.getElementById('pw-msg');
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pw);
    const hasLetter = /[a-zA-Z]/.test(pw);
    const hasNumber = /[0-9]/.test(pw);
    const validLength = pw.length >= 8 && pw.length <= 12;

    if (pw.length === 0) {
        msg.textContent = '';
        return;
    }

    if (!validLength) {
        msg.style.color = '#c0392b';
        msg.textContent = '비밀번호는 8~12자로 입력해 주세요.';
    } else if (!hasLetter) {
        msg.style.color = '#c0392b';
        msg.textContent = '영문을 포함해 주세요.';
    } else if (!hasNumber) {
        msg.style.color = '#c0392b';
        msg.textContent = '숫자를 포함해 주세요.';
    } else if (!hasSpecialChar) {
        msg.style.color = '#c0392b';
        msg.textContent = '특수문자를 포함해 주세요.';
    } else {
        msg.style.color = '#4CAF50';
        msg.textContent = '사용 가능한 비밀번호입니다.';
    }
    checkPwMatch();
});

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

// 휴대폰 형식 실시간 검증
document.getElementById('userPhone').addEventListener('input', function () {
    const phone = this.value.trim();
    const msg = document.getElementById('phone-msg');

    if (phone.length === 0) {
        msg.textContent = '';
        return;
    }

    if (!PHONE_PATTERN.test(phone)) {
        msg.style.color = '#c0392b';
        msg.textContent = '휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)';
    } else {
        msg.style.color = '#4CAF50';
        msg.textContent = '';
    }
});

// 휴대폰 인증 (백엔드 API가 아직 없어서 임시 처리)
function sendPhoneCode() {
    const phone = document.getElementById('userPhone').value.trim();

    if (!PHONE_PATTERN.test(phone)) {
        alert('휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)');
        return;
    }

    alert('휴대폰 인증 기능은 아직 준비 중입니다.');
}

// 회원가입 제출
async function submitJoin() {
    const id = document.getElementById('userId').value.trim();
    const pw = document.getElementById('userPw').value;
    const email = document.getElementById('userEmail').value.trim();
    const name = document.getElementById('userName').value.trim();
    const birthDate = document.getElementById('userBirth').value;
    const genderInput = document.querySelector('input[name="gender"]:checked');
    const gender = genderInput ? genderInput.value : '';
    const phone = document.getElementById('userPhone').value.trim();
    const zipCode = document.getElementById('postcode').value.trim();
    const addr1 = document.getElementById('addrBase').value.trim();
    const addr2 = document.getElementById('addrDetail').value.trim();

    if (!name) {
        alert('이름을 입력해 주세요.');
        return;
    }

    if (!idChecked) {
        alert('아이디 중복확인을 해주세요.');
        return;
    }

    if (!USERPW_PATTERN.test(pw)) {
        alert('비밀번호는 8~12자, 영문/숫자/특수문자를 모두 포함해야 합니다.');
        return;
    }

    if (!EMAIL_PATTERN.test(email) || !emailChecked) {
        alert('이메일 중복확인을 완료해 주세요.');
        return;
    }

    if (!emailVerified) {
        alert('이메일 인증을 완료해 주세요.');
        return;
    }

    if (phone && !PHONE_PATTERN.test(phone)) {
        alert('휴대폰 번호 형식이 올바르지 않습니다. (예: 010-1234-5678)');
        return;
    }

    try {
        const signupRes = await fetch('/K_Market/api/member/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                uid: id,
                password: pw,
                email: email,
                name: name,
                birthDate: birthDate || undefined,
                gender: gender || undefined,
                phone: phone || undefined,
                zipCode: zipCode || undefined,
                addr1: addr1 || undefined,
                addr2: addr2 || undefined
            })
        });

        if (!signupRes.ok) {
            alert('회원가입 실패');
            return;
        }

        alert('회원가입 완료!');
        window.location.href = '/K_Market/';
    } catch (err) {
        console.error(err);
        alert('통신 오류');
    }
}