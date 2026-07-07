package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.entity.product.ProductVariantItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantItemRepository extends JpaRepository<ProductVariantItem, ProductVariantItemId> {
}
