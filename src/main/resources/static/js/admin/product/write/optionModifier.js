document.addEventListener("DOMContentLoaded", function () {
    const addButton = document.getElementById("option-add-button");
    const firstOptionRow = document.querySelector("[data-option-row]");
    const stockInput = document.getElementById("stock");
    const optionStockBody = document.querySelector("[data-option-stock-body]");
    const optionItemFields = document.querySelector("[data-option-item-fields]");

    if (!addButton || !firstOptionRow) return;

    const optionRowsContainer = firstOptionRow.parentElement;

    optionRowsContainer.querySelectorAll("[data-option-row]").forEach(ensureOptionRowKey);
    initializeEditOptions(optionRowsContainer);

    if (optionStockBody) {
        optionStockBody.addEventListener("input", function (event) {
            if (!event.target.matches("[data-option-stock-input]")) return;

            updateTotalStock(stockInput, optionStockBody);
            dispatchOptionChange(optionRowsContainer);
        });

        optionStockBody.addEventListener("change", function (event) {
            if (!event.target.matches("[data-option-status-input]")) return;
            dispatchOptionChange(optionRowsContainer);
        });
    }

    addButton.addEventListener("click", function () {
        const nextIndex = document.querySelectorAll("[data-option-row]").length + 1;
        optionRowsContainer.appendChild(createOptionRow(nextIndex));
        updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
    });

    optionRowsContainer.addEventListener("focusout", function (event) {
        if (!event.target.matches("[data-option-name-input], [data-option-item-value]")) return;
        updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
    });

    optionRowsContainer.addEventListener("keydown", function (event) {
        if (!event.target.matches("[data-option-item-draft]") || event.key !== "Enter") return;

        event.preventDefault();
        if (addOptionItem(event.target.closest("[data-option-row]"))) {
            updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
        }
    });

    optionRowsContainer.addEventListener("click", function (event) {
        const openItemButton = event.target.closest("[data-option-item-open]");
        if (openItemButton) {
            openOptionItemDraft(openItemButton.closest("[data-option-row]"));
            return;
        }

        const addItemButton = event.target.closest("[data-option-item-add]");
        if (addItemButton) {
            if (addOptionItem(addItemButton.closest("[data-option-row]"))) {
                updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
            }
            return;
        }

        const cancelItemButton = event.target.closest("[data-option-item-cancel]");
        if (cancelItemButton) {
            closeOptionItemDraft(cancelItemButton.closest("[data-option-row]"));
            return;
        }

        const deleteItemButton = event.target.closest("[data-option-item-delete]");
        if (deleteItemButton) {
            deleteItemButton.closest("[data-option-item]")?.remove();
            updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
            return;
        }

        const deleteButton = event.target.closest("[data-option-delete]");
        if (!deleteButton) return;

        const optionRow = deleteButton.closest("[data-option-row]");
        if (!optionRow) return;

        optionRow.remove();
        updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
    });

    updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput);
});

function createOptionRow(index, group = null) {
    const row = document.createElement("tr");
    const optionHeader = document.createElement("th");
    const optionCell = document.createElement("td");
    const valueHeader = document.createElement("th");
    const valueCell = document.createElement("td");
    const actionCell = document.createElement("td");
    const optionLabel = document.createElement("label");
    const optionInput = document.createElement("input");
    const valueLabel = document.createElement("label");
    const itemEditor = createOptionItemEditor(index);
    const deleteButton = document.createElement("button");

    row.dataset.optionRow = "";
    ensureOptionRowKey(row);
    if (group?.clientKey) {
        row.dataset.optionGroupKey = group.clientKey;
    }

    optionLabel.dataset.optionLabel = "";
    optionLabel.htmlFor = `option${index}`;
    optionLabel.textContent = `옵션${index}`;
    optionHeader.append(optionLabel);

    optionInput.id = `option${index}`;
    optionInput.type = "text";
    optionInput.maxLength = 50;
    optionInput.value = group?.name || "";
    optionInput.dataset.optionNameInput = "";
    optionCell.append(optionInput);

    valueLabel.htmlFor = `option${index}-value`;
    valueLabel.textContent = `옵션${index} 항목`;
    valueHeader.append(valueLabel);
    if (group?.items?.length > 0) {
        const itemList = itemEditor.querySelector("[data-option-item-list]");
        group.items.forEach(item => itemList?.append(createOptionItem(item.value, item.clientKey)));
    }
    valueCell.append(itemEditor);

    deleteButton.className = "option-delete-button";
    deleteButton.type = "button";
    deleteButton.dataset.optionDelete = "";
    deleteButton.textContent = "삭제";
    actionCell.className = "option-delete-cell";
    actionCell.append(deleteButton);

    row.append(optionHeader, optionCell, valueHeader, valueCell, actionCell);
    return row;
}

