package org.example.k_market.repository.product;

import org.example.k_market.entity.product.ProductOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionItemRepository extends JpaRepository<ProductOptionItem, Integer> {
    List<ProductOptionItem> findByGroupIdInAndDeletedFalseOrderByIdAsc(List<Integer> groupIds);

    @Modifying
    @Query("delete from ProductOptionItem poi where poi.groupId in :groupIds")
    int deleteAllByGroupIdIn(@Param("groupIds") List<Integer> groupIds);
}
