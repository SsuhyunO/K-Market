import { initPagination } from '../global/pagination.js';
import { escapeHtml } from '../global/htmlUtils.js';

const modalStack = [];

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;

    const current = document.querySelector('.modal-overlay.active');
    if (current && current.id !== modalId) {
        current.classList.remove('active');
        modalStack.push(current.id);
    }

    modal.classList.add('active');
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (!modal) return;
    modal.classList.remove('active');

    if (modalStack.length > 0) {
        const prevId = modalStack.pop();
        const prevModal = document.getElementById(prevId);
        if (prevModal) prevModal.classList.add('active');
    }
}

document.querySelectorAll('[data-close]').forEach(button => {
    button.addEventListener('click', () => {
        closeModal(button.getAttribute('data-close'));
    });
});

document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', event => {
        if (event.target === overlay) {
            closeModal(overlay.id);
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    initPeriodControls();
    initPagination({
        selector: '#pagination',
        fetchPage: loadPoints,
        onError: handlePointLoadError
    });
});

async function loadPoints(page = 1) {
    const params = new URLSearchParams();
    params.set('page', page);

    const range = getSelectedDateRange();
    if (range.startDate && range.endDate) {
        params.set('startDate', range.startDate);
        params.set('endDate', range.endDate);
    }

    const response = await fetch(`${getContextPath()}point/api/list?${params.toString()}`, {
        headers: { Accept: 'application/json' }
    });

    if (!response.ok) {
        throw new Error('포인트 내역 조회 실패: ' + response.status);
    }

    const pageData = await response.json();
    renderPointRows(pageData);
    return pageData;
}

function renderPointRows(pageData) {
    const tbody = document.getElementById('pointListBody');
    tbody.innerHTML = '';

    const points = pageData?.list || [];
    if (points.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="order-empty">조회된 포인트내역이 없습니다.</td></tr>';
        return;
    }

    points.forEach(point => {
        const tr = document.createElement('tr');
        const isPlus = Number(point.point) >= 0;
        const amountClass = isPlus ? 'point-plus' : 'point-minus';
        const amountText = `${isPlus ? '+' : '-'}${formatNumber(Math.abs(Number(point.point)))}`;

        tr.innerHTML = `
            <td>${escapeHtml(point.createdAt)}</td>
            <td>${escapeHtml(point.content)}</td>
            <td>${point.orderNo > 0 ? escapeHtml(point.orderNo) : '-'}</td>
            <td class="${amountClass}">${amountText}</td>
            <td>${escapeHtml(point.note)}</td>
            <td>${escapeHtml(point.expireDate || '-')}</td>
        `;

        tbody.appendChild(tr);
    });
}

function initPeriodControls() {
    document.querySelectorAll('input[name="periodType"]').forEach(radio => {
        radio.addEventListener('change', () => {
            document.getElementById('periodMonthSelect').value = '0';
            document.getElementById('periodStartDate').value = '';
            document.getElementById('periodEndDate').value = '';
        });
    });

    document.getElementById('periodMonthSelect')?.addEventListener('change', () => {
        document.querySelectorAll('input[name="periodType"]').forEach(radio => {
            radio.checked = false;
        });
        document.getElementById('periodStartDate').value = '';
        document.getElementById('periodEndDate').value = '';
    });

    document.querySelectorAll('.period-date-input').forEach(input => {
        input.addEventListener('change', () => {
            document.querySelectorAll('input[name="periodType"]').forEach(radio => {
                radio.checked = false;
            });
            document.getElementById('periodMonthSelect').value = '0';
        });
    });

    document.getElementById('periodSearchBtn')?.addEventListener('click', () => {
        const range = getSelectedDateRange();
        if (!range.valid) return;

        const params = new URLSearchParams(window.location.search);
        params.set('page', '1');
        window.history.replaceState(null, '', `${window.location.pathname}?${params.toString()}`);
        window.dispatchEvent(new CustomEvent('pagination:refresh', { detail: { page: 1 } }));
    });
}

function getSelectedDateRange() {
    const startDateValue = document.getElementById('periodStartDate')?.value;
    const endDateValue = document.getElementById('periodEndDate')?.value;

    if (startDateValue || endDateValue) {
        if (!startDateValue || !endDateValue) {
            alert('시작일과 종료일을 모두 선택해주세요.');
            return { valid: false };
        }
        if (startDateValue > endDateValue) {
            alert('시작일이 종료일보다 늦을 수 없습니다.');
            return { valid: false };
        }

        const oneYearLater = addYears(new Date(startDateValue), 1);
        if (new Date(endDateValue) > oneYearLater) {
            alert('조회 기간은 최대 1년까지 가능합니다.');
            return { valid: false };
        }

        return {
            valid: true,
            startDate: startDateValue,
            endDate: endDateValue
        };
    }

    const checkedPeriod = document.querySelector('input[name="periodType"]:checked');
    if (checkedPeriod) {
        const days = Number(checkedPeriod.dataset.days || 30);
        const endDate = new Date();
        const startDate = new Date();
        startDate.setDate(startDate.getDate() - days);

        return {
            valid: true,
            startDate: formatDate(startDate),
            endDate: formatDate(endDate)
        };
    }

    const monthSelectValue = Number(document.getElementById('periodMonthSelect')?.value || 0);
    const endDate = new Date();
    const startDate = new Date();
    startDate.setMonth(startDate.getMonth() - (monthSelectValue + 1));

    return {
        valid: true,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
    };
}

function handlePointLoadError(error) {
    console.error(error);
    alert('포인트 내역을 불러오지 못했습니다.');
    document.getElementById('pointListBody').innerHTML =
        '<tr><td colspan="6" class="order-empty">포인트 내역을 불러오지 못했습니다.</td></tr>';
}

function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

function addYears(date, years) {
    const next = new Date(date);
    next.setFullYear(next.getFullYear() + years);
    return next;
}

function formatNumber(value) {
    return Number(value || 0).toLocaleString('ko-KR');
}

function getContextPath() {
    return document.querySelector('meta[name="context-path"]')?.content || '/';
}
