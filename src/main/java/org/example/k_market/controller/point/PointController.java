package org.example.k_market.controller.point;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("myPointController")
@RequestMapping("/my")
public class PointController {

    @GetMapping("/point")
    public String list() {
        return "my/point";
    }
}
