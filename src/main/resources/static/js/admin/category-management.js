document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".category-tree summary .category-delete-button").forEach(button => {
        button.addEventListener("click", function (e) {
            e.preventDefault();
            e.stopPropagation();
        });
    });
});
