package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.dto.shop.SalesStatusDTO;

import java.util.List;
import java.util.Map;

@Mapper
public interface SalesDAO {
    List<SalesStatusDTO> selectSalesStatusList(Map<String, Object> params);
    int countSalesStatus(Map<String, Object> params);
}