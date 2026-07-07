package org.example.k_market.repository.category;

import org.example.k_market.entity.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByCode(String code);
    List<Category> findAllByOrderBySortOrderAscCateIdAsc();
    List<Category> findByParentIsNullOrderBySortOrderAscCateIdAsc();
    List<Category> findByParent_CateIdOrderBySortOrderAscCateIdAsc(Integer parentId);
    List<Category> findByParentIsNull();
    List<Category> findByParent_CateId(Integer parentId);
}
