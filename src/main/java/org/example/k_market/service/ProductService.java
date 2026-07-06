package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.repository.category.CategoryRepository;
import org.example.k_market.repository.product.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductNoticeValueRepository noticeValueRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductVariantRepository variantRepository;
    private final ProductVariantItemRepository variantItemRepository;
}
