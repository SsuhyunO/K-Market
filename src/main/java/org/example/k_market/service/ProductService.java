package org.example.k_market.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.pagination.request.PageRequest;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.dto.product.response.ProductDetailResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.service.product.ProductDetailViewer;
import org.example.k_market.service.product.ProductListViewer;
import org.example.k_market.service.product.ProductRegister;
import org.example.k_market.service.product.ProductRemover;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    /*
     * 상품/옵션 삭제 정책
     *
     * 1. 주문/장바구니 기능이 완성되기 전까지 상품 삭제는 임시로 hard delete 처리한다.
     *    상품 본문, 상품정보 제공고시, 옵션 그룹, 옵션 항목, 옵션 조합, 업로드 파일을 함께 삭제한다.
     *
     * 2. 주문/장바구니 기능이 연결되면 상품이 장바구니 또는 주문에서 참조 중인지 확인해야 한다.
     *    - 장바구니 참조 여부는 Cart.prodNo 기준으로 판단한다.
     *    - 주문 참조 여부는 OrderItem.prodVariantId -> ProductVariant.prodNo 기준으로 판단한다.
     *    - 참조 중인 상품은 주문/장바구니 이력 보존을 위해 삭제하지 않고 판매중지 또는 soft delete 정책으로 처리한다.
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

    @Transactional
    public void register(ProductRegisterRequest request, String sellerUid) {
        productRegister.register(request, sellerUid);
    }

    @Transactional
    public PageResponse<ProductListResponse> getProductPageInfo(PageRequest request) {
        return productListViewer.getProductPageInfo(request);
    }

    @Transactional
    public ProductDetailResponse getProductDetail(int prodNo) {
        return productDetailViewer.getProductDetail(prodNo);
    }

    @Transactional
    public void remove(List<Integer> prodNos) {
        productRemover.removeAll(prodNos);
    }
}
