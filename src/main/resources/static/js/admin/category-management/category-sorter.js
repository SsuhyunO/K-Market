const SORT_ANIMATION_MS = 160;
const DRAG_END_ANIMATION_MS = 180;

const sortableCategories = new WeakSet();
let pendingDrag = null;
let draggedCategory = null;
let dropGuideItem = null;
let suppressNextClick = false;

document.addEventListener('DOMContentLoaded', function () {
    const categoryTree = document.querySelector('.category-tree');
    if (!categoryTree) return;

    categoryTree
        .querySelectorAll('.category-item')
        .forEach(item => registerSortableCategory({ item }));
});

document.addEventListener('click', event => {
    if (!suppressNextClick) return;

    event.preventDefault();
    event.stopPropagation();
    suppressNextClick = false;
}, true);

/**
 * 카테고리 항목을 정렬 대상으로 등록하고 pointer 기반 드래그 시작 이벤트를 연결한다.
 */
export function registerSortableCategory(category) {
    const item = category?.item;
    if (!item || sortableCategories.has(item)) return;

    sortableCategories.add(item);
    item.draggable = false;
    item.addEventListener('pointerdown', handlePointerDown);
}

/**
 * 드래그 후보 시작점을 기록한다.
 * 버튼/input 클릭과 다른 depth에서 버블링된 pointerdown은 정렬 시작으로 취급하지 않는다.
 */
function handlePointerDown(event) {
    const item = event.currentTarget;

    if (event.button !== 0) return;
    if (event.target.closest('.category-row')?.parentElement !== item) return;
    if (event.target.closest('.category-actions, input, button')) return;

    pendingDrag = {
        item,
        startX: event.clientX,
        startY: event.clientY,
        pointerId: event.pointerId
    };

    item.setPointerCapture?.(event.pointerId);
    document.addEventListener('pointermove', handlePointerMove);
    document.addEventListener('pointerup', handlePointerUp);
    document.addEventListener('pointercancel', handlePointerCancel);
}

/**
 * pointer 이동 거리가 임계값을 넘으면 실제 드래그를 시작하고, 이후 본체 이동과 실시간 재정렬을 처리한다.
 */
function handlePointerMove(event) {
    if (!pendingDrag && !draggedCategory) return;

    if (!draggedCategory) {
        const distanceX = Math.abs(event.clientX - pendingDrag.startX);
        const distanceY = Math.abs(event.clientY - pendingDrag.startY);
        if (distanceX < 4 && distanceY < 4) return;

        startDrag(event);
    }

    event.preventDefault();
    updateDraggedItem(event);
    reorderByPointer(event);
}

/**
 * pointer release 시 드래그를 종료하고 변경된 순서가 있으면 sort 이벤트 발행 흐름을 실행한다.
 */
function handlePointerUp(event) {
    if (draggedCategory) {
        event.preventDefault();
        finishDrag();
        suppressNextClick = true;
    }

    cleanupPointerEvents();
    pendingDrag = null;
}

/**
 * 브라우저가 pointer 동작을 취소한 경우 이벤트 발행 없이 드래그 상태를 정리한다.
 */
function handlePointerCancel() {
    if (draggedCategory) finishDrag(false);

    cleanupPointerEvents();
    pendingDrag = null;
}

/**
 * 드래그 상태 객체를 만들고 텍스트 선택 방지/본체 이동 스타일을 적용한다.
 */
function startDrag(event) {
    const item = pendingDrag.item;
    const categoryTree = item.closest('.category-tree');

    draggedCategory = {
        item,
        categoryTree,
        parentList: item.parentElement,
        initialIndex: getCategoryIndex(item),
        dragOffsetY: pendingDrag.startY - getDragRect(item).top,
        dragBounds: getDragBounds(item),
        inlineStyle: item.getAttribute('style') ?? ''
    };

    clearTextSelection();
    categoryTree?.classList.add('category-sorting');
    startDirectDrag(item, event);
    item.classList.add('category-dragging');
}

/**
 * 드래그 종료 시 즉시 sort 이벤트를 발행한 뒤, 본체가 정렬 위치로 돌아가는 애니메이션을 실행한다.
 */
