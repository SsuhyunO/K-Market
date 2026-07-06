package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
}
