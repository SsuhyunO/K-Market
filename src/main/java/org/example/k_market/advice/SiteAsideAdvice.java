package org.example.k_market.advice;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.category.CategoryTreeDTO;
import org.example.k_market.dto.product.response.BestProductResponse;
import org.example.k_market.service.CategoryService;
import org.example.k_market.service.product.ProductService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class SiteAsideAdvice {
    private final CategoryService categoryService;
    private final ProductService productService;

    @ModelAttribute("categoryTree")
    public List<CategoryTreeDTO> categoryTree(HttpServletRequest request) {
        if (shouldSkip(request)) {
            return List.of();
        }
        return categoryService.getCategoryTree();
    }

    @ModelAttribute("bestProducts")
    public List<BestProductResponse> bestProducts(HttpServletRequest request) {
        if (shouldSkip(request)) {
            return List.of();
        }
        return productService.getBestProducts();
    }

    private boolean shouldSkip(HttpServletRequest request) {
        if (request.getDispatcherType() == DispatcherType.ERROR) {
            return true;
        }

        String path = getRequestPath(request);
        return path.startsWith("/api/")
                || path.startsWith("/files/")
                || path.equals("/admin")
                || path.startsWith("/admin/");
    }

    private String getRequestPath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        if (!contextPath.isBlank() && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }

        return requestUri;
    }
}
