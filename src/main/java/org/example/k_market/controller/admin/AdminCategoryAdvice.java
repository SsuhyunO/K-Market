package org.example.k_market.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.example.k_market.dto.AdminCategory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "org.example.k_market.controller.admin")
public class AdminCategoryAdvice {

    @ModelAttribute("category")
    public AdminCategory category(HttpServletRequest request) {
        return AdminCategory.fromPath(request.getServletPath())
                .orElse(AdminCategory.MAIN);
    }
}
