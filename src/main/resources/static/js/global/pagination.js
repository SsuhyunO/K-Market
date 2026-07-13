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

    pagination.addEventListener("click", event => {
        const link = event.target.closest("[data-page]");
        if (!link) return;

        event.preventDefault();

        moveToPage(link.dataset.page, true);
    });

    window.addEventListener("popstate", () => {
        moveToPage(getCurrentPage(paramName), false);
    });

    // 검색 조건이나 정렬 조건이 바뀌었을 때 외부에서 갱신 요청
    window.addEventListener("pagination:refresh", event => {
        const requestedPage = event.detail?.page;
        const page = requestedPage ?? getCurrentPage(paramName);

        // 외부에서 URL을 이미 변경했으므로 다시 pushState 하지 않음
        moveToPage(page, false);
    });

    moveToPage(getCurrentPage(paramName), false);

    async function moveToPage(page, pushUrl = true) {
        const safePage = toPositiveInteger(page, 1);

        try {
            const data = await fetchPage(safePage);
            const pageData = getPageData(data);

            renderPagination(pageData, {
                selector,
                paramName
            });

            if (updateUrl && pushUrl) {
                updatePageUrl(
                    pageData.page ?? safePage,
                    { paramName }
                );
            }
        } catch (error) {
            onError(error);
        }
    }
}

function renderPagination(pageData, options = {}) {
    const pagination = document.querySelector(options.selector);
    if (!pagination) return;

    pagination.innerHTML = "";

    if (!pageData || pageData.totalPage <= 0) return;

    if (pageData.hasPrev) {
        appendPageItem(
            pagination,
            "이전",
            pageData.page - 1,
            pageData.hasPrev);
    }


    for (let page = pageData.startPage; page <= pageData.lastPage; page++) {
        pagination.appendChild(
            page === pageData.page
                ? createCurrentPage(page)
                : createPageLink(page, page)
        );
    }

    if (pageData.hasNext) {
        appendPageItem(
            pagination,
            "다음",
            pageData.page + 1,
            pageData.hasNext
        );
    }
}

function appendPageItem(pagination, text, page) {
    pagination.appendChild(createPageLink(text, page));
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
    span.className = "active current";
    span.textContent = page;
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
