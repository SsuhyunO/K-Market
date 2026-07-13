package org.example.k_market.repository.cart;

import org.example.k_market.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByMemberUidOrderByCartNoDesc(String memberUid);

    List<Cart> findByMemberUidAndCartNoIn(String memberUid, List<Integer> cartNos);

    java.util.Optional<Cart> findByMemberUidAndProdVariantId(String memberUid, int prodVariantId);

    @Query("select distinct c.prodVariantId from Cart c where c.prodVariantId in :variantIds")
    List<Integer> findReferencedVariantIds(@Param("variantIds") List<Integer> variantIds);

    @Modifying
    @Query("delete from Cart c where c.prodVariantId in :variantIds")
    int deleteByProdVariantIdIn(@Param("variantIds") List<Integer> variantIds);

    @Modifying
    @Query("delete from Cart c where c.memberUid = :memberUid and c.cartNo in :cartNos")
    int deleteByCartNoInAndMemberUid(
        @Param("cartNos") List<Integer> cartNos,
        @Param("memberUid") String memberUid
    );
}