function initializeEditOptions(optionRowsContainer) {
    const product = window.productEditData;
    if (!product || !Array.isArray(product.optionGroups) || product.optionGroups.length === 0) return;

    optionRowsContainer.innerHTML = "";
    product.optionGroups
        .map(group => ({
            clientKey: `group-${group.id}`,
            name: group.name || "",
            items: (group.items || []).map(item => ({
                clientKey: `item-${item.id}`,
                value: item.value || ""
            }))
        }))
        .forEach((group, index) => {
            optionRowsContainer.append(createOptionRow(index + 1, group));
        });
}

function createOptionItemEditor(index) {
    const editor = document.createElement("div");
    const openButton = document.createElement("button");
    const addRow = document.createElement("div");
    const draftInput = document.createElement("input");
    const addButton = document.createElement("button");
    const cancelButton = document.createElement("button");
    const itemList = document.createElement("div");

    editor.className = "option-item-editor";
    editor.dataset.optionItemEditor = "";
    openButton.className = "option-item-open-button";
    openButton.type = "button";
    openButton.dataset.optionItemOpen = "";
    openButton.textContent = "항목 추가";

    addRow.className = "option-item-add-row";
    addRow.hidden = true;

    draftInput.id = `option${index}-value`;
    draftInput.type = "text";
    draftInput.maxLength = 30;
    draftInput.dataset.optionItemDraft = "";

    addButton.className = "option-item-add-button";
    addButton.type = "button";
    addButton.dataset.optionItemAdd = "";
    addButton.textContent = "확인";

    cancelButton.className = "option-item-cancel-button";
    cancelButton.type = "button";
    cancelButton.dataset.optionItemCancel = "";
    cancelButton.textContent = "취소";

    itemList.className = "option-item-list";
    itemList.dataset.optionItemList = "";

    addRow.append(draftInput, addButton, cancelButton);
    editor.append(openButton, addRow, itemList);
    return editor;
}

function openOptionItemDraft(row) {
    if (!row) return;

    const addRow = row.querySelector(".option-item-add-row");
    const openButton = row.querySelector("[data-option-item-open]");
    const draftInput = row.querySelector("[data-option-item-draft]");

    if (addRow) addRow.hidden = false;
    if (openButton) openButton.hidden = true;
    draftInput?.focus();
}

function closeOptionItemDraft(row) {
    if (!row) return;

    const addRow = row.querySelector(".option-item-add-row");
    const openButton = row.querySelector("[data-option-item-open]");
    const draftInput = row.querySelector("[data-option-item-draft]");

    if (draftInput) draftInput.value = "";
    if (addRow) addRow.hidden = true;
    if (openButton) openButton.hidden = false;
}

function addOptionItem(row) {
    if (!row) return false;

    const draftInput = row.querySelector("[data-option-item-draft]");
    const itemList = row.querySelector("[data-option-item-list]");
    const value = draftInput?.value.trim() || "";

    if (!draftInput || !itemList || value === "") return false;

    itemList.append(createOptionItem(value));
    closeOptionItemDraft(row);
    return true;
}

