package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.admin.*;

import java.util.List;

@Mapper
public interface AdminMainDAO {
    OperationCardDTO selectOperationCard(@Param("sellerUid") String sellerUid);
    OrderStatDTO selectOrderStat(@Param("sellerUid") String sellerUid);
    MemberStatDTO selectMemberStat();
    Long selectVisitStat(@Param("sellerUid") String sellerUid);
    InquiryStatDTO selectInquiryStat();
    List<BoardListItemDTO> selectRecentNotices();
    List<BoardListItemDTO> selectRecentInquiries();
    List<CategorySalesDTO> selectCategorySalesDaily(@Param("sellerUid") String sellerUid);
    List<CategorySalesTotalDTO> selectCategorySalesTotal(@Param("sellerUid") String sellerUid);
}
