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
    const id = document.getElementById('userId').value.trim();
    const pw = document.getElementById('userPw').value;
    const pwCf = document.getElementById('userPwConfirm').value;
    const company = document.getElementById('companyName').value.trim();
    const ceo = document.getElementById('ceoName').value.trim();
    const bizNum = document.getElementById('bizNum').value.trim();
    const mailNum = document.getElementById('mailOrderNum').value.trim();
    const tel = document.getElementById('tel').value.trim();

    if (!id || id.length < 4 || id.length > 12) {
        alert('아이디는 4~12자로 입력해 주세요.');
        return;
    }
    if (!pw || pw.length < 8 || pw.length > 12) {
        alert('비밀번호는 8~12자로 입력해 주세요.');
        return;
    }
    if (pw !== pwCf) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    if (!company) {
        alert('회사명을 입력해 주세요.');
        return;
    }
    if (!ceo) {
        alert('대표자명을 입력해 주세요.');
        return;
    }
    if (!bizNum) {
        alert('사업자등록번호를 입력해 주세요.');
        return;
    }
    if (!mailNum) {
        alert('통신판매업번호를 입력해 주세요.');
        return;
    }
    if (!tel) {
        alert('전화번호를 입력해 주세요.');
        return;
    }

    try {
        // 1) 아이디 중복확인
        const checkIdRes = await fetch(`/K_Market/api/member/check-uid?uid=${encodeURIComponent(id)}`);
        const idDuplicate = await checkIdRes.json();
        if (idDuplicate) {
            alert('이미 사용 중인 아이디입니다.');
            return;
        }

        // 2) 사업자등록번호 중복확인
        const checkBizRes = await fetch(`/K_Market/api/seller/check-bizregno?bizRegNo=${encodeURIComponent(bizNum)}`);
        const bizDuplicate = await checkBizRes.json();
        if (bizDuplicate) {
            alert('이미 등록된 사업자등록번호입니다.');
            return;
        }

        // 3) 판매자 회원가입 요청 (대표자명 = name, 주소 = zipCode/addr1/addr2)
        const signupRes = await fetch('/K_Market/api/seller/signup', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                uid: id,
                password: pw,
                name: ceo,
                phone: tel,
                email: document.getElementById('userEmail') ? document.getElementById('userEmail').value.trim() : '',
                zipCode: document.getElementById('postcode') ? document.getElementById('postcode').value : '',
                addr1: document.getElementById('addrBase') ? document.getElementById('addrBase').value : '',
                addr2: document.getElementById('addrDetail') ? document.getElementById('addrDetail').value : '',
                companyName: company,
                bizRegNo: bizNum,
                onlineSalesNo: mailNum,
                tel: tel,
                fax: document.getElementById('fax') ? document.getElementById('fax').value : ''
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