function createOptionItem(value, clientKey = null) {
    const item = document.createElement("div");
    const input = document.createElement("input");
    const deleteButton = document.createElement("button");

    item.className = "option-item";
    item.dataset.optionItem = "";
    item.dataset.optionItemKey = clientKey || createClientKey("item");

    input.type = "text";
    input.maxLength = 30;
    input.value = value;
    input.dataset.optionItemValue = "";

    deleteButton.className = "option-item-delete-button";
    deleteButton.type = "button";
    deleteButton.dataset.optionItemDelete = "";
    deleteButton.textContent = "삭제";

    item.append(input, deleteButton);
    return item;
}

function updateProductOptions(optionRowsContainer, optionStockBody, optionItemFields, stockInput) {
    updateOptionRows();

    const validGroups = getValidOptionGroups();
    const hasInvalidOptions = hasInvalidOptionRows();
    renderOptionItemFields(optionItemFields, validGroups);
    renderOptionStockRows(optionStockBody, validGroups, hasInvalidOptions);
    updateTotalStock(stockInput, optionStockBody);
    dispatchOptionChange(optionRowsContainer);
}

function updateOptionRows() {
    const optionRows = Array.from(document.querySelectorAll("[data-option-row]"));

    optionRows.forEach((row, index) => {
        const optionNumber = index + 1;
        const optionInput = row.querySelector("[data-option-name-input], td:nth-of-type(1) input");
        const draftInput = row.querySelector("[data-option-item-draft]");
        const optionLabel = row.querySelector("th:nth-of-type(1) label");
        const valueLabel = row.querySelector("th:nth-of-type(2) label");

        ensureOptionRowKey(row);

        if (optionInput) {
            optionInput.id = `option${optionNumber}`;
            optionInput.name = "";
            optionInput.maxLength = 50;
            optionInput.dataset.optionNameInput = "";
        }

        if (draftInput) {
            draftInput.id = `option${optionNumber}-value`;
            draftInput.name = "";
            draftInput.maxLength = 30;
            draftInput.dataset.optionItemDraft = "";
        }

        if (optionLabel) {
            optionLabel.htmlFor = `option${optionNumber}`;
            optionLabel.textContent = `옵션${optionNumber}`;
        }

        if (valueLabel) {
            valueLabel.htmlFor = `option${optionNumber}-value`;
            valueLabel.textContent = `옵션${optionNumber} 항목`;
        }
    });
}

function getValidOptionGroups() {
    const groups = Array.from(document.querySelectorAll("[data-option-row]"))
        .map(readOptionGroup)
        .filter(group => group !== null);

    const duplicatedNames = groups
        .map(group => group.name)
        .filter((name, index, names) => names.indexOf(name) !== index || names.lastIndexOf(name) !== index);

    if (duplicatedNames.length === 0) {
        return groups;
    }

    return groups.filter(group => {
        const isDuplicated = duplicatedNames.includes(group.name);

        if (isDuplicated) {
            renderOptionGroupError(group.row, "중복된 옵션 이름은 사용할 수 없습니다.");
        }

        return !isDuplicated;
    });
}

function hasInvalidOptionRows() {
    return Array.from(document.querySelectorAll("[data-option-error]"))
        .some(error => error.textContent.trim() !== "");
}

function readOptionGroup(row) {
    const nameInput = row.querySelector("[data-option-name-input]");
    const name = nameInput?.value.trim() || "";
    const items = readOptionItems(row);
    const hasDraftValue = (row.querySelector("[data-option-item-draft]")?.value.trim() || "") !== "";
    const hasAnyValue = name !== "" || items.length > 0 || hasDraftValue;
    const error = validateOptionGroup(name, items, hasAnyValue, hasDraftValue);

    renderOptionGroupError(row, error);

    if (error || !hasAnyValue) return null;

    return {
        row,
        clientKey: row.dataset.optionGroupKey,
        name,
        items
    };
}

