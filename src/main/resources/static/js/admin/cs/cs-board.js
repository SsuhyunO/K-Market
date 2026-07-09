import { delegate } from '../../global/event-manager.js';
import { initModals } from '../../global/modal-form.js';
import { Validation } from '../../global/validation.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { ManagementTableForm } from '../global/management-table-form.js';

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initRecruitRegisterValidation();
    initCsBoardLinks();
    initCsDeleteButtons();
    initCsSingleDeleteButtons();

    // 관리자 고객센터 기능 추가
    initNoticeTypeFilter();
    initFaqCategoryFilter();
    initQnaFilter();
});

function initRecruitRegisterValidation() {
    const form = document.getElementById("recruit-register-form");
    if (!form) return;

    ModalFormValidation.bind({
        form,
        validate: validateRecruitRegisterForm,
        isField: isRecruitRegisterField,
        getRelatedFieldIds: getRecruitRegisterRelatedFieldIds,
        ensureErrors: ensureRecruitRegisterErrors,
        validateOnOpen: true
    });
}

function ensureRecruitRegisterErrors(form) {
    FormValidation.ensureFieldErrors(
        form,
        "#recruit-title, #recruit-department, #recruit-career, #recruit-employment-type, #recruit-start-date, #recruit-end-date, #recruit-note"
    );
}

function validateRecruitRegisterForm(form) {
    const errors = [];

    FormValidation.addRequiredError(form, "recruit-title", "제목을 입력해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-department", "채용부서를 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-career", "경력사항을 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-employment-type", "채용형태를 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-start-date", "모집 시작일을 선택해주세요.", errors);
    FormValidation.addRequiredError(form, "recruit-end-date", "모집 종료일을 선택해주세요.", errors);

    const startDate = form.querySelector("#recruit-start-date")?.value || "";
    const endDate = form.querySelector("#recruit-end-date")?.value || "";
    if (startDate !== "" && endDate !== "" && !Validation.greaterThanOrEqual(startDate, endDate).valid) {
        errors.push({
            fieldId: "recruit-end-date",
            message: "모집 종료일은 시작일 이후로 선택해주세요."
        });
    }

    FormValidation.addRequiredError(form, "recruit-note", "비고를 입력해주세요.", errors);

    return errors;
}

function getRecruitRegisterRelatedFieldIds(fieldId) {
    if (fieldId === "recruit-start-date" || fieldId === "recruit-end-date") {
        return ["recruit-start-date", "recruit-end-date"];
    }

    return [fieldId];
}

function isRecruitRegisterField(form, target) {
    return target.matches("input, select, textarea") && form.contains(target);
}

function initCsBoardLinks() {
    delegate(document, "click", "[data-cs-link]", function (e, button) {
        const href = button.dataset.csLink;
        if (!href) return;

        e.preventDefault();
        window.location.href = href;
    });
}

/**
 * 상세보기(view/reply) 화면 등에 남아있는 단순 confirm형 삭제 버튼용
 * (실제 제출 로직 없이 confirm만 처리, 폼 제출은 각 화면에서 별도 처리)
 */
function initCsDeleteButtons() {
    delegate(document, "click", "[data-cs-delete]", function (e, button) {
        const message = button.dataset.confirmMessage || "삭제하시겠습니까?";
        if (window.confirm(message)) return;

        e.preventDefault();
    });
}

/**
 * 목록(list) 화면의 개별 삭제 버튼
 * data-cs-delete-single + data-checkbox-value="{boardNo}" 조합으로 사용
 * 클릭 시 해당 row의 checkbox만 체크하고 management-table-form을 제출
 */
function initCsSingleDeleteButtons() {
    delegate(document, "click", "[data-cs-delete-single]", function (e, button) {
        const message = button.dataset.confirmMessage || "삭제하시겠습니까?";
        if (!window.confirm(message)) return;

        const form = document.getElementById("management-table-form");
        if (!form) return;

        const checkboxName = form.dataset.checkboxName;
        const value = button.dataset.checkboxValue;
        if (!checkboxName || !value) return;

        const checkboxes = form.querySelectorAll(`input[name="${checkboxName}"]`);
        checkboxes.forEach(cb => {
            cb.checked = (cb.value === value);
        });

        form.submit();
    });
}

