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
                    const safeName = escapeHtml(member.name);

                    // ===== 추가된 부분: 판매자(SELLER)면 "판매자" 표시 + admin 이동 링크 =====
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
                            '<a href="/K_Market/admin/main" id="sellerAdminLink" ' +
                            'style="font-size:13px; color:#1a6fd3; font-weight:700;">판매자</a>' +
                            '<span style="color:var(--color-gray-200);">|</span>';
                    }

                    authArea.innerHTML =
                        '<span style="font-size:13px; color:var(--color-gray-600);">' +
                        safeName + '님 환영합니다' +
                        '</span>' +
                        '<span style="color:var(--color-gray-200);">|</span>' +
                        sellerBadgeHtml +
                        '<a href="#" id="navLogoutLink" style="font-size:13px; color:var(--color-gray-600);">로그아웃</a>';

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