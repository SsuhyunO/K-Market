package org.example.k_market.repository;

import org.example.k_market.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @EntityGraph(attributePaths = {"product", "member"})
    List<Review> findByMemberUid(String memberUid, Pageable pageable);

    int countByMemberUid(String memberUid);

    @EntityGraph(attributePaths = {"product", "member"})
    List<Review> findByProductProdNo(int productNo, Pageable pageable);

    int countByProductProdNo(int productNo);
}
