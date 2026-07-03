import { Validation } from '../../global/validation.js';
import { initModals } from '../../global/modal-form.js';
import { delegate } from '../../global/event-manager.js';
import { FormValidation } from '../global/form-validation.js';
import { ModalFormValidation } from '../global/modal-form-validation.js';
import { ManagementTableForm } from '../global/management-table-form.js';

const BANNER_TABLE_HEADERS = {
    mainTop: "메인상단 배너",
    mainSlider: "메인 슬라이더 배너",
    productDetailView: "상품 상세보기 배너",
    userLogin: "회원로그인 배너",
    myPage: "마이페이지 배너"
};

document.addEventListener("DOMContentLoaded", function () {
    initModals();
    ManagementTableForm.init();
    initBannerCategoryNav();
    initBannerRegisterValidation();
});

function initBannerCategoryNav() {
    const nav = document.querySelector(".banner-nav");
    if (!nav) return;

    const list = nav.querySelector("ul");
    const links = [...nav.querySelectorAll("[data-banner-category]")];
    if (!list || links.length === 0) return;

    const indicator = document.createElement("span");
    indicator.className = "banner-nav-indicator";
    indicator.setAttribute("aria-hidden", "true");
    list.prepend(indicator);
    nav.classList.add("is-ready");

    const currentCategory = getCurrentBannerCategory(links);
    setCurrentBannerCategory(links, currentCategory);
    updateBannerTableHeader(currentCategory);
    moveBannerNavIndicator(nav, indicator);

    delegate(nav, "click", "[data-banner-category]", function (e, link) {
        e.preventDefault();

        const category = link.dataset.bannerCategory;
        if (!category) return;
        if (link.closest("li")?.classList.contains("current")) return;

        setCurrentBannerCategory(links, category);
        updateBannerCategoryUrl(category);
        updateBannerTableHeader(category);
        moveBannerNavIndicator(nav, indicator);

        nav.dispatchEvent(new CustomEvent("banner-category:change", {
            bubbles: true,
            detail: { category }
        }));
    });

    window.addEventListener("resize", function () {
        moveBannerNavIndicator(nav, indicator);
    });

    window.addEventListener("popstate", function () {
        const category = getCurrentBannerCategory(links);
        setCurrentBannerCategory(links, category);
        updateBannerTableHeader(category);
        moveBannerNavIndicator(nav, indicator);
    });
}

function getCurrentBannerCategory(links) {
    const params = new URLSearchParams(window.location.search);
    const category = params.get("bannerCategory");
    if (links.some(link => link.dataset.bannerCategory === category)) {
        return category;
    }

    return links.find(link => link.closest("li")?.classList.contains("current"))?.dataset.bannerCategory
        || links[0].dataset.bannerCategory;
}

function setCurrentBannerCategory(links, category) {
    links.forEach(link => {
        const isCurrent = link.dataset.bannerCategory === category;
        link.closest("li")?.classList.toggle("current", isCurrent);
        link.setAttribute("aria-current", isCurrent ? "page" : "false");
    });
}

function updateBannerCategoryUrl(category) {
    const url = new URL(window.location.href);
    url.searchParams.set("bannerCategory", category);
    window.history.pushState({ bannerCategory: category }, "", url);
}

function updateBannerTableHeader(category) {
    const title = document.querySelector("#banner-management-section .section-title");
    if (!title) return;

    title.textContent = BANNER_TABLE_HEADERS[category] || BANNER_TABLE_HEADERS.mainTop;
}

function moveBannerNavIndicator(nav, indicator) {
    const currentItem = nav.querySelector("li.current");
    if (!currentItem) return;

    indicator.style.width = `${currentItem.offsetWidth}px`;
    indicator.style.transform = `translateX(${currentItem.offsetLeft}px)`;
}

function initBannerRegisterValidation() {
    const registerForm = document.getElementById("banner-register-form");
    if (!registerForm) return;

    ModalFormValidation.bind({
        form: registerForm,
        validate: validateBannerRegisterForm,
        isField: isBannerRegisterField,
        getRelatedFieldIds: getBannerRegisterRelatedFieldIds,
        validateOnOpen: true
    });
}

function getBannerRegisterRelatedFieldIds(fieldId) {
    if (fieldId === "banner-width" || fieldId === "banner-height") {
        return ["banner-width", "banner-height"];
    }

    if (fieldId === "banner-start-date" || fieldId === "banner-end-date") {
        return ["banner-start-date", "banner-end-date"];
    }

    if (fieldId === "banner-start-time" || fieldId === "banner-end-time") {
        return ["banner-start-time", "banner-end-time"];
    }

    return [fieldId];
}

