let currentTab = 'email';
        let emailVerified = false;
        let phoneVerified = false;

        function switchTab(event, tab) {
            currentTab = tab;
            document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-panel').forEach(panel => panel.classList.remove('active'));
            event.target.classList.add('active');
            document.getElementById('panel-' + tab).classList.add('active');
            document.getElementById('guide-email').style.display = tab === 'email' ? 'block' : 'none';
            document.getElementById('guide-phone').style.display = tab === 'phone' ? 'block' : 'none';
        }

        function sendEmailCode() {
            const email = document.getElementById('email-addr').value.trim();
            if (!email) { alert('이메일을 입력해 주세요.'); return; }
            alert('인증번호를 발송했습니다.');
        }

        function confirmEmailCode() {
            const code = document.getElementById('email-code').value.trim();
            if (!code) { alert('인증번호를 입력해 주세요.'); return; }
            emailVerified = true;
            alert('이메일 인증이 완료되었습니다.');
        }

        function sendPhoneCode() {
            const phone = document.getElementById('phone-num').value.trim();
            if (!phone) { alert('휴대폰 번호를 입력해 주세요.'); return; }
            alert('인증번호를 발송했습니다.');
        }

        function confirmPhoneCode() {
            const code = document.getElementById('phone-code').value.trim();
            if (!code) { alert('인증번호를 입력해 주세요.'); return; }
            phoneVerified = true;
            alert('휴대폰 인증이 완료되었습니다.');
        }

        function goNext() {
            if (currentTab === 'email') {
                const id    = document.getElementById('email-id').value.trim();
                const email = document.getElementById('email-addr').value.trim();
                if (!id)    { alert('아이디를 입력해 주세요.'); return; }
                if (!email) { alert('이메일을 입력해 주세요.'); return; }
                if (!emailVerified) { alert('이메일 인증을 완료해 주세요.'); return; }
            } else {
                const id    = document.getElementById('phone-id').value.trim();
                const phone = document.getElementById('phone-num').value.trim();
                if (!id)    { alert('아이디를 입력해 주세요.'); return; }
                if (!phone) { alert('휴대폰 번호를 입력해 주세요.'); return; }
                if (!phoneVerified) { alert('휴대폰 인증을 완료해 주세요.'); return; }
            }
            window.location.href = changePasswordUrl;
        }