/* =============================================
   KMarket - recruit-pagination.js
   채용 페이지 페이지네이션 동작 확인용 스크립트

   ※ 실제 서버 페이징(Controller + Service + DB)이 붙기 전까지
     클릭이 "먹는지" 눈으로 확인하기 위한 임시 클라이언트 로직입니다.
     실제 구현 시에는 이 JS 대신 GET /company/recruit?page=N 형태로
     서버에 요청해서 Controller가 새 데이터를 모델에 담아 렌더링하면 됩니다.
   ============================================= */

document.addEventListener('DOMContentLoaded', function () {
    initRecruitPagination();
});

function initRecruitPagination() {
    const pagination = document.getElementById('recruitPagination');
    const tableBody = document.getElementById('recruitTableBody');
    if (!pagination || !tableBody) return;

    const TOTAL_PAGES = 5;
    let currentPage = 1;

    // 페이지별 더미 데이터 (실제로는 서버에서 내려줄 데이터)
    const dummyData = {
        1: [
            { id: 20, dept: '광고사업', exp: '경력 4 ~ 15년', type: '정규직', title: '광고 플랫폼 기획', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 19, dept: 'IT',       exp: '경력 2 ~ 15년', type: '정규직', title: 'IT 인재 상시 영입(경력)', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 18, dept: '운영',     exp: '무관',          type: '계약직', title: '단기 아르바이트 모집', open: false, period: '0000-00-00 ~ 0000-00-00' },
            { id: 17, dept: '경영',     exp: '신입',          type: '계약직', title: '경영지원 직원 채용 공고', open: false, period: '0000-00-00 ~ 0000-00-00' },
        ],
        2: [
            { id: 16, dept: 'MD',       exp: '경력 3 ~ 10년', type: '정규직', title: '패션 카테고리 MD 모집', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 15, dept: '물류',     exp: '경력 1 ~ 5년',  type: '정규직', title: '물류센터 운영 매니저', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 14, dept: '마케팅',   exp: '경력 2 ~ 7년',  type: '정규직', title: '퍼포먼스 마케터', open: false, period: '0000-00-00 ~ 0000-00-00' },
            { id: 13, dept: '디자인',   exp: '경력 3 ~ 8년',  type: '정규직', title: 'UI/UX 디자이너', open: false, period: '0000-00-00 ~ 0000-00-00' },
        ],
        3: [
            { id: 12, dept: '개발',     exp: '경력 2 ~ 10년', type: '정규직', title: '백엔드 개발자(Java/Spring)', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 11, dept: '개발',     exp: '경력 1 ~ 7년',  type: '정규직', title: '프론트엔드 개발자(React)', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 10, dept: '데이터',   exp: '경력 3 ~ 10년', type: '정규직', title: '데이터 엔지니어', open: false, period: '0000-00-00 ~ 0000-00-00' },
            { id: 9,  dept: '고객센터', exp: '무관',          type: '계약직', title: '고객상담 직원 모집', open: false, period: '0000-00-00 ~ 0000-00-00' },
        ],
        4: [
            { id: 8, dept: '재무',     exp: '경력 5 ~ 12년', type: '정규직', title: '재무회계 담당자', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 7, dept: '법무',     exp: '경력 3 ~ 9년',  type: '정규직', title: '법무팀 변호사', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 6, dept: '인사',     exp: '경력 2 ~ 6년',  type: '정규직', title: 'HR 매니저', open: false, period: '0000-00-00 ~ 0000-00-00' },
            { id: 5, dept: '구매',     exp: '경력 1 ~ 5년',  type: '계약직', title: 'MD 보조 인턴', open: false, period: '0000-00-00 ~ 0000-00-00' },
        ],
        5: [
            { id: 4, dept: '광고사업', exp: '경력 4 ~ 15년', type: '정규직', title: '광고 플랫폼 기획', open: true,  period: '0000-00-00 ~ 0000-00-00' },
            { id: 3, dept: 'IT',       exp: '경력 2 ~ 15년', type: '정규직', title: 'IT 인재 상시 영입(경력)', open: true, period: '0000-00-00 ~ 0000-00-00' },
            { id: 2, dept: '운영',     exp: '무관',          type: '계약직', title: '단기 아르바이트 모집', open: false, period: '0000-00-00 ~ 0000-00-00' },
            { id: 1, dept: '경영',     exp: '신입',          type: '계약직', title: '경영지원 직원 채용 공고', open: false, period: '0000-00-00 ~ 0000-00-00' },
        ],
    };

    function renderPage(page) {
        const rows = dummyData[page] || [];
        tableBody.innerHTML = rows.map(job => `
      <tr>
        <td>${job.id}</td>
        <td>${job.dept}</td>
        <td>${job.exp}</td>
        <td>${job.type}</td>
        <td>${job.title}</td>
        <td><span class="status ${job.open ? 'open' : 'closed'}">${job.open ? '모집중' : '종료'}</span></td>
        <td>${job.period}</td>
      </tr>
    `).join('');
    }

    function updateActiveButton(page) {
        pagination.querySelectorAll('.page-num').forEach(btn => {
            btn.classList.toggle('active', Number(btn.dataset.page) === page);
        });
    }

    function goToPage(page) {
        if (page < 1 || page > TOTAL_PAGES) return; // 범위 밖이면 무시
        currentPage = page;
        renderPage(currentPage);
        updateActiveButton(currentPage);
    }

    pagination.addEventListener('click', function (e) {
        e.preventDefault();
        const target = e.target.closest('a');
        if (!target) return;

        const pageData = target.dataset.page;

        if (pageData === 'prev') {
            goToPage(currentPage - 1);
        } else if (pageData === 'next') {
            goToPage(currentPage + 1);
        } else {
            goToPage(Number(pageData));
        }
    });

    // 초기 1페이지 렌더링
    renderPage(currentPage);
}