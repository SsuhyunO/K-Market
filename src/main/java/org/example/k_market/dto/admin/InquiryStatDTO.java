package org.example.k_market.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryStatDTO {
    private long totalInquiryCount;
    private int todayInquiryCount;
    private int yesterdayInquiryCount;
}
