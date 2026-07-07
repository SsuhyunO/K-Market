package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.dto.coupon.CouponDTO;

import java.util.List;

@Mapper
public interface CouponDAO {
    List<CouponDTO> findAll();

    void insert(CouponDTO dto);
}
