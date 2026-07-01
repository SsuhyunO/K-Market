package org.example.k_market.controller.admin.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("memberListController")
@RequestMapping("/admin")
public class ListController {

    @GetMapping("/member-list")
    public String list() {
        return "admin/member/list";
    }
}
