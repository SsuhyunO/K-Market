package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.coupon.CouponDTO;

import java.util.List;

@Mapper
public interface CouponDAO {
    List<CouponDTO> findAll();

    void insert(CouponDTO dto);

    int getTotalCount();

    List<CouponDTO> getCouponList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    void updateStatusToDisabled(@Param("couponNo") int couponNo);
}
