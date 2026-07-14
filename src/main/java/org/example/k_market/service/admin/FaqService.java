package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.FaqDAO;
import org.example.k_market.dto.admin.FaqDTO;
import org.example.k_market.dto.common.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FaqService {

    private static final int PAGE_SIZE = 10;

    private final FaqDAO faqDAO;


    /* =========================================================
       FAQ 목록
       ========================================================= */

    /**
     * 기존 코드 호환용
     */
    public List<FaqDTO> getFaqList(
            int page,
            String category1
    ) {
        return getFaqList(
                page,
                category1,
                null
        );
    }

    /**
     * FAQ 1차·2차 유형별 목록
     */
    public List<FaqDTO> getFaqList(
            int page,
            String category1,
            String category2
    ) {
        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * PAGE_SIZE;

        String safeCategory1 =
                normalize(category1);

        String safeCategory2 =
                normalize(category2);

        if (safeCategory1 == null) {
            safeCategory2 = null;
        }

        return faqDAO.getFaqList(
                offset,
                PAGE_SIZE,
                safeCategory1,
                safeCategory2
        );
    }


    /* =========================================================
       FAQ 페이징
       ========================================================= */

    /**
     * 기존 코드 호환용
     */
    public PageInfo getPageInfo(
            int page,
            String category1
    ) {
        return getPageInfo(
                page,
                category1,
                null
        );
    }

    /**
     * FAQ 1차·2차 유형별 페이징
     */
    public PageInfo getPageInfo(
            int page,
            String category1,
            String category2
    ) {
        int safePage = Math.max(page, 1);

        String safeCategory1 =
                normalize(category1);

        String safeCategory2 =
                normalize(category2);

        if (safeCategory1 == null) {
            safeCategory2 = null;
        }

        int totalCount =
                faqDAO.getTotalCount(
                        safeCategory1,
                        safeCategory2
                );

        Page<FaqDTO> pageResult =
                new PageImpl<>(
                        List.of(),
                        PageRequest.of(
                                safePage - 1,
                                PAGE_SIZE
                        ),
                        totalCount
                );

        return new PageInfo(pageResult);
    }


    /* =========================================================
       상세 조회
       ========================================================= */

    public FaqDTO getFaq(int boardNo) {

        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "FAQ 번호가 올바르지 않습니다."
            );
        }

        FaqDTO faq =
                faqDAO.getFaqByNo(boardNo);

        if (faq == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 FAQ입니다."
            );
        }

        return faq;
    }


    /* =========================================================
       등록
       ========================================================= */

    @Transactional
    public void insertFaq(FaqDTO dto) {

        validateFaq(dto);

        dto.setTitle(
                dto.getTitle().trim()
        );

        dto.setContent(
                dto.getContent().trim()
        );

        int result =
                faqDAO.insertFaq(dto);

        if (result == 0) {
            throw new IllegalStateException(
                    "FAQ 등록에 실패했습니다."
            );
        }
    }


    /* =========================================================
       수정
       ========================================================= */

    @Transactional
    public void updateFaq(FaqDTO dto) {

        if (dto == null ||
                dto.getBoardNo() <= 0) {

            throw new IllegalArgumentException(
                    "FAQ 번호가 올바르지 않습니다."
            );
        }

        validateFaq(dto);

        dto.setTitle(
                dto.getTitle().trim()
        );

        dto.setContent(
                dto.getContent().trim()
        );

        int result =
                faqDAO.updateFaq(dto);

        if (result == 0) {
            throw new IllegalArgumentException(
                    "수정할 FAQ가 없습니다."
            );
        }
    }


    /* =========================================================
       삭제
       ========================================================= */

    @Transactional
    public void deleteFaqs(
            List<Integer> boardNoList
    ) {
        if (boardNoList == null ||
                boardNoList.isEmpty()) {
            return;
        }

        List<Integer> validBoardNoList =
                boardNoList.stream()
                        .filter(boardNo ->
                                boardNo != null &&
                                        boardNo > 0
                        )
                        .distinct()
                        .toList();

        if (validBoardNoList.isEmpty()) {
            return;
        }

        faqDAO.deleteFaqs(
                validBoardNoList
        );
    }


    /* =========================================================
       검증
       ========================================================= */

    private void validateFaq(FaqDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "FAQ 정보가 없습니다."
            );
        }

        if (dto.getBoardType() == null ||
                dto.getBoardType().isBlank() ||
                !dto.getBoardType().startsWith("faq/")) {

            throw new IllegalArgumentException(
                    "FAQ 유형을 선택해주세요."
            );
        }

        if (dto.getCategory1() == null ||
                dto.getCategory1().isBlank()) {

            throw new IllegalArgumentException(
                    "1차 FAQ 유형을 선택해주세요."
            );
        }

        if (dto.getCategory2() == null ||
                dto.getCategory2().isBlank()) {

            throw new IllegalArgumentException(
                    "2차 FAQ 유형을 선택해주세요."
            );
        }

        if (dto.getTitle() == null ||
                dto.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "FAQ 제목을 입력해주세요."
            );
        }

        if (dto.getContent() == null ||
                dto.getContent().isBlank()) {

            throw new IllegalArgumentException(
                    "FAQ 내용을 입력해주세요."
            );
        }
    }

    private String normalize(String value) {

        if (value == null ||
                value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}