package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.coupon.CouponDTO;

import java.util.List;

@Mapper
public interface CouponDAO {
    List<CouponDTO> findAll();

    void insert(CouponDTO dto);

    List<CouponDTO> getCouponList(@Param("searchType") String searchType,
                                  @Param("keyword") String keyword,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    int getTotalCount(@Param("searchType") String searchType,
                      @Param("keyword") String keyword);

    void updateStatusToDisabled(@Param("couponNo") int couponNo);

    void disableExpiredCoupons();

    CouponDTO getCouponByNo(@Param("couponNo") int couponNo);
}
