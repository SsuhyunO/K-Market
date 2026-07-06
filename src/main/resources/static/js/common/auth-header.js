// 헤더의 로그인 상태 표시 처리
document.addEventListener('DOMContentLoaded', async function () {
    try {
        const res = await fetch('/K_Market/api/member/me', {
            credentials: 'include'
        });

        if (!res.ok) return; // 세션 없음 등 -> 기본 헤더(로그인/회원가입) 그대로 둠

        const member = await res.json();
        if (!member) return; // 비로그인 상태

        const authArea = document.getElementById('authArea');
        if (!authArea) return;

        const safeName = escapeHtml(member.name);

        authArea.innerHTML =
            '<span style="font-size:13px; color:var(--color-gray-600);">' +
            safeName + '님 환영합니다' +
            '</span>' +
            '<span style="color:var(--color-gray-200);">|</span>' +
            '<a href="#" id="navLogoutLink" style="font-size:13px; color:var(--color-gray-600);">로그아웃</a>';

        document.getElementById('navLogoutLink').addEventListener('click', async function (e) {
            e.preventDefault();
            try {
                await fetch('/K_Market/api/member/logout', {
                    method: 'POST',
                    credentials: 'include'
                });
            } catch (err) {
                console.error(err);
            }
            window.location.href = '/K_Market/';
        });

    } catch (err) {
        console.error('로그인 상태 확인 실패', err);
    }
});

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
