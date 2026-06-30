import { bindCategoryModifier } from './category-modifier.js';
import { registerSortableCategory } from './category-sorter.js';

document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('.category-tree')?.closest('form');
    const categoryTree = form?.querySelector('.category-tree');
    if (!form || !categoryTree) return;

    let initialTreeHtml = categoryTree.innerHTML;
    const cancelButton = createCancelButton();

    form.querySelector('.section-form-actions')?.prepend(cancelButton);
    setActionState(form, false);

    cancelButton.addEventListener('click', () => {
        if (!confirm('변경사항을 취소하고 초기 상태로 되돌리시겠습니까?')) return;

        restoreInitialTree(form, categoryTree, initialTreeHtml);
    });

    form.addEventListener('submit', event => {
        event.preventDefault();

        const payload = serializeCategoryTree(categoryTree);
        console.log('category-management submit payload:', payload);

        initialTreeHtml = categoryTree.innerHTML;
        setActionState(form, false);
        clearChangeMessage(form);
    });
});

/**
 * 변경취소 버튼 DOM을 생성한다.
 * 버튼은 항상 렌더링되며 변경사항 유무에 따라 disabled 상태만 바뀐다.
 */
function createCancelButton() {
    const button = document.createElement('button');

    button.classList.add('section-cancel-button', 'category-reset-button');
    button.type = 'button';
    button.textContent = '변경취소';

    return button;
}

/**
 * 현재 트리 DOM을 마지막 기준 상태로 되돌린다.
 * innerHTML 교체로 사라진 수정/정렬 이벤트 리스너를 다시 바인딩한다.
 */
function restoreInitialTree(form, categoryTree, initialTreeHtml) {
    categoryTree.dataset.categoryRestoring = 'true';
    categoryTree.innerHTML = initialTreeHtml;

    bindCategoryModifier(form);
    categoryTree
        .querySelectorAll('.category-item')
        .forEach(item => registerSortableCategory({ item }));

    setActionState(form, false);
    clearChangeMessage(form);

    window.setTimeout(() => {
        delete categoryTree.dataset.categoryRestoring;
    }, 0);
}

/**
 * 변경 여부에 따라 폼 액션 버튼들의 활성 상태를 일괄 갱신한다.
 */
function setActionState(form, hasChanges) {
    toggleCancelButtonEnabled(form, hasChanges);
    toggleSubmitButton(form, hasChanges);
}

/**
 * 변경취소 버튼을 활성/비활성 상태로 전환한다.
 */
function toggleCancelButtonEnabled(form, isEnabled) {
    const cancelButton = form.querySelector('.category-reset-button');
    if (!cancelButton) return;

    cancelButton.disabled = !isEnabled;
}

/**
 * 수정하기 버튼을 활성/비활성 상태로 전환한다.
 */
function toggleSubmitButton(form, isEnabled) {
    const submitButton = form.querySelector('.section-submit-button');
    if (!submitButton) return;

    submitButton.disabled = !isEnabled;
}

/**
 * 변경사항 안내 메시지를 비운다.
 */
function clearChangeMessage(form) {
    const message = form.querySelector('.category-change-message');
    if (message) message.textContent = '';
}

/**
 * 현재 트리뷰 DOM을 제출용 카테고리 payload로 직렬화한다.
 * 백엔드 연동 전까지는 프론트 categoryId를 기준 식별자로 사용한다.
 */
function serializeCategoryTree(categoryTree) {
    return getRootItems(categoryTree).map((item, index) => ({
        id: item.dataset.categoryId ?? '',
        title: getCategoryTitle(item),
        depth: 1,
        order: index,
        children: getChildItems(item).map((child, childIndex) => ({
            id: child.dataset.categoryId ?? '',
            parentId: item.dataset.categoryId ?? '',
            title: getCategoryTitle(child),
            depth: 2,
            order: childIndex
        }))
    }));
}

/**
 * 트리 최상위의 1차 카테고리 li 목록을 반환한다.
 */
function getRootItems(categoryTree) {
    return [...categoryTree.querySelectorAll(':scope > .category-list > .category-item')];
}

/**
 * 특정 1차 카테고리에 속한 2차 카테고리 li 목록을 반환한다.
 */
function getChildItems(rootItem) {
    return [...rootItem.querySelectorAll(':scope > details > .category-list > .category-item')];
}

/**
 * 카테고리 row 안의 표시 제목을 추출한다.
 */
function getCategoryTitle(item) {
    return item.querySelector(':scope > .category-row > .category-title')?.textContent.trim() ?? '';
}
