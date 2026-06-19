/* =============================================
   KMarket Admin - admin.js
   ============================================= */

/* ── 막대 차트 ── */
(function renderBarChart() {
  const data = [
    { label: '10-10', order: 120, pay: 90,  cancel: 20 },
    { label: '10-11', order: 200, pay: 160, cancel: 35 },
    { label: '10-12', order: 170, pay: 130, cancel: 28 },
    { label: '10-13', order: 145, pay: 110, cancel: 15 },
    { label: '10-14', order: 180, pay: 140, cancel: 30 },
  ];

  const maxVal = Math.max(...data.flatMap(d => [d.order, d.pay, d.cancel]));
  const chartH  = 148; // px (내부 높이)

  const barChart   = document.getElementById('barChart');
  const barLabels  = document.getElementById('barLabels');
  if (!barChart) return;

  data.forEach(d => {
    const g = document.createElement('div');
    g.className = 'bar-group';

    ['order', 'pay', 'cancel'].forEach(key => {
      const bar = document.createElement('div');
      bar.className = `bar ${key}`;
      bar.style.height = Math.round((d[key] / maxVal) * chartH) + 'px';
      bar.title = `${key}: ${d[key]}`;
      g.appendChild(bar);
    });

    barChart.appendChild(g);

    const lbl = document.createElement('div');
    lbl.className = 'bar-label';
    lbl.textContent = d.label;
    barLabels.appendChild(lbl);
  });
})();


/* ── 파이 차트 (Canvas) — 부모 너비에 맞게 반응형 ── */
(function renderPieChart() {
  const canvas = document.getElementById('pieChart');
  if (!canvas) return;

  const data = [
    { label: '가전', value: 35, color: '#2563eb' },
    { label: '식품', value: 25, color: '#16a34a' },
    { label: '의류', value: 22, color: '#e8380d' },
    { label: '기타', value: 18, color: '#f97316' },
  ];

  function draw() {
    // 부모 너비에서 캔버스 크기 결정 (정사각형)
    const wrap = canvas.parentElement;
    const size = Math.min(wrap.clientWidth, 200); // 최대 200px
    canvas.width  = size;
    canvas.height = size;

    const ctx = canvas.getContext('2d');
    const total = data.reduce((s, d) => s + d.value, 0);
    const cx = size / 2;
    const cy = size / 2;
    const r  = Math.min(cx, cy) - 4;

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

  // 범례
  const legend = document.getElementById('pieLegend');
  if (legend) {
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
})();


/* ── Aside 서브메뉴 — hover 유지 (CSS 보완) ── */
// CSS :hover만으로도 동작하지만, 터치 디바이스 대비
document.querySelectorAll('.nav-item').forEach(item => {
  item.addEventListener('mouseenter', () => {
    document.querySelectorAll('.nav-item').forEach(i => {
      if (i !== item) i.classList.remove('open');
    });
  });
});