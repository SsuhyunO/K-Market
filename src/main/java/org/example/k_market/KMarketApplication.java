package org.example.k_market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"org.example.k_market"})  // 명시적으로 스캔
@EnableScheduling
public class KMarketApplication {
    public static void main(String[] args) {
        SpringApplication.run(KMarketApplication.class, args);
    }
}