package org.example.k_market.repository.coupon;

import org.example.k_market.entity.coupon.Coupon_issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Coupon_issueRepository extends JpaRepository<Coupon_issue, Integer> {
}
