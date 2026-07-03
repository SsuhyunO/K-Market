 document.getElementById('newPw').addEventListener('input', function() {
            const pw = this.value;
            const msg = document.getElementById('pw-msg');
            if (pw.length >= 8 && pw.length <= 12) {
                msg.style.color = '#4CAF50';
                msg.textContent = '사용 가능한 비밀번호 입니다.';
            } else if (pw.length > 0) {
                msg.style.color = '#c0392b';
                msg.textContent = '8~12자로 입력해 주세요.';
            } else {
                msg.style.color = '#888';
                msg.textContent = '사용 가능한 비밀번호 입니다.';
            }
        });

        function submitPw() {
            const pw   = document.getElementById('newPw').value;
            const pwCf = document.getElementById('newPwConfirm').value;
            if (!pw || pw.length < 8 || pw.length > 12) {
                alert('비밀번호는 8~12자로 입력해 주세요.'); return;
            }
            if (pw !== pwCf) {
                alert('비밀번호가 일치하지 않습니다.'); return;
            }
            alert('비밀번호가 변경되었습니다.');
            window.location.href = loginUrl;
        }