package org.example.k_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.k_market"})  // 명시적으로 스캔
public class KMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(KMarketApplication.class, args);
    }
}