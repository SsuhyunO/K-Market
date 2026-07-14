package org.example.k_market.repository;

import org.example.k_market.entity.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Integer> {
    List<Point> findByMemberUid(String memberUid, Pageable pageable);

    int countByMemberUid(String memberUid);

    List<Point> findByMemberUidAndCreatedAtBetween(
        String memberUid,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    );

    int countByMemberUidAndCreatedAtBetween(
        String memberUid,
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}
