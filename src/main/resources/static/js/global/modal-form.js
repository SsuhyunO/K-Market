export function initModals() {
    bindModalOpenButtons();
    bindModalCloseButtons();
    bindModalBackdropClose();
    bindModalEscapeClose();
}

function bindModalOpenButtons() {
    document.querySelectorAll("[data-modal-target]").forEach(button => {
        button.addEventListener("click", function () {
            const modal = document.getElementById(button.dataset.modalTarget);
            if (!modal) return;

            openModal(modal);
        });
    });
}

function bindModalCloseButtons() {
    document.querySelectorAll("[data-modal-close]").forEach(button => {
        button.addEventListener("click", function () {
            const modal = button.closest("[data-modal]");
            if (!modal) return;

            closeModal(modal);
        });
    });
}

function bindModalBackdropClose() {
    document.querySelectorAll("[data-modal]").forEach(modal => {
        let isBackdropPointerDown = false;

        modal.addEventListener("mousedown", function (e) {
            isBackdropPointerDown = e.target === modal;
        });

        modal.addEventListener("click", function (e) {
            if (!isBackdropPointerDown || e.target !== modal) return;
            closeModal(modal);
        });
    });
}

function bindModalEscapeClose() {
    document.addEventListener("keydown", function (e) {
        if (e.key !== "Escape") return;

        document.querySelectorAll("[data-modal]:not(.hidden)").forEach(closeModal);
    });
}

export function openModal(modal) {
    modal.classList.remove("hidden");
    modal.dispatchEvent(new CustomEvent("modal:open", { bubbles: true }));
}

export function closeModal(modal) {
    modal.classList.add("hidden");
    modal.dispatchEvent(new CustomEvent("modal:close", { bubbles: true }));
}