function readOptionItems(row) {
    return Array.from(row.querySelectorAll("[data-option-item]"))
        .map(item => ({
            clientKey: item.dataset.optionItemKey,
            value: item.querySelector("[data-option-item-value]")?.value.trim() || ""
        }));
}

function validateOptionGroup(name, items, hasAnyValue, hasDraftValue) {
    const values = items.map(item => item.value);

    if (!hasAnyValue) return "";
    if (!name) return "옵션 이름을 입력해주세요.";
    if (name.length > 50) return "옵션 이름은 50자 이하로 입력해주세요.";
    if (hasDraftValue) return "입력 중인 옵션 항목을 추가하거나 비워주세요.";
    if (items.length === 0) return "옵션 항목을 1개 이상 추가해주세요.";
    if (values.some(value => value === "")) return "빈 옵션 항목은 사용할 수 없습니다.";
    if (values.some(value => value.length > 30)) return "옵션 항목은 각각 30자 이하로 입력해주세요.";
    if (values.length !== new Set(values).size) return "중복된 옵션 항목은 사용할 수 없습니다.";

    return "";
}

function renderOptionGroupError(row, message) {
    const valueCell = row.querySelector("td:nth-of-type(2)");
    let error = row.querySelector("[data-option-error]");

    row.querySelectorAll("[data-option-name-input], [data-option-item-value], [data-option-item-draft]").forEach(input => {
        input.classList.toggle("is-invalid", Boolean(message));
    });

    if (!valueCell) return;

    if (!error) {
        error = document.createElement("p");
        error.className = "field-error";
        error.dataset.optionError = "";
        valueCell.append(error);
    }

    error.textContent = message;
}

function renderOptionItemFields(optionItemFields, groups) {
    if (!optionItemFields) return;

    optionItemFields.innerHTML = "";

    groups.forEach((group, groupIndex) => {
        appendHiddenInput(optionItemFields, `optionGroups[${groupIndex}].clientKey`, group.clientKey);
        appendHiddenInput(optionItemFields, `optionGroups[${groupIndex}].name`, group.name);

        group.items.forEach((item, itemIndex) => {
            appendHiddenInput(optionItemFields, `optionGroups[${groupIndex}].items[${itemIndex}].clientKey`, item.clientKey);
            appendHiddenInput(optionItemFields, `optionGroups[${groupIndex}].items[${itemIndex}].value`, item.value);
        });
    });
}

function renderOptionStockRows(optionStockBody, groups, hasInvalidOptions) {
    if (!optionStockBody) return;

    const previousValues = getPreviousVariantValues(optionStockBody);
    const combinations = getOptionCombinations(groups, hasInvalidOptions);

    optionStockBody.innerHTML = "";

    combinations.forEach((combination, index) => {
        const previous = previousValues.get(combination.key) || {};
        optionStockBody.append(createOptionStockRow(combination, index, previous));
    });
}

function getPreviousVariantValues(optionStockBody) {
    const values = new Map();

    optionStockBody.querySelectorAll("[data-option-stock-row]").forEach(row => {
        const key = row.dataset.optionKey;
        if (!key) return;

        values.set(key, {
            stock: row.querySelector("[data-option-stock-input]")?.value || "0",
            status: row.querySelector("[data-option-status-input]")?.value || "ON_SALE"
        });
    });

    if (values.size === 0) {
        getEditVariantValues().forEach((value, key) => values.set(key, value));
    }

    return values;
}

function getEditVariantValues() {
    const values = new Map();
    const variants = window.productEditData?.variants || [];

    variants.forEach(variant => {
        const items = variant.items || [];
        const key = items.length === 0
            ? "default"
            : items.map(item => `item-${item.id}`).join("|");

        values.set(key, {
            stock: String(variant.stock ?? 0),
            status: variant.status || "ON_SALE"
        });
    });

    return values;
}

