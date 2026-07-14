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

        /*
         * 1차 유형이 없으면
         * 2차 유형도 필터에 적용하지 않는다.
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


    /* =========================================================
       문의글 상세
       ========================================================= */

    /**
     * 문의글 한 건 조회
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


    /* =========================================================
       사용자 문의글 등록
       ========================================================= */

    /**
     * 사용자 문의글 등록
     *
     * CsController에서 로그인 회원 UID를
     * dto.memberUid에 넣은 후 호출한다.
     */
    @Transactional
    public void insertQna(QnaDTO dto) {

        validateQna(dto);

        /*
         * board.memberUid가 member 테이블과
         * 외래키로 연결되어 있으므로
         * 실제 존재하는 로그인 회원 UID가 필요하다.
         */
        if (dto.getMemberUid() == null ||
                dto.getMemberUid().isBlank()) {

            throw new IllegalArgumentException(
                    "로그인 회원 정보가 없습니다."
            );
        }

        dto.setMemberUid(
                dto.getMemberUid().trim()
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

        qnaDAO.deleteQnas(validBoardNoList);
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

        /*
         * 정상적인 boardType 예:
         * qna/회원/가입
         */
        if (dto.getBoardType() == null ||
                dto.getBoardType().isBlank() ||
                !dto.getBoardType().startsWith("qna/")) {

            throw new IllegalArgumentException(
                    "문의 유형을 선택해주세요."
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
     * 검색 조건값 정리
     *
     * 빈 문자열은 null로 변환하여
     * MyBatis 동적 조건에서 제외한다.
     */
    private String normalize(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}