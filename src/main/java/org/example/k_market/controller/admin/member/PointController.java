package org.example.k_market.controller.admin.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class PointController {

    @GetMapping("/management-point")
    public String point() {
        return "admin/member/point";
    }
}
