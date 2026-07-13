package org.example.k_market;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.entity.order.Order;
import org.example.k_market.entity.product.Product;
import org.example.k_market.entity.product.ProductVariant;
import org.example.k_market.repository.order.OrderItemRepository;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.repository.product.ProductVariantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(properties = {
    "spring.security.oauth2.client.registration.google.client-id=test",
    "spring.security.oauth2.client.registration.google.client-secret=test"
})
@EnabledIfEnvironmentVariable(named = "DB_URL", matches = ".+")
class ProductOrderReferenceTest {

    private static final int PROD_NO = 6;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void product6OrderReferenceCheck() {
        Product product = productRepository.findById(PROD_NO)
            .orElseThrow(() -> new AssertionError("prodNo=6 상품이 없습니다."));

        List<Integer> variantIds = productVariantRepository.findByProdNoOrderByIdAsc(PROD_NO).stream()
            .map(ProductVariant::getId)
            .toList();
        List<Integer> referencedProdNos = orderItemRepository.findReferencedProdNos(List.of(PROD_NO));

        boolean hasOrderReference = referencedProdNos.contains(PROD_NO);
        List<String> productAssociations = entityManager.getMetamodel()
            .entity(Product.class)
            .getAttributes()
            .stream()
            .filter(Attribute::isAssociation)
            .map(attribute -> attribute.getName() + " -> " + attribute.getJavaType().getSimpleName())
            .sorted()
            .toList();
        boolean productHasDirectOrderAssociation = entityManager.getMetamodel()
            .entity(Product.class)
            .getAttributes()
            .stream()
            .filter(Attribute::isAssociation)
            .anyMatch(attribute -> attribute.getJavaType().equals(Order.class));

        log.info("prodNo={} productName={}", product.getProdNo(), product.getProdName());
        log.info("prodNo={} variantIds={}", PROD_NO, variantIds);
        log.info("prodNo={} hasOrderReference={}", PROD_NO, hasOrderReference);
        log.info("Product JPA associations={}", productAssociations);

        assertThat(product.getProdNo()).isEqualTo(PROD_NO);
        assertThat(productHasDirectOrderAssociation).isFalse();
    }
}
