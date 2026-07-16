/* =============================================
   KMarket Admin - main-dashboard.js
   ============================================= */

// 카테고리 색상 팔레트 (5개 기준: 1차 카테고리 4개 + 미분류)
const CATEGORY_COLORS = ['#2563eb', '#16a34a', '#e8380d', '#f97316', '#9333ea'];

document.addEventListener('DOMContentLoaded', function () {
    renderBarChart();
    renderPieChart();
});

/* ── 막대 차트 (최근 5일 x 1차 카테고리별 매출) ── */
function renderBarChart() {
    const rawData = window.categorySalesDaily || [];
    // rawData: [{ saleDate: '2026-07-15', cateId: 1, cateName: '미분류', amount: 1000 }, ...]

    if (rawData.length === 0) return;

    // 1) 등장하는 카테고리 목록 추출 (cateId 기준 정렬, 색상 매핑용)
    const categoryMap = new Map(); // cateId -> cateName
    rawData.forEach(row => {
        if (!categoryMap.has(row.cateId)) {
            categoryMap.set(row.cateId, row.cateName);
        }
    });
    const categories = [...categoryMap.entries()]
        .sort((a, b) => a[0] - b[0])
        .map(([cateId, cateName]) => ({ cateId, cateName }));

    // 2) 날짜별로 그룹핑: { '07-15': { '가전': 1000, '식품': 500, ... }, ... }
    const dateGroups = {};
    rawData.forEach(row => {
        const label = row.saleDate.slice(5); // '2026-07-15' -> '07-15'
        if (!dateGroups[label]) dateGroups[label] = {};
        dateGroups[label][row.cateName] = row.amount;
    });
    const dateLabels = Object.keys(dateGroups).sort();

    // 3) 최대값 계산 (막대 높이 비율용)
    let maxVal = 0;
    dateLabels.forEach(label => {
        categories.forEach(({ cateName }) => {
            const val = dateGroups[label][cateName] || 0;
            if (val > maxVal) maxVal = val;
        });
    });
    if (maxVal === 0) maxVal = 1; // 0으로 나누기 방지

    const chartH = 148; // px (내부 높이)

    const barChart = document.getElementById('barChart');
    const barLabels = document.getElementById('barLabels');
    if (!barChart) return;

    barChart.innerHTML = '';
    barLabels.innerHTML = '';

    dateLabels.forEach(label => {
        const g = document.createElement('div');
        g.className = 'bar-group';

        categories.forEach(({ cateName }, idx) => {
            const val = dateGroups[label][cateName] || 0;
            const bar = document.createElement('div');
            bar.className = 'bar';
            bar.style.background = CATEGORY_COLORS[idx % CATEGORY_COLORS.length];
            bar.style.height = Math.round((val / maxVal) * chartH) + 'px';
            bar.title = `${cateName}: ${val.toLocaleString()}`;
            g.appendChild(bar);
        });

        barChart.appendChild(g);

        const lbl = document.createElement('div');
        lbl.className = 'bar-label';
        lbl.textContent = label;
        barLabels.appendChild(lbl);
    });

    renderBarLegend(categories);
}

/* ── 막대 차트 범례 (카테고리 동적 렌더링) ── */
function renderBarLegend(categories) {
    const legend = document.querySelector('.chart-legend');
    if (!legend) return;

    legend.innerHTML = '';
    categories.forEach(({ cateName }, idx) => {
        const span = document.createElement('span');
        span.className = 'leg';
        span.style.color = CATEGORY_COLORS[idx % CATEGORY_COLORS.length];
        span.textContent = `■ ${cateName}`;
        legend.appendChild(span);
    });
}

/* ── 파이 차트 (Canvas) — 1차 카테고리별 전체 매출 합계 ── */
function renderPieChart() {
    const canvas = document.getElementById('pieChart');
    if (!canvas) return;

    const rawTotal = window.categorySalesTotal || [];
    // rawTotal: [{ cateId: 1, cateName: '미분류', totalAmount: 5000 }, ...]

    const data = rawTotal
        .slice()
        .sort((a, b) => a.cateId - b.cateId)
        .map((row, idx) => ({
            label: row.cateName,
            value: row.totalAmount,
            color: CATEGORY_COLORS[idx % CATEGORY_COLORS.length]
        }));

    if (data.length === 0) return;

    function draw() {
        const wrap = canvas.parentElement;
        const size = Math.min(wrap.clientWidth, 200);
        canvas.width = size;
        canvas.height = size;

        const ctx = canvas.getContext('2d');
        const total = data.reduce((s, d) => s + d.value, 0);
        const cx = size / 2;
        const cy = size / 2;
        const r = Math.min(cx, cy) - 4;

        if (total === 0) return;

        let startAngle = -Math.PI / 2;
        data.forEach(d => {
            const slice = (d.value / total) * Math.PI * 2;
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, r, startAngle, startAngle + slice);
            ctx.closePath();
            ctx.fillStyle = d.color;
            ctx.fill();
            ctx.strokeStyle = '#fff';
            ctx.lineWidth = 2;
            ctx.stroke();
            startAngle += slice;
        });
    }

    draw();
    window.addEventListener('resize', draw);

    const legend = document.getElementById('pieLegend');
    if (legend) {
        legend.innerHTML = '';
        data.forEach(d => {
            const item = document.createElement('div');
            item.className = 'pie-leg';
            item.innerHTML = `
                <span class="pie-dot" style="background:${d.color}"></span>
                <span>${d.label}</span>
            `;
            legend.appendChild(item);
        });
    }
}