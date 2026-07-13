package org.example.k_market.service.seller;

import org.example.k_market.dto.seller.response.SellerStatisticsResponse;
import org.example.k_market.enums.seller.SellerGrade;
import org.springframework.stereotype.Component;

@Component
public class SellerGradeCalculator {
    public SellerGrade calculate(SellerStatisticsResponse statistics) {
        if (statistics == null) return SellerGrade.NORMAL;

        if (statistics.getAverageRating() >= 4.5
            && statistics.getReviewCount() >= 50
            && statistics.getSalesCount() >= 100) {

            return SellerGrade.EXCELLENT;
        }

        if (statistics.getAverageRating() >= 4.0
            && statistics.getReviewCount() >= 10
            && statistics.getSalesCount() >= 20) {

            return SellerGrade.GOOD;
        }

        return SellerGrade.NORMAL;
    }
}
