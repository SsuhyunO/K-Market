package org.example.k_market.controller.admin.shop;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.seller.ShopDto;
import org.example.k_market.service.ShopService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("shopListController")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ListController {

    private final ShopService shopService;

    @GetMapping("/shop-list")
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        ShopDto.ListResult result = shopService.getShopList(page);
        model.addAttribute("shopList", result.getItems());
        model.addAttribute("pageInfo", result.getPageInfo());
        return "admin/shop/list";
    }
}