package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductNoticeValueId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductNoticeValueRepository extends JpaRepository<ProductNoticeValue, ProductNoticeValueId> {
    List<ProductNoticeValue> findByIdProdNo(int prodNo);

    @Modifying
    @Query("delete from ProductNoticeValue pnv where pnv.id.prodNo in :prodNos")
    int deleteAllByProdNoIn(@Param("prodNos") List<Integer> prodNos);

    @Modifying
    @Query("""
        delete from ProductNoticeValue pnv
        where pnv.id.prodNo = :prodNo
            and pnv.id.noticeKey in :noticeKeys
    """)
    int deleteByProdNoAndNoticeKeyIn(
        @Param("prodNo") int prodNo,
        @Param("noticeKeys") List<String> noticeKeys
    );

    long countByIdNoticeKeyIn(List<String> noticeKeys);
}
