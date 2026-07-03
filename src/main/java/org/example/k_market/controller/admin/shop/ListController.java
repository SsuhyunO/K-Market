package org.example.k_market.controller.admin.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("shopListController")
@RequestMapping("/admin")
public class ListController {

    @GetMapping("/shop-list")
    public String list() {

        return "admin/shop/list";
    }
}
