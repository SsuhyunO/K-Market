package org.example.k_market.controller.admin.order;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminOrderController")
@RequestMapping("/admin/order")
public class OrderController {

    @GetMapping("/order-list")
    public String orderList() {
        return "admin/order/order-list";
    }

    @GetMapping("/delivery-list")
    public String deliveryList() {
        return "admin/order/delivery-list";
    }

    @GetMapping("/claim-list")
    public String claimList() {
        return "admin/order/claim-list";
    }
}
