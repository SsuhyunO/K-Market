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

    public List<NoticeDTO> getNoticeList(int page) {

        int safePage = Math.max(page, 1);
        int offset = (safePage - 1) * PAGE_SIZE;

        return noticeDAO.getNoticeList(offset, PAGE_SIZE);
    }

    public PageInfo getPageInfo(int page) {

        int safePage = Math.max(page, 1);
        int totalCount = noticeDAO.getTotalCount();

        Page<NoticeDTO> pageResult = new PageImpl<>(
                List.of(),
                PageRequest.of(safePage - 1, PAGE_SIZE),
                totalCount
        );

        return new PageInfo(pageResult);
    }

    public NoticeDTO getNotice(int boardNo) {

        if (boardNo <= 0) {
            throw new IllegalArgumentException(
                    "공지사항 번호가 올바르지 않습니다."
            );
        }

        NoticeDTO notice = noticeDAO.getNoticeByNo(boardNo);

        if (notice == null) {
            throw new IllegalArgumentException(
                    "존재하지 않는 공지사항입니다."
            );
        }

        return notice;
    }

    @Transactional
    public void insertNotice(NoticeDTO dto) {

        validateNotice(dto);

        /*
         * 임시값.
         * 나중에는 로그인 관리자 UID로 교체해야 한다.
         */
        if (dto.getMemberUid() == null ||
                dto.getMemberUid().isBlank()) {
            dto.setMemberUid("admin");
        }

        noticeDAO.insertNotice(dto);
    }

    @Transactional
    public void updateNotice(NoticeDTO dto) {

        if (dto.getBoardNo() <= 0) {
            throw new IllegalArgumentException(
                    "공지사항 번호가 올바르지 않습니다."
            );
        }

        validateNotice(dto);

        int affectedRows = noticeDAO.updateNotice(dto);

        if (affectedRows == 0) {
            throw new IllegalArgumentException(
                    "수정할 공지사항이 없습니다."
            );
        }
    }

    @Transactional
    public void deleteNotices(List<Integer> boardNoList) {

        if (boardNoList == null || boardNoList.isEmpty()) {
            return;
        }

        noticeDAO.deleteNotices(boardNoList);
    }

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
}