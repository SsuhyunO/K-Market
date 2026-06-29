/*observer는 값을 감시하고 이벤트만 발행하는 책임*/
import { createCategoryStateStore } from './category-state.js';

document.addEventListener('DOMContentLoaded', function () {
    const categoryTree = document.querySelector('.category-tree');
    if (!categoryTree) return;

    const categoryState = createCategoryStateStore(categoryTree);

    observeCategoryChanges(categoryTree, categoryState);
});

/**
 * 카테고리 트리의 DOM 변화를 감시하고 변화 종류에 맞는 이벤트 발행 흐름으로 위임한다.
 */
function observeCategoryChanges(categoryTree, categoryState) {
    const observer = new MutationObserver(mutations => {
        const childMutations = mutations.filter(mutation => mutation.type === 'childList');
        if (childMutations.length) {
            handleChildListMutations(categoryTree, categoryState, childMutations);
        }

        mutations.forEach(mutation => {
            if (mutation.type === 'childList') {
                handleTitleMutation(categoryTree, categoryState, mutation.target);
            }

            if (mutation.type === 'characterData') {
                handleTitleMutation(categoryTree, categoryState, mutation.target.parentElement);
            }

            if (mutation.type === 'attributes' && mutation.attributeName === 'open') {
                handleOpenMutation(categoryTree, categoryState, mutation.target);
            }
        });
    });

    observer.observe(categoryTree, {
        subtree: true,
        childList: true,
        characterData: true,
        attributes: true,
        attributeFilter: ['open']
    });
}

/**
 * 자식 노드 추가/삭제 mutation을 batch 단위로 분석한다.
 * 같은 노드가 removed/added에 모두 있으면 정렬로 인한 이동으로 보고 add/remove 이벤트를 막는다.
 */
function handleChildListMutations(categoryTree, categoryState, mutations) {
    const addedItems = collectTopCategoryItems(mutations.flatMap(mutation => [...mutation.addedNodes]));
    const removedItems = collectTopCategoryItems(mutations.flatMap(mutation => [...mutation.removedNodes]));
    const movedItems = new Set(addedItems.filter(item => removedItems.includes(item)));

    removedItems
        .filter(item => !movedItems.has(item))
        .forEach(item => dispatchCategoryEvent(categoryTree, 'category:remove', item, categoryState));

    addedItems
        .filter(item => !movedItems.has(item))
        .forEach(item => {
            categoryState.sync(item);
            dispatchCategoryEvent(categoryTree, 'category:add', item, categoryState);

            item
                .querySelectorAll('.category-item')
                .forEach(child => categoryState.sync(child));
        });

    movedItems.forEach(item => {
        categoryState.sync(item);
        item
            .querySelectorAll('.category-item')
            .forEach(child => categoryState.sync(child));
    });
}

/**
 * 카테고리 제목 텍스트 변경 여부를 비교하고 실제 변경 시 update 이벤트를 발행한다.
 */
function handleTitleMutation(categoryTree, categoryState, target) {
    const title = target?.closest?.('.category-row > span.category-title');
    const item = title?.closest('.category-item');
    if (!item || !categoryTree.contains(item)) return;

    const previousState = categoryState.get(item);
    const currentState = categoryState.snapshot(item);
    if (!previousState || previousState.title === currentState.title) return;

    categoryState.sync(item);
    dispatchCategoryEvent(categoryTree, 'category:update', item, categoryState, {
        previousTitle: previousState.title,
        title: currentState.title
    });
}

/**
 * 1차 카테고리 details open 상태 변경을 감지해 toggle 이벤트를 발행한다.
 */
function handleOpenMutation(categoryTree, categoryState, target) {
    const details = target;
    const item = details.parentElement;
    if (!item?.matches?.('.category-tree > .category-list > .category-item')) return;

    const previousState = categoryState.get(item);
    const currentState = categoryState.snapshot(item);
    if (!previousState || previousState.open === currentState.open) return;

    categoryState.sync(item);
    dispatchCategoryEvent(categoryTree, 'category:toggle', item, categoryState, {
        open: currentState.open
    });
}

/**
 * mutation node 목록에서 중첩 중복을 제거한 최상위 category-item만 수집한다.
 */
function collectTopCategoryItems(nodes) {
    const items = [];

    nodes.forEach(node => {
        if (!(node instanceof Element)) return;

        if (node.matches('.category-item')) items.push(node);
        node.querySelectorAll('.category-item').forEach(item => items.push(item));
    });

    return items.filter(item => !items.some(other => other !== item && other.contains(item)));
}

/**
 * 현재 카테고리 상태와 추가 detail을 합쳐 category:* 커스텀 이벤트를 발행한다.
 */
function dispatchCategoryEvent(categoryTree, type, item, categoryState, detail = {}) {
    const state = categoryState.get(item) ?? categoryState.snapshot(item);

    categoryTree.dispatchEvent(new CustomEvent(type, {
        bubbles: true,
        detail: {
            ...state,
            ...detail
        }
    }));
}
