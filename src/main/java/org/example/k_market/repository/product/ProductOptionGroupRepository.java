package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionGroupRepository extends JpaRepository<ProductOptionGroup, Integer> {
    List<ProductOptionGroup> findByProdNoAndDeletedFalseOrderByIdAsc(int prodNo);
    List<ProductOptionGroup> findByProdNoIn(List<Integer> prodNos);

    @Modifying
    @Query("delete from ProductOptionGroup pog where pog.prodNo in :prodNos")
    int deleteAllByProdNoIn(@Param("prodNos") List<Integer> prodNos);

    @Modifying
    @Query("update ProductOptionGroup pog set pog.name = :name, pog.deleted = false where pog.id = :id and pog.prodNo = :prodNo")
    int updateNameByIdAndProdNo(@Param("id") int id, @Param("prodNo") int prodNo, @Param("name") String name);

    @Modifying
    @Query("update ProductOptionGroup pog set pog.deleted = true where pog.id in :ids")
    int markDeletedByIdIn(@Param("ids") List<Integer> ids);

    @Modifying
    @Query("update ProductOptionGroup pog set pog.deleted = true where pog.prodNo in :prodNos")
    int markDeletedByProdNoIn(@Param("prodNos") List<Integer> prodNos);
}
