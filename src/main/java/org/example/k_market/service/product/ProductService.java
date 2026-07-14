package org.example.k_market.service.product;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ManagementProductSearchCommand;
import org.example.k_market.dto.product.request.ProductListRequest;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.dto.product.request.ProductSearchRequest;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.BestProductResponse;
import org.example.k_market.dto.product.response.ManagementProductListResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.dto.product.response.ProductSearchResponse;
import org.example.k_market.enums.product.MainProductSortType;
import org.example.k_market.repository.product.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    /*
     * 상품/옵션 삭제 정책
     *
     * 1. 장바구니나 주문에서 참조하지 않는 상품은 hard delete 처리한다.
     *    상품 본문, 상품정보 제공고시, 옵션 그룹, 옵션 항목, 옵션 조합, 업로드 파일을 함께 삭제한다.
     *
     * 2. 상품이 장바구니 또는 주문에서 참조 중인지 확인한다.
     *    - 장바구니는 Cart.prodVariantId 기준으로 대상 row를 먼저 삭제한다.
     *    - 주문 참조 여부는 OrderItem.prodVariantId -> ProductVariant.prodNo 기준으로 판단한다.
     *    - 주문에서 참조 중인 상품은 이력 보존을 위해 삭제하지 않고 모든 상품 조합을 판매중지(STOPPED) 처리한다.
     *
     * 3. 상품 등록 중 추가/삭제된 옵션, 옵션 항목, 조합은 아직 DB에 저장되지 않은 임시 상태이므로
     *    최종 제출 시점에 남아있는 현재 상태만 저장한다.
     *
     * 4. 상품 수정 중 기존 옵션, 옵션 항목, 조합을 제거하는 경우에는 참조 여부로 삭제 방식을 나눈다.
     *    - 장바구니 또는 주문에서 참조 중이면 상태값으로 soft delete 처리한다.
     *    - 어디에서도 참조하지 않으면 hard delete 처리한다.
     *
     * 5. 옵션 삭제 판단은 장바구니/주문이 직접 참조할 가능성이 높은 ProductVariant를 기준으로 먼저 판단하고,
     *    이후 관련 ProductOptionItem, ProductOptionGroup 삭제 방식을 결정한다.
     *
     * 6. 옵션이 없는 상품은 가짜 기본 옵션 그룹/항목을 만들지 않고 기본 ProductVariant 1개만 생성한다.
     */
    private final ProductRegister productRegister;
    private final ProductListViewer productListViewer;
    private final ProductDetailViewer productDetailViewer;
    private final ProductRemover productRemover;
    private final ProductModifier productModifier;
    private final ProductRepository productRepository;

    @Transactional
    public void register(ProductRegisterRequest request, String sellerUid) {
        productRegister.register(request, sellerUid);
    }

    @Transactional
    public PageResponse<ManagementProductListResponse> getProductPageInfoForManagement(ManagementProductSearchCommand command) {
        return productListViewer.getProductPageInfoForManagement(command);
    }

    @Transactional
    public ProductDetailResponse getProductDetailForManagement(int prodNo) {
        return productDetailViewer.getProductDetailForManagement(prodNo);
    }

    @Transactional
    public ProductDetailResponse getProductDetail(int prodNo) {
        productRepository.increaseHit(prodNo);
        return productDetailViewer.getProductDetailForManagement(prodNo);
    }

    public PageResponse<ProductListResponse> getProductPageInfo(ProductListRequest request) {
        return productListViewer.getProductPageInfo(request);
    }

    public PageResponse<ProductSearchResponse> getProductSearchPageInfo(ProductSearchRequest request) {
        return productListViewer.getProductSearchPageInfo(request);
    }

    public List<BestProductResponse> getBestProducts() {
        return productRepository.findBestProducts(PageRequest.of(0, 5))
            .stream()
            .map(BestProductResponse::from)
            .toList();
    }

    public List<ProductListResponse> getMainProducts(MainProductSortType sortType) {
        return productListViewer.getMainProducts(sortType, 8);
    }

    @Transactional
    public ProductRemovalResult remove(List<Integer> prodNos) {
        return productRemover.removeAll(prodNos);
    }

    @Transactional
    public void modify(int prodNo, ProductRegisterRequest request) {
        productModifier.modify(prodNo, request);
    }
}
