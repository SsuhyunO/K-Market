package org.example.k_market.repository.product;

import org.example.k_market.entity.category.Category;
import org.example.k_market.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("""
        select p
        from Product p
        where p.status = 'ON_SALE'
            and exists (
                select 1
                from ProductVariant pv
                where pv.prodNo = p.prodNo
                    and pv.status = 'ON_SALE'
            )
        order by p.sold desc, p.prodNo desc
    """)
    List<Product> findBestProducts(Pageable pageable);

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

    @Modifying
    @Query("""
        update Product p
        set p.status = :status
        where p.prodNo in :prodNos
    """)
    int updateStatusByProdNoIn(
        @Param("prodNos") List<Integer> prodNos,
        @Param("status") String status
    );

    @Modifying
    @Query("""
        update Product p
        set p.category = :category,
            p.prodName = :prodName,
            p.description = :description,
            p.maker = :maker,
            p.deliveryFee = :deliveryFee,
            p.price = :price,
            p.discount = :discount,
            p.point = :point,
            p.thumb1FileId = :thumb1FileId,
            p.thumb2FileId = :thumb2FileId,
            p.thumb3FileId = :thumb3FileId,
            p.detailInfoFileId = :detailInfoFileId,
            p.infoNoticeType = :infoNoticeType
        where p.prodNo = :prodNo
    """)
    int updateProductForModify(
        @Param("prodNo") int prodNo,
        @Param("category") Category category,
        @Param("prodName") String prodName,
        @Param("description") String description,
        @Param("maker") String maker,
        @Param("deliveryFee") int deliveryFee,
        @Param("price") int price,
        @Param("discount") int discount,
        @Param("point") int point,
        @Param("thumb1FileId") Integer thumb1FileId,
        @Param("thumb2FileId") Integer thumb2FileId,
        @Param("thumb3FileId") Integer thumb3FileId,
        @Param("detailInfoFileId") Integer detailInfoFileId,
        @Param("infoNoticeType") String infoNoticeType
    );

    @Modifying
    @Query("update Product p set p.hit = p.hit + 1 where p.prodNo = :prodNo")
    int increaseHit(@Param("prodNo") int prodNo);
}
