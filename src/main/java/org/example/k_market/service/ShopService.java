package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.ShopDto;
import org.example.k_market.entity.Member;
import org.example.k_market.entity.Seller;
import org.example.k_market.repository.MemberRepository;
import org.example.k_market.repository.SellerRepository;
import org.example.k_market.util.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private static final int PAGE_SIZE = 10;

    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 상점목록 조회 (페이지네이션 포함)
    public ShopDto.ListResult getShopList(int page) {
        int pageIndex = Math.max(page - 1, 0);
        Pageable pageable = PageRequest.of(pageIndex, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "uid"));

        Page<Seller> sellerPage = sellerRepository.findAll(pageable);
        List<Seller> sellers = sellerPage.getContent();
        int totalCount = (int) sellerPage.getTotalElements();

        List<ShopDto.ListItem> items = new ArrayList<>();
        for (int i = 0; i < sellers.size(); i++) {
            Seller seller = sellers.get(i);
            Member member = memberRepository.findById(seller.getUid()).orElse(null);
            String ceoName = (member != null) ? member.getName() : "";
            int no = totalCount - (pageIndex * PAGE_SIZE) - i;
            items.add(ShopDto.ListItem.from(no, seller, ceoName));
        }

        PageInfo pageInfo = new PageInfo(page, totalCount, PAGE_SIZE, 5);

        return new ShopDto.ListResult(items, pageInfo);
    }

    // 상점등록 (member + seller 동시 생성)
    @Transactional
    public void registerShop(ShopDto.RegisterRequest request, String regIp) {

        if (memberRepository.existsByUid(request.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (sellerRepository.existsByBizRegNo(request.getBusinessRegistrationNumber())) {
            throw new IllegalArgumentException("이미 등록된 사업자등록번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = request.toMemberEntity(encodedPassword, regIp);
        memberRepository.save(member);

        Seller seller = request.toSellerEntity();
        sellerRepository.save(seller);
    }

    // 상태변경 (승인/중단/재개) - 즉시 DB 반영
    @Transactional
    public void changeStatus(String uid, String status) {
        Seller seller = sellerRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("상점 정보가 없습니다."));

        seller.changeStatus(status);
    }
}