package org.example.k_market.repository.product;

import org.example.k_market.entity.category.Category;
import org.example.k_market.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Modifying
    @Query("""
        update Product p
        set p.category = :uncategorized
        where p.category.cateId in :deletedCategoryIds
    """)
    int moveProductsToUncategorized(
        @Param("deletedCategoryIds") List<Integer> deletedCategoryIds,
        @Param("uncategorized") Category uncategorized
    );
}
