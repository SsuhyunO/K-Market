/*listener는 각 js파일간 이벤트를 구독하고 관련 로직을 처리하는 책임*/
import { registerSortableCategory } from './category-sorter.js';

document.addEventListener('DOMContentLoaded', function () {
    const categoryTree = document.querySelector('.category-tree');
    if (!categoryTree) return;

    bindCategoryChangeMessage(categoryTree);
    bindCategorySorter(categoryTree);
});

/**
 * 카테고리 변경 이벤트를 구독해 변경 메시지와 폼 액션 버튼 상태를 갱신한다.
 */
function bindCategoryChangeMessage(categoryTree) {
    const changeMessage = createChangeMessage(categoryTree);

    [
        'category:add',
        'category:update',
        'category:remove',
        'category:sort'
    ].forEach(type => {
        categoryTree.addEventListener(type, () => {
            if (categoryTree.dataset.categoryRestoring === 'true') return;

            changeMessage.textContent = '변경사항이 있습니다.';
            setActionState(categoryTree, true);
        });
    });
}

/**
 * 변경사항 유무에 따라 변경취소/수정하기 버튼의 disabled 상태를 갱신한다.
 */
function setActionState(categoryTree, hasChanges) {
    const form = categoryTree.closest('form');
    if (!form) return;

    const cancelButton = form.querySelector('.category-reset-button');
    if (cancelButton) cancelButton.disabled = !hasChanges;

    const submitButton = form.querySelector('.section-submit-button');
    if (submitButton) submitButton.disabled = !hasChanges;
}

/**
 * 새 카테고리 추가 이벤트를 sorter에 전달해 동적 항목도 정렬 가능하게 만든다.
 */
function bindCategorySorter(categoryTree) {
    categoryTree.addEventListener('category:add', event => {
        registerSortableCategory(event.detail);
    });
}

/**
 * 변경사항 안내 메시지를 폼 액션 영역 직전에 생성한다.
 */
function createChangeMessage(categoryTree) {
    const form = categoryTree.closest('form');
    const actions = form?.querySelector('.section-form-actions');
    const message = document.createElement('p');

    message.classList.add('field-error', 'category-change-message');

    if (actions) {
        actions.insertAdjacentElement('beforebegin', message);
        return message;
    }

    categoryTree.insertAdjacentElement('afterend', message);
    return message;
}
