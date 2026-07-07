package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.entity.product.Product;
import org.example.k_market.repository.product.ProductRepository;
import org.example.k_market.service.product.ProductFileUploader;
import org.example.k_market.service.product.ProductFormValidator;
import org.example.k_market.service.product.ProductNoticeWriter;
import org.example.k_market.service.product.ProductOptionWriter;
import org.example.k_market.service.product.ProductUploadedFiles;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    /*
     * 상품 옵션 삭제 정책
     *
     * 1. 상품 등록 중 추가/삭제된 옵션, 옵션 항목, 조합은 아직 DB에 저장되지 않은 임시 상태이므로
     *    최종 제출 시점에 남아있는 현재 상태만 저장한다.
     *
     * 2. 상품 수정 중 기존 옵션, 옵션 항목, 조합을 제거하는 경우에는 참조 여부로 삭제 방식을 나눈다.
     *    - 장바구니 또는 주문에서 참조 중이면 상태값으로 soft delete 처리한다.
     *    - 어디에서도 참조하지 않으면 hard delete 처리한다.
     *
     * 3. 삭제 판단은 장바구니/주문이 직접 참조할 가능성이 높은 ProductVariant를 기준으로 먼저 판단하고,
     *    이후 관련 ProductOptionItem, ProductOptionGroup 삭제 방식을 결정한다.
     *
     * 4. 옵션이 없는 상품은 가짜 기본 옵션 그룹/항목을 만들지 않고 기본 ProductVariant 1개만 생성한다.
     */
    private final ProductRepository productRepository;
    private final ProductFormValidator productFormValidator;
    private final ProductFileUploader productFileUploader;
    private final ProductOptionWriter productOptionWriter;
    private final ProductNoticeWriter productNoticeWriter;

    @Transactional
    public void register(ProductRegisterRequest request, String sellerUid) {
        Category category = productFormValidator.validateForSave(request, sellerUid);
        ProductUploadedFiles files = productFileUploader.uploadRequiredFiles(request);

        Product product = productRepository.save(Product.builder()
            .category(category)
            .prodName(request.getProdName())
            .price(request.getPrice())
            .discount(request.getDiscount())
            .point(request.getPoint())
            .sold(0)
            .thumb1FileId(files.thumb1FileId())
            .thumb2FileId(files.thumb2FileId())
            .thumb3FileId(files.thumb3FileId())
            .detailInfoFileId(files.detailInfoFileId())
            .sellerUid(sellerUid)
            .infoNoticeType(request.getInfoNoticeType())
            .build());

        productOptionWriter.save(product.getProdNo(), request.getOptionGroups(), request.getVariants());
        productNoticeWriter.save(product.getProdNo(), request.getInformationNoticeValues());
    }

    @Transactional
    public void getProductList() {

    }
}
