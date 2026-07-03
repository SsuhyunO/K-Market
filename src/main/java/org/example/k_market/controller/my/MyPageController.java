package org.example.k_market.controller.my;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyPageController {

    @GetMapping("/my/home")
    public String home() {
        return "my/home";
    }

    @GetMapping("/my/order")
    public String order() {
        return "my/order";
    }

    @GetMapping("/my/point")
    public String point() {
        return "my/point";
    }

    @GetMapping("/my/coupon")
    public String coupon() {
        return "my/coupon";
    }

    @GetMapping("/my/review")
    public String review() {
        return "my/review";
    }

    @GetMapping("/my/qna")
    public String qna() {
        return "my/qna";
    }

    @GetMapping("/my/info")
    public String info() {
        return "my/info";
    }

}