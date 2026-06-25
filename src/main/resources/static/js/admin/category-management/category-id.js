let fallbackSequence = 0;

document.addEventListener('DOMContentLoaded', function () {
    const categoryTree = document.querySelector('.category-tree');
    if (!categoryTree) return;

    initCategoryIds(categoryTree);
    observeCategoryIds(categoryTree);
});

/**
 * 카테고리 DOM에 프론트 전용 식별자가 없으면 새로 부여하고 반환한다.
 */
export function ensureCategoryId(item) {
    if (!item?.matches?.('.category-item')) return '';
    if (item.dataset.categoryId) return item.dataset.categoryId;

    item.dataset.categoryId = createCategoryId();
    return item.dataset.categoryId;
}

/**
 * 카테고리 DOM의 프론트 전용 식별자를 조회한다.
 */
export function getCategoryId(item) {
    return item?.dataset?.categoryId ?? '';
}

/**
 * 최초 렌더링된 모든 카테고리에 프론트 전용 식별자를 보장한다.
 */
function initCategoryIds(categoryTree) {
    categoryTree
        .querySelectorAll('.category-item')
        .forEach(ensureCategoryId);
}

/**
 * 동적으로 추가되는 카테고리를 감지해 프론트 전용 식별자를 자동 부여한다.
 */
function observeCategoryIds(categoryTree) {
    const observer = new MutationObserver(mutations => {
        mutations.forEach(mutation => {
            mutation.addedNodes.forEach(node => {
                if (!(node instanceof Element)) return;

                if (node.matches('.category-item')) ensureCategoryId(node);
                node.querySelectorAll('.category-item').forEach(ensureCategoryId);
            });
        });
    });

    observer.observe(categoryTree, {
        subtree: true,
        childList: true
    });
}

/**
 * 브라우저 UUID를 우선 사용하고, 불가능한 경우 시간/순번 기반 fallback id를 만든다.
 */
function createCategoryId() {
    if (globalThis.crypto?.randomUUID) {
        return `front-cat-${globalThis.crypto.randomUUID()}`;
    }

    fallbackSequence += 1;
    return `front-cat-${Date.now()}-${fallbackSequence}-${Math.random().toString(36).slice(2)}`;
}
