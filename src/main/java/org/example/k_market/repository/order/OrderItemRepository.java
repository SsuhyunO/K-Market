package org.example.k_market.repository.order;

import org.example.k_market.entity.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("""
        select distinct pv.prodNo
        from OrderItem oi, ProductVariant pv
        where oi.prodVariantId = pv.id
            and pv.prodNo in :prodNos
    """)
    List<Integer> findReferencedProdNos(List<Integer> prodNos);

    @Query("select distinct oi.prodVariantId from OrderItem oi where oi.prodVariantId in :variantIds")
    List<Integer> findReferencedVariantIds(List<Integer> variantIds);
}
