package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.QnaDAO;
import org.example.k_market.dto.admin.QnaDTO;
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
public class QnaService {

    private static final int PAGE_SIZE = 10;

    private final QnaDAO qnaDAO;

    /**
     * 문의글 목록 조회
     */
    public List<QnaDTO> getQnaList(
            int page,
            String category1,
            String category2
    ) {
        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * PAGE_SIZE;

        String safeCategory1 = normalize(category1);
        String safeCategory2 = normalize(category2);

        /*
         * 1차 유형이 없으면 2차 유형도 적용하지 않음
         */
        if (safeCategory1 == null) {
            safeCategory2 = null;
        }

        return qnaDAO.getQnaList(
                offset,
                PAGE_SIZE,
                safeCategory1,
                safeCategory2
        );
    }

    /**
     * 문의글 페이징 정보
     */
    public PageInfo getPageInfo(
            int page,
            String category1,
            String category2
    ) {
        int safePage = Math.max(page, 1);

        String safeCategory1 = normalize(category1);
        String safeCategory2 = normalize(category2);

        if (safeCategory1 == null) {
            safeCategory2 = null;
        }

        int totalCount = qnaDAO.getTotalCount(
                safeCategory1,
                safeCategory2
        );

        Page<QnaDTO> pageResult = new PageImpl<>(
                List.of(),
                PageRequest.of(safePage - 1, PAGE_SIZE),
                totalCount
        );

        return new PageInfo(pageResult);
    }

    /**
     * 문의글 상세 조회
     */
    public QnaDTO getQna(int boardNo) {

        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "문의글 번호가 올바르지 않습니다."
            );
        }

        QnaDTO qna = qnaDAO.getQnaByNo(boardNo);

        if (qna == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 문의글입니다."
            );
        }

        return qna;
    }

    /**
     * 관리자 답변 등록 및 수정
     */
    @Transactional
    public void updateAnswer(
            int boardNo,
            String answer
    ) {
        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "문의글 번호가 올바르지 않습니다."
            );
        }

        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException(
                    "답변 내용을 입력해주세요."
            );
        }

        int result = qnaDAO.updateAnswer(
                boardNo,
                answer.trim()
        );

        if (result == 0) {
            throw new IllegalArgumentException(
                    "답변할 문의글이 존재하지 않습니다."
            );
        }
    }

    /**
     * 문의글 단일 및 선택 삭제
     */
    @Transactional
    public void deleteQnas(
            List<Integer> boardNoList
    ) {
        if (boardNoList == null || boardNoList.isEmpty()) {
            return;
        }

        List<Integer> validBoardNoList = boardNoList.stream()
                .filter(boardNo -> boardNo != null && boardNo > 0)
                .distinct()
                .toList();

        if (validBoardNoList.isEmpty()) {
            return;
        }

        qnaDAO.deleteQnas(validBoardNoList);
    }

    /**
     * 검색 유형값 정리
     */
    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}