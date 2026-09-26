package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {

    List<Banner> findByBannerTypeOrderByBannerIdDesc(String bannerType);

    @Query("""
            SELECT b
            FROM Banner b
            WHERE b.enabled = true
              AND (b.startAt IS NULL OR b.startAt <= :now)
              AND (b.endAt IS NULL OR b.endAt >= :now)
            ORDER BY b.bannerId DESC
            """)
    List<Banner> findActiveBanners(@Param("now") LocalDateTime now);

    @Query("""
            SELECT b
            FROM Banner b
            WHERE b.bannerType = :bannerType
              AND b.enabled = true
              AND (b.startAt IS NULL OR b.startAt <= :now)
              AND (b.endAt IS NULL OR b.endAt >= :now)
            ORDER BY b.bannerId DESC
            """)
    List<Banner> findActiveBannersByType(
            @Param("bannerType") String bannerType,
            @Param("now") LocalDateTime now
    );
}
