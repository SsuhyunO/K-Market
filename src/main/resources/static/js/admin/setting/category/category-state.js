import { getCategoryId } from './category-id.js';

/**
 * 카테고리 DOM 상태를 WeakMap에 저장하고 조회/스냅샷/동기화 API를 제공한다.
 */
export function createCategoryStateStore(categoryTree) {
    const states = new WeakMap();

    categoryTree
        .querySelectorAll('.category-item')
        .forEach(item => syncCategoryState(states, item, categoryTree));

    return {
        get(item) {
            return states.get(item);
        },
        snapshot(item) {
            return getCategoryState(item, categoryTree);
        },
        sync(item) {
            return syncCategoryState(states, item, categoryTree);
        }
    };
}

/**
 * 특정 카테고리의 현재 DOM 상태를 계산해 상태 저장소에 반영한다.
 */
function syncCategoryState(states, item, categoryTree) {
    const state = getCategoryState(item, categoryTree);
    states.set(item, state);
    return state;
}

/**
 * 카테고리 DOM에서 이벤트 detail로 사용할 상태 객체를 만든다.
 */
function getCategoryState(item, categoryTree) {
    const row = item.querySelector(':scope > .category-row');
    const title = row?.querySelector(':scope > span.category-title')?.textContent ?? '';
    const isRoot = item.parentElement?.matches('.category-tree > .category-list') ?? false;
    const rootItem = isRoot
        ? item
        : item.closest('.category-tree > .category-list > .category-item');

    return {
        id: getCategoryId(item),
        item,
        row,
        title,
        depth: isRoot ? 1 : 2,
        isRoot,
        parentId: isRoot ? null : getCategoryId(rootItem),
        parentItem: isRoot ? null : rootItem,
        rootId: getCategoryId(rootItem),
        rootItem,
        categoryTree,
        open: item.querySelector(':scope > details')?.open ?? false
    };
}
