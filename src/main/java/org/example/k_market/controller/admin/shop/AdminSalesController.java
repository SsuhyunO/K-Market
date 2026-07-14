package org.example.k_market.controller.admin.shop;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.shop.SalesSearchRequest;
import org.example.k_market.dto.shop.SalesStatusResult;
import org.example.k_market.service.shop.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminSalesController")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminSalesController {

    private final SalesService salesService;

    // 실제 브라우저 접속 경로: /admin/sales-status
    // (기존에는 /admin/shop/sales 로 매핑되어 있어서, 실제 접속 경로를 처리하던
    //  더미 SalesController.java 가 대신 요청을 가로채고 있었음 -> 데이터 미조회 원인)
    @GetMapping("/sales-status")
    public String salesStatus(SalesSearchRequest request, Model model) {
        SalesStatusResult result = salesService.getSalesStatus(request);
        model.addAttribute("result", result);
        model.addAttribute("period", request.getPeriod());
        return "admin/shop/sales";
    }
}