 function openPostcode() {
            new daum.Postcode({
                oncomplete: function(data) {
                    document.getElementById('postcode').value = data.zonecode;
                    document.getElementById('addrBase').value = data.roadAddress || data.jibunAddress;
                    document.getElementById('addrDetail').focus();
                }
            }).open();
        }

        function submitJoin() {
            const id      = document.getElementById('userId').value.trim();
            const pw      = document.getElementById('userPw').value;
            const pwCf    = document.getElementById('userPwConfirm').value;
            const company = document.getElementById('companyName').value.trim();
            const ceo     = document.getElementById('ceoName').value.trim();
            const bizNum  = document.getElementById('bizNum').value.trim();
            const mailNum = document.getElementById('mailOrderNum').value.trim();
            const tel     = document.getElementById('tel').value.trim();

            if (!id || id.length < 4 || id.length > 12) {
                alert('아이디는 4~12자로 입력해 주세요.'); return;
            }
            if (!pw || pw.length < 8 || pw.length > 12) {
                alert('비밀번호는 8~12자로 입력해 주세요.'); return;
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
            if (!mailNum) {
                alert('통신판매업번호를 입력해 주세요.'); return;
            }
            if (!tel) {
                alert('전화번호를 입력해 주세요.'); return;
            }

            alert('판매자 회원가입이 완료되었습니다!');
            window.location.href = indexUrl;
        }