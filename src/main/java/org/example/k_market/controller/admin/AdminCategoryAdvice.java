package org.example.k_market.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import org.example.k_market.dto.AdminCategoryDTO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "org.example.k_market.controller.admin")
public class AdminCategoryAdvice {

    @ModelAttribute("category")
    public AdminCategoryDTO category(HttpServletRequest request) {
        return AdminCategoryDTO.fromPath(request.getServletPath())
                .orElse(AdminCategoryDTO.MAIN);
    }
}
