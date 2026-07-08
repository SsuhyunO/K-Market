package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.dto.product.ProductDTO;

import java.util.List;

@Mapper
public interface ProductDAO {
    List<ProductDTO> findProductsForPage();
}