/**
 * 공지사항 유형 필터
 * notice/list.html에서
 * select id="notice-type-filter"
 * tr data-notice-row data-notice-type="고객서비스"
 * 이런 식으로 연결해서 사용
 */
function initNoticeTypeFilter() {
    const select = document.getElementById("notice-type-filter");
    if (!select) return;

    select.addEventListener("change", function () {
        const selectedType = select.value;
        const rows = document.querySelectorAll("[data-notice-row]");

        rows.forEach(function (row) {
            const rowType = row.dataset.noticeType;

            if (selectedType === "" || selectedType === rowType) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    });
}

/**
 * FAQ 1차 / 2차 유형 필터
 * faq/list.html에서
 * select id="faq-cate1-filter"
 * select id="faq-cate2-filter"
 * tr data-faq-row data-cate1="member" data-cate2="가입"
 * 이런 식으로 연결해서 사용
 */
function initFaqCategoryFilter() {
    const cate1Select = document.getElementById("faq-cate1-filter");
    const cate2Select = document.getElementById("faq-cate2-filter");

    if (!cate1Select || !cate2Select) return;

    const cate2Map = {
        member: ["가입", "탈퇴", "정보수정", "로그인"],
        coupon: ["쿠폰", "이벤트", "할인혜택"],
        order: ["주문", "결제", "영수증"],
        delivery: ["배송조회", "배송지연", "배송변경"],
        cancel: ["취소", "반품", "환불"],
        travel: ["여행", "숙박", "항공"],
        safe: ["안전거래", "피해신고", "주의사항"]
    };

    cate1Select.addEventListener("change", function () {
        const selectedCate1 = cate1Select.value;

        cate2Select.innerHTML = '<option value="">2차 선택</option>';

        if (selectedCate1 && cate2Map[selectedCate1]) {
            cate2Map[selectedCate1].forEach(function (cate2) {
                const option = document.createElement("option");
                option.value = cate2;
                option.textContent = cate2;
                cate2Select.appendChild(option);
            });
        }

        filterFaqRows();
    });

    cate2Select.addEventListener("change", filterFaqRows);

    function filterFaqRows() {
        const selectedCate1 = cate1Select.value;
        const selectedCate2 = cate2Select.value;
        const rows = document.querySelectorAll("[data-faq-row]");

        rows.forEach(function (row) {
            const rowCate1 = row.dataset.cate1;
            const rowCate2 = row.dataset.cate2;

            const matchCate1 = selectedCate1 === "" || selectedCate1 === rowCate1;
            const matchCate2 = selectedCate2 === "" || selectedCate2 === rowCate2;

            if (matchCate1 && matchCate2) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    }
}

/**
 * 문의하기 QNA 필터
 * qna/list.html에서
 * select id="qna-cate1-filter"
 * select id="qna-cate2-filter"
 * select id="qna-status-filter"
 * tr data-qna-row data-cate1="member" data-cate2="가입" data-status="검토중"
 * 이런 식으로 연결해서 사용
 */
function initQnaFilter() {
    const cate1Select = document.getElementById("qna-cate1-filter");
    const cate2Select = document.getElementById("qna-cate2-filter");
    const statusSelect = document.getElementById("qna-status-filter");

    if (!cate1Select && !cate2Select && !statusSelect) return;

    const filterHandler = function () {
        const selectedCate1 = cate1Select ? cate1Select.value : "";
        const selectedCate2 = cate2Select ? cate2Select.value : "";
        const selectedStatus = statusSelect ? statusSelect.value : "";

        const rows = document.querySelectorAll("[data-qna-row]");

        rows.forEach(function (row) {
            const rowCate1 = row.dataset.cate1;
            const rowCate2 = row.dataset.cate2;
            const rowStatus = row.dataset.status;

            const matchCate1 = selectedCate1 === "" || selectedCate1 === rowCate1;
            const matchCate2 = selectedCate2 === "" || selectedCate2 === rowCate2;
            const matchStatus = selectedStatus === "" || selectedStatus === rowStatus;

            if (matchCate1 && matchCate2 && matchStatus) {
                row.style.display = "";
            } else {
                row.style.display = "none";
            }
        });
    };

    if (cate1Select) cate1Select.addEventListener("change", filterHandler);
    if (cate2Select) cate2Select.addEventListener("change", filterHandler);
    if (statusSelect) statusSelect.addEventListener("change", filterHandler);
}