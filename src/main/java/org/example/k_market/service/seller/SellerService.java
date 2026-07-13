package org.example.k_market.service.seller;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.SellerDto;
import org.example.k_market.entity.Member;
import org.example.k_market.entity.Seller;
import org.example.k_market.repository.MemberRepository;
import org.example.k_market.repository.SellerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 사업자등록번호 중복확인
    public boolean isBizRegNoDuplicate(String bizRegNo) {
        return sellerRepository.existsByBizRegNo(bizRegNo);
    }

    // 판매자 회원가입 (member + seller 동시 생성)
    @Transactional
    public void registerSeller(SellerDto.RegisterRequest request, String regIp) {

        if (memberRepository.existsByUid(request.getUid())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (sellerRepository.existsByBizRegNo(request.getBizRegNo())) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = request.toMemberEntity(encodedPassword, regIp);
        memberRepository.save(member);

        Seller seller = request.toSellerEntity();
        sellerRepository.save(seller);
    }

    public Seller findByUid(String uid) {
        return sellerRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("판매자 정보가 없습니다."));
    }
}