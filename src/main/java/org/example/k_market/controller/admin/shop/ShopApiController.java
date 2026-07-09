package org.example.k_market.controller.admin.shop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.ShopDto;
import org.example.k_market.service.ShopService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class ShopApiController {

    private final ShopService shopService;

    // 상점등록 (팝업에서 등록하기 클릭 시)
    @PostMapping
    public String register(@Valid @RequestBody ShopDto.RegisterRequest request, HttpServletRequest httpRequest) {
        String regIp = httpRequest.getRemoteAddr();
        shopService.registerShop(request, regIp);
        return "상점이 등록되었습니다.";
    }

    // 상태변경 (승인/중단/재개) - 버튼 클릭 시 즉시 반영
    @PatchMapping("/{uid}/status")
    public String changeStatus(@PathVariable String uid, @Valid @RequestBody ShopDto.StatusRequest request) {
        shopService.changeStatus(uid, request.getStatus());
        return "상태가 변경되었습니다.";
    }
}