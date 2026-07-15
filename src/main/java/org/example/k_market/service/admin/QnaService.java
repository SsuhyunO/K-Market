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


    /* =========================================================
       문의글 목록
       ========================================================= */

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
     * 문의글 페이징 정보 조회
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
                PageRequest.of(
                        safePage - 1,
                        PAGE_SIZE
                ),
                totalCount
        );

        return new PageInfo(pageResult);
    }


    /**
     * 로그인 회원의 문의글 전체 조회
     */
    public List<QnaDTO> getQnaListByMemberUid(
            String memberUid
    ) {
        if (memberUid == null || memberUid.isBlank()) {
            return List.of();
        }

        return qnaDAO.getQnaListByMemberUid(
                memberUid.trim()
        );
    }


    /**
     * 로그인 회원의 최근 문의글 5개 조회
     */
    public List<QnaDTO> getRecentQnaListByMemberUid(
            String memberUid
    ) {
        if (memberUid == null || memberUid.isBlank()) {
            return List.of();
        }

        return qnaDAO.getRecentQnaListByMemberUid(
                memberUid.trim()
        );
    }


    /* =========================================================
       문의글 상세
       ========================================================= */

    /**
     * 문의글 한 건 조회
     */
    public QnaDTO getQna(int boardNo) {

        validateBoardNo(boardNo);

        QnaDTO qna = qnaDAO.getQnaByNo(boardNo);

        if (qna == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 문의글입니다."
            );
        }

        return qna;
    }


    /* =========================================================
       사용자 문의글 등록
       ========================================================= */

    /**
     * 사용자 문의글 등록
     */
    @Transactional
    public void insertQna(QnaDTO dto) {

        validateQna(dto);

        if (dto.getMemberUid() == null ||
                dto.getMemberUid().isBlank()) {

            throw new IllegalArgumentException(
                    "로그인 회원 정보가 없습니다."
            );
        }

        dto.setMemberUid(
                dto.getMemberUid().trim()
        );

        dto.setCategory1(
                dto.getCategory1().trim()
        );

        dto.setCategory2(
                dto.getCategory2().trim()
        );

        dto.setTitle(
                dto.getTitle().trim()
        );

        dto.setContent(
                dto.getContent().trim()
        );

        int result = qnaDAO.insertQna(dto);

        if (result == 0) {
            throw new IllegalStateException(
                    "문의글 등록에 실패했습니다."
            );
        }
    }


    /* =========================================================
       관리자 답변
       ========================================================= */

    /**
     * 관리자 답변 등록 및 수정
     */
    @Transactional
    public void updateAnswer(
            int boardNo,
            String answer
    ) {
        validateBoardNo(boardNo);

        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException(
                    "답변 내용을 입력해주세요."
            );
        }

        QnaDTO qna = qnaDAO.getQnaByNo(boardNo);

        if (qna == null) {
            throw new IllegalArgumentException(
                    "답변할 문의글이 존재하지 않습니다."
            );
        }

        int result = qnaDAO.updateAnswer(
                boardNo,
                answer.trim()
        );

        if (result == 0) {
            throw new IllegalStateException(
                    "문의 답변 등록에 실패했습니다."
            );
        }
    }


    /**
     * 관리자 문의 답변 삭제
     *
     * 문의글 자체는 유지하고
     * answer, answeredAt 값만 NULL로 초기화한다.
     */
    @Transactional
    public void deleteAnswer(int boardNo) {

        validateBoardNo(boardNo);

        QnaDTO qna = qnaDAO.getQnaByNo(boardNo);

        if (qna == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 문의글입니다."
            );
        }

        if (qna.getAnswer() == null ||
                qna.getAnswer().isBlank()) {

            throw new IllegalStateException(
                    "삭제할 답변이 없습니다."
            );
        }

        int result = qnaDAO.deleteAnswer(boardNo);

        if (result == 0) {
            throw new IllegalStateException(
                    "문의 답변 삭제에 실패했습니다."
            );
        }
    }


    /* =========================================================
       문의글 삭제
       ========================================================= */

    /**
     * 문의글 단일 및 선택 삭제
     */
    @Transactional
    public void deleteQnas(
            List<Integer> boardNoList
    ) {
        if (boardNoList == null ||
                boardNoList.isEmpty()) {

            throw new IllegalArgumentException(
                    "삭제할 문의글을 선택해주세요."
            );
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
            throw new IllegalArgumentException(
                    "삭제할 문의글 번호가 올바르지 않습니다."
            );
        }

        int result = qnaDAO.deleteQnas(
                validBoardNoList
        );

        if (result == 0) {
            throw new IllegalStateException(
                    "문의글 삭제에 실패했습니다."
            );
        }
    }


    /* =========================================================
       입력값 검증
       ========================================================= */

    /**
     * 문의글 등록값 검증
     */
    private void validateQna(QnaDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "문의글 정보가 없습니다."
            );
        }

        if (dto.getCategory1() == null ||
                dto.getCategory1().isBlank()) {

            throw new IllegalArgumentException(
                    "1차 문의 유형을 선택해주세요."
            );
        }

        if (dto.getCategory2() == null ||
                dto.getCategory2().isBlank()) {

            throw new IllegalArgumentException(
                    "2차 문의 유형을 선택해주세요."
            );
        }

        if (dto.getTitle() == null ||
                dto.getTitle().isBlank()) {

            throw new IllegalArgumentException(
                    "문의 제목을 입력해주세요."
            );
        }

        if (dto.getTitle().trim().length() > 100) {
            throw new IllegalArgumentException(
                    "문의 제목은 100자 이하로 입력해주세요."
            );
        }

        if (dto.getContent() == null ||
                dto.getContent().isBlank()) {

            throw new IllegalArgumentException(
                    "문의 내용을 입력해주세요."
            );
        }

        if (dto.getContent().trim().length() > 2000) {
            throw new IllegalArgumentException(
                    "문의 내용은 2000자 이하로 입력해주세요."
            );
        }
    }


    /**
     * 문의글 번호 검증
     */
    private void validateBoardNo(int boardNo) {

        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "문의글 번호가 올바르지 않습니다."
            );
        }
    }


    /**
     * 검색 조건값 정리
     */
    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}