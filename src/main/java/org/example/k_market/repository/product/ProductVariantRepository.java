package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    List<ProductVariant> findByProdNoOrderByIdAsc(int prodNo);
    List<ProductVariant> findByProdNoIn(List<Integer> prodNos);

    @Modifying
    @Query("delete from ProductVariant pv where pv.prodNo in :prodNos")
    int deleteAllByProdNoIn(@Param("prodNos") List<Integer> prodNos);
}
