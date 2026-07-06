package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Integer> {
}
