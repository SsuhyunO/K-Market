package org.example.k_market.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping({"/admin/main", "/"})
    public String main() {
        return "admin/main";
    }
}
