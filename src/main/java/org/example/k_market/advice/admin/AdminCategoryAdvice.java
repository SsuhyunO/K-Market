package org.example.k_market.advice.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.example.k_market.common.admin.AdminCategory;
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
