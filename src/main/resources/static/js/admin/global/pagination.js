export function initPagination(options = {}) {
    const {
        selector = ".admin-pagination",
        fetchPage,
        getPageData = data => data,
        paramName = "page",
        updateUrl = true,
        onError = defaultErrorHandler
    } = options;

    if (typeof fetchPage !== "function") {
        console.error("initPagination: fetchPage 함수가 필요합니다.");
        return;
    }

    const pagination = document.querySelector(selector);
    if (!pagination) return;

    async function moveToPage(page, pushUrl = true) {
        const safePage = toPositiveInteger(page, 1);

        try {
            const data = await fetchPage(safePage);
            const pageData = getPageData(data);

            renderPagination(pageData, { selector });

            if (updateUrl && pushUrl) {
                updatePageUrl(pageData.page ?? safePage, { paramName });
            }
        } catch (error) {
            onError(error);
        }
    }

    pagination.addEventListener("click", event => {
        const link = event.target.closest("[data-page]");
        if (!link) return;

        event.preventDefault();

        moveToPage(link.dataset.page, true);
    });

    window.addEventListener("popstate", () => {
        moveToPage(getCurrentPage(paramName), false);
    });

    moveToPage(getCurrentPage(paramName), false);
}

function renderPagination(pageData, options = {}) {
    const pagination = document.querySelector(options.selector || ".admin-pagination");
    if (!pagination) return;

    pagination.innerHTML = "";

    if (!pageData || pageData.totalPage <= 1) return;

    appendPageItem(pagination, "이전", pageData.page - 1, pageData.page > 1);

    for (let page = pageData.startPage; page <= pageData.lastPage; page++) {
        pagination.appendChild(
            page === pageData.page
                ? createCurrentPage(page)
                : createPageLink(page, page)
        );
    }

    appendPageItem(
        pagination,
        "다음",
        pageData.page + 1,
        pageData.page < pageData.totalPage
    );
}

function appendPageItem(pagination, text, page, enabled) {
    pagination.appendChild(
        enabled ? createPageLink(text, page) : createDisabledPage(text)
    );
}

function createPageLink(text, page) {
    const a = document.createElement("a");
    a.href = `?page=${page}`;
    a.textContent = text;
    a.dataset.page = page;
    return a;
}

function createCurrentPage(page) {
    const span = document.createElement("span");
    span.className = "current";
    span.textContent = page;
    return span;
}

function createDisabledPage(text) {
    const span = document.createElement("span");
    span.className = "disabled";
    span.textContent = text;
    return span;
}

function getCurrentPage(paramName = "page") {
    const params = new URLSearchParams(window.location.search);
    return toPositiveInteger(params.get(paramName), 1);
}

function updatePageUrl(page, options = {}) {
    const safePage = toPositiveInteger(page, null);
    if (!safePage) return;

    const paramName = options.paramName || "page";
    const params = new URLSearchParams(window.location.search);

    params.set(paramName, safePage);

    window.history.pushState(
        null,
        "",
        `${window.location.pathname}?${params.toString()}`
    );
}

function toPositiveInteger(value, fallback = 1) {
    const number = Number.parseInt(value, 10);
    return Number.isInteger(number) && number > 0 ? number : fallback;
}

function defaultErrorHandler(error) {
    console.error(error);
}