package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ManagementProductListResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.dto.product.response.ProductSearchResponse;

import java.util.List;

@Mapper
public interface ProductDAO {
    List<ManagementProductListResponse> findProductsForManagement(
        @Param("size") int size,
        @Param("offset") int offset,
        @Param("seller") String seller,
        @Param("role") String role,
        @Param("type") String type,
        @Param("keyword") String keyword,
        @Param("uncategorizedOnly") boolean uncategorizedOnly);
    int totalCountForManagement(
        @Param("seller") String seller,
        @Param("role") String role,
        @Param("type") String type,
        @Param("keyword") String keyword,
        @Param("uncategorizedOnly") boolean uncategorizedOnly);
    ProductDetailResponse findProductDetail(@Param("prodNo") int prodNo);
    List<ProductListResponse> findProductsForCustomer(
        @Param("size") int size,
        @Param("offset") int offset,
        @Param("cateId") int cateId,
        @Param("type") String type
    );
    int totalCountForCustomer(
        @Param("cateId") int cateId
    );
    List<ProductSearchResponse> findProductsForSearch(
        @Param("size") int size,
        @Param("offset") int offset,
        @Param("type") String type,
        @Param("keyword") String keyword,
        @Param("name") boolean name,
        @Param("description") boolean description,
        @Param("price") boolean price,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
    int totalCountForSearch(
        @Param("keyword") String keyword,
        @Param("name") boolean name,
        @Param("description") boolean description,
        @Param("price") boolean price,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice
    );
    List<ProductListResponse> findProductsForMain(
        @Param("size") int size,
        @Param("type") String type
    );
}
