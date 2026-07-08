package org.example.k_market.advice.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    // ===== 추가된 부분: admin 화면(aside.html 등)에서 role별 메뉴 노출 제어에 사용 =====
    @ModelAttribute("loginMemberType")
    public String loginMemberType(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null) ? (String) session.getAttribute("loginMemberType") : null;
    }
}