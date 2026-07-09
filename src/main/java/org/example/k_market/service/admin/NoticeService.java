package org.example.k_market.service.admin;


import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.NoticeDAO;
import org.example.k_market.dto.common.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.example.k_market.dto.admin.NoticeDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private static final int PAGE_SIZE = 10;

    private final NoticeDAO noticeDAO;

    public List<NoticeDTO> getNoticeList(int page) {
        int offset = (page - 1) * PAGE_SIZE;
        return noticeDAO.getNoticeList(offset, PAGE_SIZE);
    }

    public PageInfo getPageInfo(int page) {
        int totalCount = noticeDAO.getTotalCount();
        Page<NoticeDTO> pageResult = new PageImpl<>(
                List.of(),
                PageRequest.of(page - 1, PAGE_SIZE),
                totalCount
        );
        return new PageInfo(pageResult);
    }

    public NoticeDTO getNotice(int boardNo) {
        return noticeDAO.getNoticeByNo(boardNo);
    }

    public void insertNotice(NoticeDTO dto) {
        noticeDAO.insertNotice(dto);
    }

    public void updateNotice(NoticeDTO dto) {
        noticeDAO.updateNotice(dto);
    }

    public void deleteNotices(List<Integer> boardNoList) {
        noticeDAO.deleteNotices(boardNoList);
    }
}