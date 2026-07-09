package org.example.k_market.repository;

import org.example.k_market.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, String> {

    // 사업자등록번호 존재 여부 확인 (중복확인용)
    boolean existsByBizRegNo(String bizRegNo);

    // 상점목록 (최신 등록순)
    List<Seller> findAllByOrderByUidDesc();
}