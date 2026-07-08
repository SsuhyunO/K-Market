package org.example.k_market.repository.coupon;

import org.example.k_market.entity.coupon.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Coupon_issueRepository extends JpaRepository<CouponIssue, Integer> {
}
