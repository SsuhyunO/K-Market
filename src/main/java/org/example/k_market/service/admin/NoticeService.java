package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.NoticeDAO;
import org.example.k_market.dto.admin.NoticeDTO;
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
public class NoticeService {

    private static final int PAGE_SIZE = 10;

    private final NoticeDAO noticeDAO;


    /* =========================================================
       목록 조회
       ========================================================= */

    /**
     * 기존 관리자 코드 호환용
     */
    public List<NoticeDTO> getNoticeList(int page) {
        return getNoticeList(page, null);
    }

    /**
     * 사용자 공지사항 유형 필터 조회
     */
    public List<NoticeDTO> getNoticeList(
            int page,
            String type
    ) {
        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * PAGE_SIZE;

        String safeType = normalizeType(type);

        return noticeDAO.getNoticeList(
                offset,
                PAGE_SIZE,
                safeType
        );
    }


    /* =========================================================
       페이징
       ========================================================= */

    /**
     * 기존 관리자 코드 호환용
     */
    public PageInfo getPageInfo(int page) {
        return getPageInfo(page, null);
    }

    /**
     * 유형별 페이징 정보
     */
    public PageInfo getPageInfo(
            int page,
            String type
    ) {
        int safePage = Math.max(page, 1);
        String safeType = normalizeType(type);

        int totalCount =
                noticeDAO.getTotalCount(safeType);

        Page<NoticeDTO> pageResult =
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

    public NoticeDTO getNotice(int boardNo) {

        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "공지사항 번호가 올바르지 않습니다."
            );
        }

        NoticeDTO notice =
                noticeDAO.getNoticeByNo(boardNo);

        if (notice == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 공지사항입니다."
            );
        }

        return notice;
    }


    /* =========================================================
       등록
       ========================================================= */

    @Transactional
    public void insertNotice(NoticeDTO dto) {

        validateNotice(dto);

        if (dto.getMemberUid() == null ||
                dto.getMemberUid().isBlank()) {
            dto.setMemberUid("admin");
        }

        dto.setTitle(dto.getTitle().trim());
        dto.setContent(dto.getContent().trim());

        int result = noticeDAO.insertNotice(dto);

        if (result == 0) {
            throw new IllegalStateException(
                    "공지사항 등록에 실패했습니다."
            );
        }
    }


    /* =========================================================
       수정
       ========================================================= */

    @Transactional
    public void updateNotice(NoticeDTO dto) {

        if (dto == null || dto.getBoardNo() <= 0) {
            throw new IllegalArgumentException(
                    "공지사항 번호가 올바르지 않습니다."
            );
        }

        validateNotice(dto);

        dto.setTitle(dto.getTitle().trim());
        dto.setContent(dto.getContent().trim());

        int result = noticeDAO.updateNotice(dto);

        if (result == 0) {
            throw new IllegalArgumentException(
                    "수정할 공지사항이 없습니다."
            );
        }
    }


    /* =========================================================
       삭제
       ========================================================= */

    @Transactional
    public void deleteNotices(
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

        noticeDAO.deleteNotices(
                validBoardNoList
        );
    }


    /* =========================================================
       검증 및 검색값 정리
       ========================================================= */

    private void validateNotice(NoticeDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException(
                    "공지사항 정보가 없습니다."
            );
        }

        if (dto.getBoardType() == null ||
                !dto.getBoardType().startsWith("notice/")) {
            throw new IllegalArgumentException(
                    "공지사항 유형을 선택해주세요."
            );
        }

        if (dto.getTitle() == null ||
                dto.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "공지사항 제목을 입력해주세요."
            );
        }

        if (dto.getContent() == null ||
                dto.getContent().isBlank()) {
            throw new IllegalArgumentException(
                    "공지사항 내용을 입력해주세요."
            );
        }
    }

    /**
     * 화면에서는 고객서비스가 넘어오고,
     * DB에는 notice/고객서비스로 저장된다.
     */
    private String normalizeType(String type) {

        if (type == null || type.isBlank()) {
            return null;
        }

        String trimmedType = type.trim();

        if (trimmedType.startsWith("notice/")) {
            return trimmedType.substring(
                    "notice/".length()
            );
        }

        return trimmedType;
    }
}