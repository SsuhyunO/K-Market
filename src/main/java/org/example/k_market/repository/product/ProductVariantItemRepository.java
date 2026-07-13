package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductVariantItem;
import org.example.k_market.entity.product.ProductVariantItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantItemRepository extends JpaRepository<ProductVariantItem, ProductVariantItemId> {
    List<ProductVariantItem> findByIdVariantIdIn(List<Integer> variantIds);

    @Modifying
    @Query("delete from ProductVariantItem pvi where pvi.id.variantId in :variantIds")
    int deleteAllByVariantIdIn(@Param("variantIds") List<Integer> variantIds);

    @Modifying
    @Query("""
        delete from ProductVariantItem pvi
        where pvi.id.variantId = :variantId
            and pvi.id.optionItemId in :optionItemIds
    """)
    int deleteByVariantIdAndOptionItemIdIn(
        @Param("variantId") int variantId,
        @Param("optionItemIds") List<Integer> optionItemIds
    );
}
