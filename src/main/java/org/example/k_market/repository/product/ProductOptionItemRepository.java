package org.example.k_market.repository.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionItemRepository extends JpaRepository<ProductOptionItemRepository, Integer> {
}
