package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.AdminMainDAO;
import org.example.k_market.dto.admin.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMainService {
    private final AdminMainDAO adminMainDAO;

    public OperationCardDTO getOperationCard(String loginMemberType, String memberUid) {
        String sellerUid = "SELLER".equals(loginMemberType) ? memberUid : null;
        return adminMainDAO.selectOperationCard(sellerUid);
    }

    public OrderStatDTO getOrderStat(String loginMemberType, String memberUid) {
        String sellerUid = "SELLER".equals(loginMemberType) ? memberUid : null;
        return adminMainDAO.selectOrderStat(sellerUid);
    }

    public MemberStatDTO getMemberStat(String loginMemberType) {
        if (!"ADMIN".equals(loginMemberType)) {
            return null; // SELLER는 회원가입 통계 필요 없음
        }
        return adminMainDAO.selectMemberStat();
    }

    public long getVisitStat(String loginMemberType, String memberUid) {
        String sellerUid = "SELLER".equals(loginMemberType) ? memberUid : null;
        Long result = adminMainDAO.selectVisitStat(sellerUid);
        return result != null ? result : 0L;
    }

    public InquiryStatDTO getInquiryStat(String loginMemberType) {
        if (!"ADMIN".equals(loginMemberType)) {
            return new InquiryStatDTO();
        }
        InquiryStatDTO result = adminMainDAO.selectInquiryStat();
        return result != null ? result : new InquiryStatDTO();
    }

    public List<BoardListItemDTO> getRecentNotices(String loginMemberType) {
        if (!"ADMIN".equals(loginMemberType)) {
            return Collections.emptyList();
        }
        return adminMainDAO.selectRecentNotices();
    }

    public List<BoardListItemDTO> getRecentInquiries(String loginMemberType) {
        if (!"ADMIN".equals(loginMemberType)) {
            return Collections.emptyList();
        }
        return adminMainDAO.selectRecentInquiries();
    }

    public List<CategorySalesDTO> getCategorySalesDaily(String loginMemberType, String memberUid) {
        String sellerUid = "SELLER".equals(loginMemberType) ? memberUid : null;
        return adminMainDAO.selectCategorySalesDaily(sellerUid);
    }

    public List<CategorySalesTotalDTO> getCategorySalesTotal(String loginMemberType, String memberUid) {
        String sellerUid = "SELLER".equals(loginMemberType) ? memberUid : null;
        return adminMainDAO.selectCategorySalesTotal(sellerUid);
    }

}
