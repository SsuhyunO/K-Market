package org.example.k_market.repository.coupon;

import org.example.k_market.entity.coupon.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    List<Coupon> findAllByOrderByCouponNoDesc();

    // 마이페이지 쿠폰함: 회원이 보유한 쿠폰의 couponNo 목록으로 상세 정보 조회
    List<Coupon> findByCouponNoIn(List<Integer> couponNos);
}