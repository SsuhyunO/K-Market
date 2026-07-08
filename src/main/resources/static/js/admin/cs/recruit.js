console.log("recruit.js loaded");

document.addEventListener("DOMContentLoaded", function () {
    console.log("recruit DOMContentLoaded");

    initRecruitEditButton();
    initRecruitSelectAll();
});

function initRecruitEditButton() {
    const btnSelectEdit = document.getElementById("btn-select-edit");

    console.log("btnSelectEdit =", btnSelectEdit);

    if (!btnSelectEdit) {
        console.log("선택수정 버튼을 찾지 못했습니다.");
        return;
    }

    btnSelectEdit.addEventListener("click", function () {
        console.log("선택수정 버튼 클릭됨");

        const checkedBoxes = document.querySelectorAll('input[name="recruitNo"]:checked');

        console.log("checkedBoxes.length =", checkedBoxes.length);

        if (checkedBoxes.length === 0) {
            alert("수정할 채용공고를 선택해주세요.");
            return;
        }

        if (checkedBoxes.length > 1) {
            alert("수정은 한 번에 하나의 채용공고만 가능합니다.");
            return;
        }

        const id = checkedBoxes[0].value;
        const contextPath = window.location.pathname.startsWith("/K_Market") ? "/K_Market" : "";

        location.href = `${contextPath}/admin/cs/recruit/modify?id=${id}`;
    });
}

function initRecruitSelectAll() {
    const selectAll = document.getElementById("select-all");

    if (!selectAll) return;

    selectAll.addEventListener("change", function () {
        const checkboxes = document.querySelectorAll('input[name="recruitNo"]');

        checkboxes.forEach(function (checkbox) {
            checkbox.checked = selectAll.checked;
        });
    });
}