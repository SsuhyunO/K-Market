// 구글 추가정보 입력 페이지 전용 스크립트

function openPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById('addrBase').value = data.roadAddress || data.jibunAddress;
            document.getElementById('addrDetail').focus();
        }
    }).open();
}

async function submitGoogleProfile() {
    const name = document.getElementById('userName').value.trim();
    const birthDate = document.getElementById('userBirth').value;
    const genderEl = document.querySelector('input[name="gender"]:checked');
    const phone = document.getElementById('userPhone').value.trim();
    const zipCode = document.getElementById('postcode').value.trim();
    const addr1 = document.getElementById('addrBase').value.trim();
    const addr2 = document.getElementById('addrDetail').value.trim();

    if (!name) {
        alert('이름을 입력해주세요.');
        return;
    }
    if (!birthDate) {
        alert('생년월일을 입력해주세요.');
        return;
    }
    if (!genderEl) {
        alert('성별을 선택해주세요.');
        return;
    }
    if (!phone) {
        alert('휴대폰 번호를 입력해주세요.');
        return;
    }

    const phonePattern = /^\d{2,3}-\d{3,4}-\d{4}$/;
    if (!phonePattern.test(phone)) {
        alert('휴대폰 번호 형식이 올바르지 않습니다. 예: 010-1234-5678');
        return;
    }

    try {
        const res = await fetch(completeProfileUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({
                name: name,
                birthDate: birthDate,
                gender: genderEl.value,
                phone: phone,
                zipCode: zipCode,
                addr1: addr1,
                addr2: addr2
            })
        });

        const result = await res.json();

        if (result.success) {
            // 이 시점부터 서버 세션에 loginMember가 세팅됨 -> 메인 이동 시 헤더에 환영메시지 표시됨
            alert('가입이 완료되었습니다.');
            window.location.href = indexUrl;
        } else {
            alert(result.message || '저장 중 오류가 발생했습니다.');
        }
    } catch (err) {
        console.error(err);
        alert('서버와 통신 중 오류가 발생했습니다.');
    }
}