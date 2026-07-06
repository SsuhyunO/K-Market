package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductNoticeValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductNoticeValueRepository extends JpaRepository<ProductNoticeValue, ProductNoticeValueId> {
}