async function finishDrag(shouldDispatchSort = true) {
    const {item, parentList, initialIndex} = draggedCategory;
    const currentIndex = getCategoryIndex(item);

    clearDropGuide();

    if (shouldDispatchSort && initialIndex !== currentIndex) {
        dispatchSortEvent(item, parentList, initialIndex, currentIndex);
    }

    await finishDirectDrag();
    draggedCategory = null;
}

/**
 * 문서에 임시로 연결한 pointer 이벤트와 pointer capture를 해제한다.
 */
function cleanupPointerEvents() {
    pendingDrag?.item.releasePointerCapture?.(pendingDrag.pointerId);
    document.removeEventListener('pointermove', handlePointerMove);
    document.removeEventListener('pointerup', handlePointerUp);
    document.removeEventListener('pointercancel', handlePointerCancel);
}

/**
 * 현재 pointer Y 좌표 아래의 같은 depth 형제 카테고리를 찾고 재정렬을 시도한다.
 */
function reorderByPointer(event) {
    const targetItem = getTargetItem(event.clientY);
    if (!targetItem) {
        clearDropGuide();
        return;
    }

    showDropGuide(targetItem);
    reorderCategory(targetItem, event.clientY);
}

/**
 * 현재 pointer Y 좌표가 걸쳐 있는 같은 부모 리스트의 형제 카테고리를 반환한다.
 */
function getTargetItem(clientY) {
    const {item, parentList} = draggedCategory;
    const siblings = getCategoryItems(parentList)
        .filter(sibling => sibling !== item);

    return siblings.find(sibling => {
        const rect = getDragRect(sibling);
        return clientY >= rect.top && clientY <= rect.bottom;
    }) ?? null;
}

/**
 * pointer가 target의 상/하단 중 어디에 있는지 판단해 DOM 순서를 실시간으로 변경한다.
 */
function reorderCategory(targetItem, clientY) {
    const {item, parentList} = draggedCategory;
    const referenceItem = shouldInsertAfter(clientY, targetItem)
        ? targetItem.nextElementSibling
        : targetItem;

    if (referenceItem === item || item.nextElementSibling === referenceItem) return;

    animateCategoryMove(parentList, () => {
        parentList.insertBefore(item, referenceItem);
    });
    updateDraggedItem({ clientY });
}

/**
 * pointer가 대상 카테고리 row의 세로 중앙보다 아래에 있는지 판정한다.
 */
function shouldInsertAfter(clientY, targetItem) {
    const rect = getDragRect(targetItem);
    return clientY > rect.top + rect.height / 2;
}

/**
 * DOM 순서 변경 전후의 위치 차이를 이용해 형제 카테고리 이동을 FLIP 애니메이션으로 표현한다.
 */
function animateCategoryMove(parentList, moveCategory) {
    const items = getAnimatedCategoryItems(parentList);
    const previousRects = new Map(items.map(item => [item, item.getBoundingClientRect()]));

    moveCategory();

    getAnimatedCategoryItems(parentList).forEach(item => {
        const previousRect = previousRects.get(item);
        if (!previousRect) return;

        const currentRect = item.getBoundingClientRect();
        const deltaX = previousRect.left - currentRect.left;
        const deltaY = previousRect.top - currentRect.top;
        if (!deltaX && !deltaY) return;

        item.style.transition = 'none';
        item.style.transform = `translate(${deltaX}px, ${deltaY}px)`;

        requestAnimationFrame(() => {
            item.style.transition = `transform ${SORT_ANIMATION_MS}ms ease`;
            item.style.transform = '';
        });
    });
}

/**
 * 드래그 본체의 transition을 끄고 현재 pointer 위치에 맞춰 즉시 이동시킨다.
 */
function startDirectDrag(item, event) {
    item.style.transition = 'none';
    updateDraggedItem(event);
}

/**
 * 드래그 중인 본체를 같은 depth의 천장/바닥 범위 안에서 y축으로 이동시킨다.
 */
function updateDraggedItem(event) {
    if (!draggedCategory) return;

    const nextTop = event.clientY - draggedCategory.dragOffsetY;
    const currentTop = getNaturalDragRect(draggedCategory.item).top;

    draggedCategory.item.style.transform = `translateY(${clampDragTop(nextTop) - currentTop}px)`;
}

