document.addEventListener('DOMContentLoaded', function () {

    /* =========================================================
       모달 관리
       ========================================================= */

    const modalStack = [];

    function openModal(modalId) {
        const modal = document.getElementById(modalId);

        if (!modal) {
            return;
        }

        const currentModal =
            document.querySelector('.modal-overlay.active');

        if (currentModal &&
            currentModal.id !== modalId) {

            currentModal.classList.remove('active');
            modalStack.push(currentModal.id);
        }

        modal.classList.add('active');
    }

    function closeModal(modalId) {
        const modal = document.getElementById(modalId);

        if (!modal) {
            return;
        }

        modal.classList.remove('active');

        if (modalStack.length > 0) {
            const previousModalId = modalStack.pop();

            const previousModal =
                document.getElementById(previousModalId);

            if (previousModal) {
                previousModal.classList.add('active');
            }
        }
    }

    function setTextContent(elementId, value) {
        const element =
            document.getElementById(elementId);

        if (element) {
            element.textContent = value;
        }
    }


    /* =========================================================
       모달 닫기
       ========================================================= */

    document
        .querySelectorAll('[data-close]')
        .forEach(function (button) {

            button.addEventListener(
                'click',
                function () {

                    closeModal(
                        button.getAttribute('data-close')
                    );
                }
            );
        });

    document
        .querySelectorAll('.modal-overlay')
        .forEach(function (overlay) {

            overlay.addEventListener(
                'click',
                function (event) {

                    if (event.target === overlay) {
                        closeModal(overlay.id);
                    }
                }
            );
        });


    /* =========================================================
       문의 등록 모달 열기
       ========================================================= */

    const openInquiryButton =
        document.getElementById('openInquiryBtn');

    if (openInquiryButton) {
        openInquiryButton.addEventListener(
            'click',
            function () {
                openModal('inquiryModal');
            }
        );
    }


    /* =========================================================
       문의 등록
       POST /cs/qnaWrite
       ========================================================= */

    const inquirySubmitButton =
        document.getElementById('inquirySubmitBtn');

    if (inquirySubmitButton) {
        inquirySubmitButton.addEventListener(
            'click',
            async function () {

                const selectedType =
                    document.querySelector(
                        'input[name="inquiryType"]:checked'
                    );

                const titleInput =
                    document.getElementById('inquiryTitle');

                const contentInput =
                    document.getElementById('inquiryContent');

                const title =
                    titleInput
                        ? titleInput.value.trim()
                        : '';

                const content =
                    contentInput
                        ? contentInput.value.trim()
                        : '';

                if (!selectedType) {
                    alert('문의종류를 선택해주세요.');
                    return;
                }

                if (!title) {
                    alert('제목을 입력해주세요.');

                    if (titleInput) {
                        titleInput.focus();
                    }

                    return;
                }

                if (title.length > 100) {
                    alert('문의 제목은 100자 이하로 입력해주세요.');

                    if (titleInput) {
                        titleInput.focus();
                    }

                    return;
                }

                if (!content) {
                    alert('내용을 입력해주세요.');

                    if (contentInput) {
                        contentInput.focus();
                    }

                    return;
                }

                if (content.length > 2000) {
                    alert('문의 내용은 2000자 이하로 입력해주세요.');

                    if (contentInput) {
                        contentInput.focus();
                    }

                    return;
                }

                const categoryMap = {
                    product: {
                        category1: '상품',
                        category2: '상품문의'
                    },

                    delivery: {
                        category1: '주문/배송',
                        category2: '배송'
                    },

                    return: {
                        category1: '취소/반품',
                        category2: '반품/취소'
                    },

                    exchange: {
                        category1: '취소/반품',
                        category2: '교환'
                    },

                    etc: {
                        category1: '기타',
                        category2: '기타'
                    }
                };

                const selectedCategory =
                    categoryMap[selectedType.value];

                if (!selectedCategory) {
                    alert('문의종류가 올바르지 않습니다.');
                    return;
                }

                const submitUrl =
                    inquirySubmitButton.dataset.submitUrl;

                const listUrl =
                    inquirySubmitButton.dataset.listUrl;

                if (!submitUrl || !listUrl) {
                    alert('문의 등록 주소가 설정되지 않았습니다.');
                    return;
                }

                const requestBody =
                    new URLSearchParams();

                requestBody.append(
                    'category1',
                    selectedCategory.category1
                );

                requestBody.append(
                    'category2',
                    selectedCategory.category2
                );

                requestBody.append(
                    'title',
                    title
                );

                requestBody.append(
                    'content',
                    content
                );

                const originalButtonText =
                    inquirySubmitButton.textContent;

                try {
                    inquirySubmitButton.disabled = true;
                    inquirySubmitButton.textContent = '등록 중...';

                    const response =
                        await fetch(
                            submitUrl,
                            {
                                method: 'POST',
                                credentials: 'include',

                                headers: {
                                    'Content-Type':
                                        'application/x-www-form-urlencoded;charset=UTF-8'
                                },

                                body: requestBody.toString(),

                                redirect: 'follow'
                            }
                        );

                    if (!response.ok) {
                        throw new Error(
                            '문의글 등록에 실패했습니다.'
                        );
                    }

                    if (response.url.includes('/member/login')) {
                        alert(
                            '로그인 후 문의글을 등록할 수 있습니다.'
                        );

                        window.location.href =
                            response.url;

                        return;
                    }

                    alert('문의글이 등록되었습니다.');

                    window.location.href =
                        listUrl;

                } catch (error) {
                    console.error(
                        '문의 등록 오류:',
                        error
                    );

                    alert(
                        error.message ||
                        '문의글 등록 중 오류가 발생했습니다.'
                    );

                } finally {
                    inquirySubmitButton.disabled = false;
                    inquirySubmitButton.textContent =
                        originalButtonText;
                }
            }
        );
    }


    /* =========================================================
       문의 상세 모달
       ========================================================= */

    document
        .querySelectorAll('.qna-title-button')
        .forEach(function (button) {

            button.addEventListener(
                'click',
                function () {

                    const category1 =
                        button.dataset.category1 || '';

                    const category2 =
                        button.dataset.category2 || '';

                    const category =
                        category2
                            ? `${category1} / ${category2}`
                            : category1;

                    const answer =
                        button.dataset.answer || '';

                    setTextContent(
                        'qnaDetailChannel',
                        button.dataset.channel || '고객센터'
                    );

                    setTextContent(
                        'qnaDetailType',
                        category || '-'
                    );

                    setTextContent(
                        'qnaDetailDate',
                        button.dataset.createdAt || '-'
                    );

                    setTextContent(
                        'qnaDetailTitle',
                        button.dataset.title || '-'
                    );

                    setTextContent(
                        'qnaDetailContent',
                        button.dataset.content || '-'
                    );

                    setTextContent(
                        'qnaDetailStatus',
                        answer
                            ? '답변완료'
                            : '답변대기'
                    );

                    const answerRow =
                        document.getElementById(
                            'qnaDetailAnswerRow'
                        );

                    const answerCell =
                        document.getElementById(
                            'qnaDetailAnswer'
                        );

                    if (answerRow && answerCell) {
                        if (answer) {
                            answerRow.style.display = '';
                            answerCell.textContent = answer;
                        } else {
                            answerRow.style.display = 'none';
                            answerCell.textContent = '';
                        }
                    }

                    openModal('qnaDetailModal');
                }
            );
        });


    /* =========================================================
       답변 내용 모달
       ========================================================= */

    document
        .querySelectorAll(
            '.qna-status-button.status-complete'
        )
        .forEach(function (button) {

            button.addEventListener(
                'click',
                function () {

                    const category1 =
                        button.dataset.category1 || '';

                    const category2 =
                        button.dataset.category2 || '';

                    const category =
                        category2
                            ? `${category1} / ${category2}`
                            : category1;

                    setTextContent(
                        'answerChannel',
                        button.dataset.channel || '고객센터'
                    );

                    setTextContent(
                        'answerType',
                        category || '-'
                    );

                    setTextContent(
                        'answerDate',
                        button.dataset.createdAt || '-'
                    );

                    setTextContent(
                        'answerTitle',
                        button.dataset.title || '-'
                    );

                    setTextContent(
                        'answerQuestion',
                        button.dataset.content || '-'
                    );

                    setTextContent(
                        'answerAnswerDate',
                        button.dataset.answeredAt || '-'
                    );

                    setTextContent(
                        'answerContent',
                        button.dataset.answer || '-'
                    );

                    openModal('qnaAnswerModal');
                }
            );
        });

});