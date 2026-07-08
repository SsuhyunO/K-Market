package org.example.k_market.config;

import org.example.k_market.interceptor.AdminRoleInterceptor;
import org.example.k_market.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/product/order", "/product/cart", "/my/**");

        // ===== 추가된 부분: admin 페이지 역할(role) 기반 접근 제어 =====
        registry.addInterceptor(new AdminRoleInterceptor())
                .addPathPatterns("/admin/**");
    }
}