/**
 * 드래그 본체가 최종 DOM 위치로 부드럽게 돌아가도록 transform을 해제하고 스타일을 복원한다.
 */
function finishDirectDrag() {
    const {item, categoryTree, inlineStyle} = draggedCategory;

    return new Promise(resolve => {
        item.style.transition = `transform ${DRAG_END_ANIMATION_MS}ms ease`;
        item.style.transform = '';

        window.setTimeout(() => {
            item.classList.remove('category-dragging');
            if (inlineStyle) {
                item.setAttribute('style', inlineStyle);
            } else {
                item.removeAttribute('style');
            }
            categoryTree?.classList.remove('category-sorting');
            resolve();
        }, DRAG_END_ANIMATION_MS);
    });
}

/**
 * 드래그 중 파란 텍스트 선택 박스가 남지 않도록 현재 문서 선택을 제거한다.
 */
function clearTextSelection() {
    window.getSelection?.()?.removeAllRanges();
}

/**
 * 같은 depth 안에서 드래그 본체가 이동할 수 있는 y축 최소/최대 좌표를 계산한다.
 */
function getDragBounds(item) {
    const items = getCategoryItems(item.parentElement);
    const firstRect = getDragRect(items[0]);
    const lastRect = getDragRect(items[items.length - 1]);
    const itemRect = getDragRect(item);

    return {
        minTop: firstRect.top,
        maxTop: lastRect.bottom - itemRect.height
    };
}

/**
 * 카테고리 item의 드래그 기준 row 영역을 반환한다.
 */
function getDragRect(item) {
    return item.querySelector(':scope > .category-row')?.getBoundingClientRect() ??
        item.getBoundingClientRect();
}

/**
 * 현재 transform을 잠시 제거해 드래그 본체의 자연 DOM 위치를 측정한다.
 */
function getNaturalDragRect(item) {
    const previousTransition = item.style.transition;
    const previousTransform = item.style.transform;

    item.style.transition = 'none';
    item.style.transform = '';

    const rect = getDragRect(item);

    item.style.transition = previousTransition;
    item.style.transform = previousTransform;

    return rect;
}

/**
 * 요청된 top 값을 같은 depth의 이동 가능 범위 안으로 제한한다.
 */
function clampDragTop(top) {
    const {minTop, maxTop} = draggedCategory.dragBounds;

    return Math.min(Math.max(top, minTop), maxTop);
}

/**
 * 정렬 대상 카테고리에 파란 밑줄 가이드를 표시한다.
 */
function showDropGuide(item) {
    if (dropGuideItem === item) return;

    clearDropGuide();
    dropGuideItem = item;
    dropGuideItem.classList.add('category-drop-guide');
}

/**
 * 현재 표시 중인 정렬 위치 가이드를 제거한다.
 */
function clearDropGuide() {
    dropGuideItem?.classList.remove('category-drop-guide');
    dropGuideItem = null;
}

/**
 * 같은 부모 리스트 안에서 카테고리 item의 현재 순서를 반환한다.
 */
function getCategoryIndex(item) {
    return getCategoryItems(item.parentElement).indexOf(item);
}

/**
 * 특정 category-list의 직계 category-item 목록을 배열로 반환한다.
 */
function getCategoryItems(parentList) {
    return [...parentList.children].filter(child => child.matches('.category-item'));
}

/**
 * FLIP 애니메이션 대상에서 현재 드래그 중인 본체를 제외한 형제 목록을 반환한다.
 */
function getAnimatedCategoryItems(parentList) {
    return getCategoryItems(parentList)
        .filter(item => item !== draggedCategory?.item);
}

/**
 * 드래그 종료 후 실제 순서가 바뀐 경우 category:sort 이벤트를 발행한다.
 */
function dispatchSortEvent(item, parentList, previousIndex, currentIndex) {
    const categoryTree = item.closest('.category-tree');
    if (!categoryTree) return;

    categoryTree.dispatchEvent(new CustomEvent('category:sort', {
        bubbles: true,
        detail: {
            id: item.dataset.categoryId ?? '',
            item,
            parentList,
            previousIndex,
            currentIndex
        }
    }));
}
