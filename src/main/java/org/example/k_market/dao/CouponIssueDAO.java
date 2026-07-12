package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.coupon.CouponIssueDTO;

import java.util.List;

@Mapper
public interface CouponIssueDAO {
    List<CouponIssueDTO> getCouponIssueList(@Param("searchType") String searchType,
                                            @Param("keyword") String keyword,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    int getTotalCount(@Param("searchType") String searchType,
                      @Param("keyword") String keyword);

    CouponIssueDTO getCouponIssueByNo(@Param("issueNo") int issueNo);

    void stopIssuesByCouponNo(int couponNo);
    int stopCouponIssue(@Param("issueNo") int issueNo, @Param("status") int status);

    List<CouponIssueDTO> getAvailableCouponsByMemberUid(@Param("memberUid") String memberUid,
                                                        @Param("sellerUidList") List<String> sellerUidList
                                                    );

    void expireIssuesByExpiredCoupons();
}
