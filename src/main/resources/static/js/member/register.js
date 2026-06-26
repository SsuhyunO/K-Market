// 이메일 인증
        function sendEmailCode() {
            const email = document.getElementById('userEmail').value.trim();
            if (!email) {
                alert('이메일을 입력해 주세요.');
                return;
            }
            alert('인증 메일을 발송했습니다.\n(실제 구현 시 서버 연동 필요)');
        }

        // 휴대폰 인증
        function sendPhoneCode() {
            const phone = document.getElementById('userPhone').value.trim();
            if (!phone) {
                alert('휴대폰 번호를 입력해 주세요.');
                return;
            }
            alert('인증 문자를 발송했습니다.\n(실제 구현 시 서버 연동 필요)');
        }

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

        // 회원가입 제출 (유효성 검사)
        function submitJoin() {
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

            // 실제 구현 시 서버로 전송
            alert('회원가입이 완료되었습니다!');
            window.location.href = '/kmarket/index';
        }