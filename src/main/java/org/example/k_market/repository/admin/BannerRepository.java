package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {

    List<Banner> findByBannerTypeOrderByBannerIdDesc(String bannerType);

    List<Banner> findByEnabledTrueOrderByBannerIdDesc();

    List<Banner> findByBannerTypeAndEnabledTrueOrderByBannerIdDesc(String bannerType);
}