function validateBannerRegisterForm(registerForm) {
    const errors = [];

    FormValidation.addRequiredError(registerForm, "banner-name", "배너명을 입력해주세요.", errors);
    validateBannerSize(registerForm, errors);
    validateBackgroundColor(registerForm, errors);
    validateBannerLink(registerForm, errors);
    FormValidation.addRequiredError(registerForm, "banner-position", "배너위치를 선택해주세요.", errors);
    validateBannerDateRange(registerForm, errors);
    validateBannerTimeRange(registerForm, errors);
    validateBannerFile(registerForm, errors);

    return errors;
}

function validateBannerSize(form, errors) {
    const widthField = form.querySelector("#banner-width");
    const heightField = form.querySelector("#banner-height");
    if (!widthField || !heightField) return;

    validateBannerSizeField(widthField, "배너 너비를 입력해주세요.", "배너 너비는 1 이상의 숫자로 입력해주세요.", errors);
    validateBannerSizeField(heightField, "배너 높이를 입력해주세요.", "배너 높이는 1 이상의 숫자로 입력해주세요.", errors);
}

function validateBannerSizeField(field, requiredMessage, numberMessage, errors) {
    if (!Validation.required(field.value).valid) {
        errors.push({ fieldId: field.id, message: requiredMessage });
        return;
    }

    if (!Validation.pattern(field.value, /^[1-9]\d*$/).valid) {
        errors.push({ fieldId: field.id, message: numberMessage });
    }
}

function validateBackgroundColor(form, errors) {
    const field = form.querySelector("#banner-background-color");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "배경색을 입력해주세요." });
        return;
    }

    if (!Validation.pattern(value, /^#([0-9a-f]{3}|[0-9a-f]{6})$/i).valid) {
        errors.push({ fieldId: field.id, message: "배경색은 #ffffff 형식으로 입력해주세요." });
    }
}

function validateBannerLink(form, errors) {
    const field = form.querySelector("#banner-link");
    if (!field) return;

    const value = field.value;
    if (!Validation.required(value).valid) {
        errors.push({ fieldId: field.id, message: "배너링크를 입력해주세요." });
        return;
    }

    if (!Validation.url(value).valid) {
        errors.push({ fieldId: field.id, message: "배너링크는 올바른 URL 형식으로 입력해주세요." });
    }
}

function validateBannerDateRange(form, errors) {
    const startDate = form.querySelector("#banner-start-date");
    const endDate = form.querySelector("#banner-end-date");
    if (!startDate || !endDate) return;

    if (!Validation.required(startDate.value).valid) {
        errors.push({ fieldId: startDate.id, message: "노출 시작일을 선택해주세요." });
    }

    if (!Validation.required(endDate.value).valid) {
        errors.push({ fieldId: endDate.id, message: "노출 종료일을 선택해주세요." });
    }

    if (startDate.value !== "" && endDate.value !== "" && !Validation.greaterThanOrEqual(startDate.value, endDate.value).valid) {
        errors.push({ fieldId: endDate.id, message: "노출 종료일은 시작일 이후로 선택해주세요." });
    }
}

function validateBannerTimeRange(form, errors) {
    const startTime = form.querySelector("#banner-start-time");
    const endTime = form.querySelector("#banner-end-time");
    if (!startTime || !endTime) return;

    if (!Validation.required(startTime.value).valid) {
        errors.push({ fieldId: startTime.id, message: "노출 시작시간을 선택해주세요." });
    }

    if (!Validation.required(endTime.value).valid) {
        errors.push({ fieldId: endTime.id, message: "노출 종료시간을 선택해주세요." });
    }

    if (startTime.value !== "" && endTime.value !== "" && !Validation.greaterThan(startTime.value, endTime.value).valid) {
        errors.push({ fieldId: endTime.id, message: "노출 종료시간은 시작시간 이후로 선택해주세요." });
    }
}

function validateBannerFile(form, errors) {
    const field = form.querySelector("#banner-file");
    if (!field) return;

    const file = field.files[0];
    if (!file) {
        errors.push({ fieldId: field.id, message: "배너이미지를 선택해주세요." });
        return;
    }

    if (!Validation.imageMimeType(file.type).valid) {
        errors.push({ fieldId: field.id, message: "배너이미지는 이미지 파일만 등록할 수 있습니다." });
    }
}

function isBannerRegisterField(form, target) {
    return target.matches("input, select") && form.contains(target);
}
