// 헤더의 로그인 상태 표시 처리
let isLoggedIn = false;

document.addEventListener('DOMContentLoaded', async function () {
    try {
        const res = await fetch('/K_Market/api/member/me', {
            credentials: 'include'
        });

        if (res.ok) {
            const member = await res.json();
            if (member) {
                isLoggedIn = true;

                const authArea = document.getElementById('authArea');
                const memberOnlyArea = document.getElementById('memberOnlyArea');

                if (authArea) {
                    // ===== 로그인/회원가입 링크 + 구분자(|) 숨기기 =====
                    const loginLink = document.getElementById('navLoginLink');
                    const joinLink = document.getElementById('navJoinLink');
                    if (loginLink) loginLink.style.display = 'none';
                    if (joinLink) joinLink.style.display = 'none';

                    const authSeparator = authArea.querySelector('span');
                    if (authSeparator) authSeparator.style.display = 'none';

                    const safeName = escapeHtml(member.name);

                    // ===== 판매자(SELLER)면 "판매자" 표시 + admin 이동 링크 =====
                    const isSeller = member.memberType === 'SELLER';
                    const isAdmin = member.memberType === 'ADMIN';

                    let sellerBadgeHtml = '';
                    if (isAdmin) {
                        sellerBadgeHtml =
                            '<a href="/K_Market/admin/main" id="sellerAdminLink" ' +
                            'style="font-size:13px; color:#d33333; font-weight:700;">관리자</a>' +
                            '<span style="color:var(--color-gray-200);">|</span>';
                    } else if (isSeller) {
                        sellerBadgeHtml =
                            '<a href="/K_Market/admin/product/list" id="sellerAdminLink" ' +
                            'style="font-size:13px; color:#1a6fd3; font-weight:700;">판매자</a>' +
                            '<span style="color:var(--color-gray-200);">|</span>';
                    }

                    // 환영메시지 + 로그아웃을 authArea 끝에 추가 (기존 로그인/회원가입 링크는 숨김 상태로 유지)
                    const welcomeHtml =
                        '<span id="welcomeText" style="font-size:13px; color:var(--color-gray-600);">' +
                        safeName + '님 환영합니다' +
                        '</span>' +
                        '<span style="color:var(--color-gray-200);">|</span>' +
                        sellerBadgeHtml +
                        '<a href="#" id="navLogoutLink" style="font-size:13px; color:var(--color-gray-600);">로그아웃</a>';

                    authArea.insertAdjacentHTML('beforeend', welcomeHtml);

                    // 로그인 상태 -> 마이페이지 노출
                    if (memberOnlyArea) {
                        memberOnlyArea.style.display = 'flex';
                    }

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
                        alert('로그아웃 되었습니다.');
                        window.location.href = '/K_Market/';
                    });
                }
            }
        }
    } catch (err) {
        console.error('로그인 상태 확인 실패', err);
    }

    // 로그인 안 했을 시 장바구니 접근 차단
    const cartLink = document.getElementById('cartLink');
    if (cartLink) {
        cartLink.addEventListener('click', function (e) {
            if (!isLoggedIn) {
                e.preventDefault();
                alert('로그인이 필요한 페이지입니다.');
                window.location.href = '/K_Market/member/login';
            }
        });
    }
});

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
