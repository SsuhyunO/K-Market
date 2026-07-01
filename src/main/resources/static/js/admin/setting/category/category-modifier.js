import { Validation } from '../../../global/validation.js';
import { ensureCategoryId } from './category-id.js';

document.addEventListener('DOMContentLoaded', function () {
    bindCategoryModifier();
});

/**
 * 카테고리 트리 안의 수정/삭제/추가 UI 이벤트를 바인딩한다.
 * reset으로 DOM이 교체된 뒤에도 같은 함수로 새 DOM에 다시 연결한다.
 */
export function bindCategoryModifier(root = document) {
    let activeModify = null;

    /* ============================= 카테고리 삭제, 수정 ================================ */
    root
        .querySelectorAll('.category-tree > .category-list > .category-item > .category-row')
        .forEach(bindRootCategoryToggle);

    root.querySelectorAll('.category-actions').forEach(bindCategoryActions);
    root.querySelectorAll('.category-add-button').forEach(bindCategoryAddButton);

    /**
     * 1차 카테고리 row 클릭으로 details open 상태를 토글하도록 연결한다.
     */
    function bindRootCategoryToggle(row) {
        const item = row.parentElement;
        const details = item.querySelector(':scope > details');
        const toggle = row.querySelector('.category-toggle');

        if (!details || !toggle) return;

        syncCategoryOpenState(item, details, toggle);

        row.addEventListener('click', e => {
            if (e.target.closest('.category-actions')) return;
            if (activeModify?.input.contains(e.target)) return;

            details.open = !details.open;
            syncCategoryOpenState(item, details, toggle);
        });
    }

    /**
     * 카테고리 row의 수정/삭제 버튼 이벤트를 연결한다.
     */
    function bindCategoryActions(cateActions) {
        const row = cateActions.closest('.category-row');
        const item = cateActions.closest('.category-item');

        cateActions
            .querySelector('.category-delete-button')
            .addEventListener(
                'click',
                e => processClickButtonDefault(
                    e,
                    () => removeAction(item)));

        cateActions
            .querySelector('.category-modify-button')
            .addEventListener(
                'click',
                e => processClickButtonDefault(
                    e,
                    () => modifyAction(row, cateActions)));
    }

    /**
     * 1차/2차 카테고리 추가 버튼을 구분해 알맞은 추가 로직을 실행한다.
     */
    function bindCategoryAddButton(addButton) {
        addButton.addEventListener('click', e => processClickButtonDefault(e, () => {
            if (!cancelModifyAction()) return;

            if (addButton.classList.contains('category-add-root-button')) {
                addRootCategory(addButton);
                return;
            }

            addSubCategory(addButton);
        }));
    }

    document.addEventListener('click', e => {
        if (!activeModify) return;
        if (activeModify.row.contains(e.target)) return;

        cancelModifyAction();
    });

    /**
     * details open 상태를 category-open 클래스와 aria-expanded 속성에 동기화한다.
     */
    function syncCategoryOpenState(item, details, toggle) {
        item.classList.toggle('category-open', details.open);
        toggle.setAttribute('aria-expanded', details.open);
    }

    /**
     * 현재 수정 중인 입력을 정리한 뒤 확인을 받고 카테고리 DOM을 삭제한다.
     */
    function removeAction(item) {
        if (!cancelModifyAction()) return;
        if (!confirm('카테고리를 삭제하시겠습니까?')) return;

        item.remove();
    }

    /**
     * 카테고리 제목을 inline input으로 전환하고 완료/취소 버튼을 임시로 표시한다.
     */
    function modifyAction(row, cateActions) {
        if (!cancelModifyAction()) return;

        const title = row.querySelector(':scope > .category-title');
        if (!title) return;

        const input = document.createElement('input');
        const inputWrap = document.createElement('div');
        const errorText = document.createElement('p');
        const confirmButton = document.createElement('button');
        const cancelButton = document.createElement('button');
        const hiddenButtons = [...cateActions.children];
        const hiddenButtonDisplays = hiddenButtons.map(button => button.style.display);

        hiddenButtons.forEach(button => button.style.display = 'none');

        confirmButton.classList.add('category-modify-button');
        confirmButton.innerText = '완료';
        confirmButton.type = 'button';
        confirmButton.addEventListener(
            'click', e =>
                processClickButtonDefault(
                    e,
                    () => confirmModifyAction(e, title, input)));

        cancelButton.classList.add('category-delete-button');
        cancelButton.innerText = '취소';
        cancelButton.type = 'button';
        cancelButton.addEventListener(
            'click',
            e =>
                processClickButtonDefault(e, () => cancelModifyAction()));

        inputWrap.classList.add('category-title');
        errorText.classList.add('field-error');

        inputWrap.style.width = '100%';
        input.style.width = '100%';

        input.value = title.textContent;
        input.type = 'text';
        input.style.resize = 'none';
        input.style.paddingLeft = '5px';
        input.addEventListener('keydown', e => {
            if (e.key === 'Enter') {
                processClickButtonDefault(e, () => confirmModifyAction(e, title, input));
            }

            if (e.key === 'Escape') {
                processClickButtonDefault(e, () => cancelModifyAction());
            }
        });
        input.addEventListener('input', () => clearModifyError(input, errorText));

        cateActions.appendChild(confirmButton);
        cateActions.appendChild(cancelButton);
        inputWrap.appendChild(input);
        inputWrap.appendChild(errorText);
        title.parentElement.insertBefore(inputWrap, title);

        activeModify = {
            row,
            title,
            input,
            inputWrap,
            errorText,
            confirmButton,
            cancelButton,
            hiddenButtons,
            hiddenButtonDisplays,
            titleDisplay: title.style.display
        };

        title.style.display = 'none';
        input.focus();
        input.select();
    }

    /**
     * 수정 입력값을 검증하고 확인 후 제목 DOM에 반영한다.
     */
    function confirmModifyAction(e, title, input) {
        if (!Validation.required(input.value).valid) {
            renderModifyError(input, activeModify?.errorText, '내용을 입력해주세요.');
            return;
        }

        if (title.innerText !== input.value &&
            !confirm(`변경 사항을 적용하시겠습니까?\n${title.innerText} -> ${input.value}`)) return;

        title.innerText = input.value;
        closeModifyAction();
    }

    /**
     * 활성 수정 입력이 있으면 변경 여부를 확인하고 수정 모드를 종료한다.
     */
    function cancelModifyAction() {
        if (!activeModify) return true;

        const {title, input} = activeModify;
        const isChanged = input.value !== title.innerText;

        if (isChanged && !confirm(`${title.innerText} -> ${input.value}\n변경을 취소하시겠습니까?`)) return false;

        closeModifyAction();
        return true;
    }

    /**
     * 수정 모드에서 생성한 임시 input/버튼을 제거하고 원래 row 버튼 상태를 복원한다.
     */
    function closeModifyAction() {
        if (!activeModify) return;

        const {
            title,
            input,
            inputWrap,
            confirmButton,
            cancelButton,
            hiddenButtons,
            hiddenButtonDisplays,
            titleDisplay
        } = activeModify;

        inputWrap.remove();
        confirmButton.remove();
        cancelButton.remove();
        hiddenButtons.forEach((button, index) => button.style.display = hiddenButtonDisplays[index]);
        title.style.display = titleDisplay;

        activeModify = null;
    }

    /**
     * 수정 input에 검증 실패 스타일과 메시지를 표시한다.
     */
    function renderModifyError(input, errorText, message) {
        input.classList.add('is-invalid');
        if (errorText) errorText.textContent = message;
        input.focus();
    }

    /**
     * 수정 input의 검증 실패 스타일과 메시지를 제거한다.
     */
    function clearModifyError(input, errorText) {
        input.classList.remove('is-invalid');
        if (errorText) errorText.textContent = '';
    }

    /* ============================= 카테고리 삭제, 수정 ================================ */
    /* ============================= 카테고리 추가(동적 추가 이벤트이므로 별도로 코드 스코프 분리) ================================ */

    /**
     * 트리 최상위 목록에 새 1차 카테고리를 추가한다.
     */
    function addRootCategory(addButton) {
        const rootList = addButton.closest('.category-tree')?.querySelector(':scope > .category-list');
        if (!rootList) return;

        const item = createRootCategoryItem('새로운 카테고리');
        rootList.appendChild(item);
    }

    /**
     * 특정 1차 카테고리의 details 안에 새 2차 카테고리를 추가하고 details를 연다.
     */
    function addSubCategory(addButton) {
        const details = addButton.closest('details');
        if (!details) return;

        const item = createSubCategoryItem('새로운 카테고리');
        const list = ensureSubCategoryList(details, addButton);

        list.appendChild(item);
        details.open = true;

        const rootItem = details.closest('.category-tree > .category-list > .category-item');
        const rootToggle = rootItem?.querySelector(':scope > .category-row .category-toggle');
        if (rootItem && rootToggle) syncCategoryOpenState(rootItem, details, rootToggle);
    }

    /**
     * 1차 카테고리 li DOM을 생성하고 내부 row/details/2차 추가 버튼 이벤트를 바인딩한다.
     */
    function createRootCategoryItem(titleText) {
        const item = document.createElement('li');
        const row = createCategoryRow(titleText, true);
        const details = document.createElement('details');
        const summary = document.createElement('summary');
        const list = document.createElement('ul');
        const addButton = createAddButton('+ 2차 카테고리 추가');

        item.classList.add('category-item');
        ensureCategoryId(item);
        summary.classList.add('category-hidden-summary');
        summary.textContent = `${titleText} 하위 카테고리`;
        list.classList.add('category-list');

        details.appendChild(summary);
        details.appendChild(list);
        details.appendChild(addButton);
        item.appendChild(row);
        item.appendChild(details);

        bindRootCategoryToggle(row);
        bindCategoryActions(row.querySelector('.category-actions'));
        bindCategoryAddButton(addButton);

        return item;
    }

    /**
     * 2차 카테고리 li DOM을 생성하고 수정/삭제 이벤트를 바인딩한다.
     */
    function createSubCategoryItem(titleText) {
        const item = document.createElement('li');
        const row = createCategoryRow(titleText, false);

        item.classList.add('category-item');
        ensureCategoryId(item);
        item.appendChild(row);
        bindCategoryActions(row.querySelector('.category-actions'));

        return item;
    }

    /**
     * 카테고리 제목, 선택적 toggle, 수정/삭제 버튼을 포함한 row DOM을 생성한다.
     */
    function createCategoryRow(titleText, withToggle) {
        const row = document.createElement('div');
        const title = document.createElement('span');
        const actions = document.createElement('div');
        const modifyButton = document.createElement('button');
        const deleteButton = document.createElement('button');

        row.classList.add('category-row');

        if (withToggle) {
            const toggle = document.createElement('button');
            toggle.classList.add('category-toggle');
            toggle.type = 'button';
            toggle.setAttribute('aria-label', '하위 카테고리 열기/닫기');
            row.appendChild(toggle);
        }

        title.classList.add('category-title');
        title.textContent = titleText;

        actions.classList.add('category-actions');

        modifyButton.classList.add('category-modify-button');
        modifyButton.type = 'button';
        modifyButton.textContent = '수정';

        deleteButton.classList.add('category-delete-button');
        deleteButton.type = 'button';
        deleteButton.textContent = '삭제';

        actions.appendChild(modifyButton);
        actions.appendChild(deleteButton);
        row.appendChild(title);
        row.appendChild(actions);

        return row;
    }

    /**
     * 카테고리 추가 버튼 DOM을 생성한다.
     */
    function createAddButton(text) {
        const button = document.createElement('button');
        button.classList.add('category-add-button');
        button.type = 'button';
        button.textContent = text;
        return button;
    }

    /**
     * 2차 카테고리 ul이 없으면 생성해 추가 버튼 앞에 삽입하고 반환한다.
     */
    function ensureSubCategoryList(details, addButton) {
        let list = details.querySelector(':scope > .category-list');
        if (list) return list;

        list = document.createElement('ul');
        list.classList.add('category-list');
        details.insertBefore(list, addButton);
        return list;
    }

    /**
     * 버튼 클릭의 기본 동작과 버블링을 막은 뒤 전달된 작업을 실행한다.
     */
    function processClickButtonDefault(event, nextProcess) {
        event.preventDefault();
        event.stopPropagation();
        nextProcess();
    }
}
