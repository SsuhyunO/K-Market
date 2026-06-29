package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class MemberController {

    @GetMapping("/member-list")
    public String list() {
        return "/admin/member-list";
    }

    @GetMapping("/management-point")
    public String pointManagement() {
        return "/admin/management-point";
    }
}
