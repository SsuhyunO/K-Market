package org.example.k_market.controller.seller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.SellerDto;
import org.example.k_market.service.SellerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
public class SellerApiController {

    private final SellerService sellerService;

    // 사업자등록번호 중복확인 -> true면 이미 등록됨(중복), false면 사용가능
    @GetMapping("/check-bizregno")
    public boolean checkBizRegNo(@RequestParam String bizRegNo) {
        return sellerService.isBizRegNoDuplicate(bizRegNo);
    }

    // 판매자 회원가입 (member + seller 동시 생성)
    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SellerDto.RegisterRequest request, HttpServletRequest httpRequest) {
        String regIp = httpRequest.getRemoteAddr();
        sellerService.registerSeller(request, regIp);
        return "판매자 회원가입이 완료되었습니다.";
    }
}