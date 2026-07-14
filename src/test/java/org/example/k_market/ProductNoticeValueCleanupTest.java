package org.example.k_market;

import lombok.extern.slf4j.Slf4j;
import org.example.k_market.common.product.ProductCommonNoticeKeys;
import org.example.k_market.entity.product.Product;
import org.example.k_market.repository.product.ProductNoticeValueRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test",
    "spring.security.oauth2.client.registration.google.client-secret=test"
})
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class ProductNoticeValueCleanupTest {

    private static final List<String> COMMON_NOTICE_KEYS = new ArrayList<>(ProductCommonNoticeKeys.all());

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductNoticeValueRepository noticeValueRepository;

    @Test
    @Transactional
    @Commit
    void removeNoticeValuesDuplicatedWithCommonProductNoticeFromAllProducts() {
        List<Product> products = productRepository.findAll();
        int deletedTotal = 0;

        for (Product product : products) {
            int deleted = noticeValueRepository.deleteByProdNoAndNoticeKeyIn(
                product.getProdNo(),
                COMMON_NOTICE_KEYS
            );
            deletedTotal += deleted;

            if (deleted > 0) {
                log.info(
                    "prodNo={} removed duplicated product_notice_value count={}",
                    product.getProdNo(),
                    deleted
                );
            }
        }

        log.info(
            "products={} removed duplicated product_notice_value total={}",
            products.size(),
            deletedTotal
        );

        assertThat(noticeValueRepository.countByIdNoticeKeyIn(COMMON_NOTICE_KEYS)).isZero();
    }
}
