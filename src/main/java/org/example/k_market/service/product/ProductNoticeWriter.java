package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.common.product.ProductCommonNoticeKeys;
import org.example.k_market.entity.product.ProductNoticeValue;
import org.example.k_market.entity.product.ProductNoticeValueId;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductNoticeWriter {
    private final ProductNoticeValueRepository noticeValueRepository;

    public void save(int prodNo, Map<String, String> noticeValues) {
        if (noticeValues == null || noticeValues.isEmpty()) {
            return;
        }

        noticeValues.entrySet().stream()
            .filter(entry -> !ProductCommonNoticeKeys.contains(entry.getKey()))
            .forEach(entry -> noticeValueRepository.save(ProductNoticeValue.builder()
                .id(new ProductNoticeValueId(entry.getKey(), prodNo))
                .value(entry.getValue())
                .build()));
    }
}
