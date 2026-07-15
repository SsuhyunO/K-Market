package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatDTO {
    private long totalOrderCount;
    private int todayOrderCount;
    private int yesterdayOrderCount;
    private long totalOrderAmount;
    private long todayOrderAmount;
    private long yesterdayOrderAmount;
}
