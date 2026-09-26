package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.coupon.CouponIssueDTO;

import java.util.List;

@Mapper
public interface CouponIssueDAO {
    List<CouponIssueDTO> getCouponIssueList(@Param("searchType") String searchType,
                                            @Param("keyword") String keyword,
                                            @Param("sellerUidScope") String sellerUidScope,
                                            @Param("offset") int offset,
                                            @Param("pageSize") int pageSize);

    int getTotalCount(@Param("searchType") String searchType,
                      @Param("keyword") String keyword,
                      @Param("sellerUidScope") String sellerUidScope);

    CouponIssueDTO getCouponIssueByNo(@Param("issueNo") int issueNo);

    void stopIssuesByCouponNo(int couponNo);
    int stopCouponIssue(@Param("issueNo") int issueNo, @Param("status") int status);

    List<CouponIssueDTO> getAvailableCouponsByMemberUid(@Param("memberUid") String memberUid,
                                                        @Param("sellerUidList") List<String> sellerUidList
                                                    );

    void expireIssuesByExpiredCoupons();

    // CouponIssueDAO.java (인터페이스)
    int markAsUsed(@Param("issueNo") int issueNo, @Param("status") int status);

    int getMyAvailableCouponCount(String memberUid);

    int issueCouponIfEligible(@Param("couponNo") int couponNo,
                              @Param("memberUid") String memberUid);

    List<CouponIssueDTO> getMyCouponList(@Param("memberUid") String memberUid);
}