function getOptionCombinations(groups, hasInvalidOptions) {
    if (groups.length === 0 && hasInvalidOptions) {
        return [];
    }

    if (groups.length === 0) {
        return [{ key: "default", label: "기본상품", items: [] }];
    }

    return combineOptionGroups(groups).map(items => {
        const label = items.map(item => `${item.groupName}: ${item.value}`).join(" / ");

        return {
            key: items.map(item => item.itemKey).join("|"),
            label,
            items
        };
    });
}

function combineOptionGroups(groups) {
    return groups.reduce((combinations, group) => {
        return combinations.flatMap(combination => {
            return group.items.map(item => [
                ...combination,
                {
                    groupKey: group.clientKey,
                    itemKey: item.clientKey,
                    groupName: group.name,
                    value: item.value
                }
            ]);
        });
    }, [[]]);
}

function createOptionStockRow(combination, index, previous) {
    const row = document.createElement("tr");
    const optionCell = document.createElement("td");
    const stockCell = document.createElement("td");
    const statusCell = document.createElement("td");
    const optionLabelText = document.createElement("span");
    const stockInput = document.createElement("input");
    const statusSelect = document.createElement("select");

    row.dataset.optionStockRow = "";
    row.dataset.optionKey = combination.key;

    optionLabelText.textContent = combination.label;
    optionCell.append(optionLabelText);

    combination.items.forEach((item, itemIndex) => {
        appendHiddenInput(optionCell, `variants[${index}].items[${itemIndex}].groupKey`, item.groupKey);
        appendHiddenInput(optionCell, `variants[${index}].items[${itemIndex}].itemKey`, item.itemKey);
    });

    stockInput.type = "number";
    stockInput.name = `variants[${index}].stock`;
    stockInput.value = previous.stock || "0";
    stockInput.min = "0";
    stockInput.required = true;
    stockInput.dataset.optionStockInput = "";
    stockCell.append(stockInput, "개");

    appendStatusOption(statusSelect, "ON_SALE", "판매중");
    appendStatusOption(statusSelect, "SOLD_OUT", "품절");
    appendStatusOption(statusSelect, "STOPPED", "판매중지");
    statusSelect.name = `variants[${index}].status`;
    statusSelect.value = previous.status || "ON_SALE";
    statusSelect.dataset.optionStatusInput = "";
    statusCell.append(statusSelect);

    row.append(optionCell, stockCell, statusCell);
    return row;
}

function appendStatusOption(select, value, label) {
    const option = document.createElement("option");
    option.value = value;
    option.textContent = label;
    select.append(option);
}

function appendHiddenInput(parent, name, value) {
    const input = document.createElement("input");
    input.type = "hidden";
    input.name = name;
    input.value = value;
    parent.append(input);
}

function updateTotalStock(stockInput, optionStockBody) {
    if (!stockInput || !optionStockBody) return;

    const totalStock = Array.from(optionStockBody.querySelectorAll("[data-option-stock-input]"))
        .reduce((sum, input) => sum + toNonNegativeNumber(input.value), 0);

    stockInput.value = totalStock;
}

function toNonNegativeNumber(value) {
    const numberValue = Number(value);

    if (!Number.isFinite(numberValue) || numberValue < 0) {
        return 0;
    }

    return numberValue;
}

function ensureOptionRowKey(row) {
    if (!row.dataset.optionGroupKey) {
        row.dataset.optionGroupKey = createClientKey("group");
    }
}

function createClientKey(prefix) {
    if (window.crypto?.randomUUID) {
        return `${prefix}-${window.crypto.randomUUID()}`;
    }

    return `${prefix}-${Date.now()}-${Math.random().toString(36).slice(2)}`;
}

function dispatchOptionChange(optionRowsContainer) {
    optionRowsContainer.dispatchEvent(new CustomEvent("product-option-change", {
        bubbles: true
    }));
}
