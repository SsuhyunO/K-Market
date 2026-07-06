package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.SellerDto;
import org.example.k_market.entity.Seller;
import org.example.k_market.repository.SellerRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    // 사업자등록번호 중복확인
    public boolean isBizRegNoDuplicate(String bizRegNo) {
        return sellerRepository.existsByBizRegNo(bizRegNo);
    }

    // 판매자 등록 (이미 회원가입 된 uid 기준으로 판매자 정보 추가)
    public void registerSeller(SellerDto.RegisterRequest request, String uid) {

        if (sellerRepository.existsByBizRegNo(request.getBizRegNo())) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        Seller seller = request.toEntity(uid);
        sellerRepository.save(seller);
    }

    // uid로 판매자 정보 조회
    public Seller findByUid(String uid) {
        return sellerRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보가 없습니다."));
    }
}