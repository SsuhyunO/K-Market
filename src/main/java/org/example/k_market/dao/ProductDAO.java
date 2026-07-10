package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ManagementProductListResponse;

import java.util.List;

@Mapper
public interface ProductDAO {
    List<ManagementProductListResponse> findProductsForManagement(
        @Param("size") int size,
        @Param("offset") int offset,
        @Param("seller") String seller,
        @Param("role") String role,
        @Param("type") String type,
        @Param("keyword") String keyword);
    ProductDetailResponse findProductDetail(@Param("prodNo") int prodNo);
}
