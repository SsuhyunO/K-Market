package org.example.k_market.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.example.k_market.common.admin.AdminCategory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        String path = getOriginalPath(request);

        if (path.equals("/admin") || path.startsWith("/admin/")) {
            model.addAttribute("category", AdminCategory.ADMIN_ERROR);
            model.addAttribute("requestedPath", path);
            return "admin/error/404";
        }

        return "error/404";
    }

    private String getOriginalPath(HttpServletRequest request) {
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String path = requestUri == null ? request.getRequestURI() : requestUri.toString();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }

        return path;
    }
}
