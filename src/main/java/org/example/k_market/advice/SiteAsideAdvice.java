package org.example.k_market.advice;

import lombok.RequiredArgsConstructor;
import org.example.k_market.controller.HomeController;
import org.example.k_market.controller.product.ProductController;
import org.example.k_market.dto.product.response.BestProductResponse;
import org.example.k_market.service.ProductService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(assignableTypes = {
    HomeController.class,
    ProductController.class
})
@RequiredArgsConstructor
public class SiteAsideAdvice {
    private final ProductService productService;

    @ModelAttribute("bestProducts")
    public List<BestProductResponse> bestProducts() {
        return productService.getBestProducts();
    }
}
