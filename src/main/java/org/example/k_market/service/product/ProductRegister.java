package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.entity.category.Category;
import org.example.k_market.entity.product.Product;
import org.example.k_market.repository.product.ProductRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRegister {
    private final ProductRepository productRepository;
    private final ProductFormValidator productFormValidator;
    private final ProductFileUploader productFileUploader;
    private final ProductOptionWriter productOptionWriter;
    private final ProductNoticeWriter productNoticeWriter;

    public void register(ProductRegisterRequest request, String sellerUid) {
        Category category = productFormValidator.validateForSave(request, sellerUid);
        ProductUploadedFiles files = productFileUploader.uploadRequiredFiles(request);

        Product product = productRepository.save(Product.builder()
            .category(category)
            .prodName(request.getProdName())
            .description(request.getDescription())
            .maker(request.getMaker())
            .deliveryFee(request.getDeliveryFee())
            .price(request.getPrice())
            .discount(request.getDiscount())
            .point(request.getPoint())
            .taxType(request.getTaxType())
            .receiptIssueType(request.getReceiptIssueType())
            .businessType(request.getBusinessType())
            .brand(request.getBrand())
            .origin(request.getOrigin())
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
}
