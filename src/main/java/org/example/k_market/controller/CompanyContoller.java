package org.example.k_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompanyContoller {
    @GetMapping("/company/index")
    public String index(){
        return "/company/index";
    }

}
