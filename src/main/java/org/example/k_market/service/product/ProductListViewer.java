package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.ProductDAO;
import org.example.k_market.dto.pagination.request.PageRequest;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.service.pagination.PageQuery;
import org.example.k_market.service.pagination.PaginationService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductListViewer {
    private static final int PRODUCT_LIST_SIZE = 10;
    private static final int PRODUCT_PAGE_BLOCK_SIZE = 10;

    private final PaginationService paginationService;
    private final ProductDAO productDAO;

    public PageResponse<ProductListResponse> getProductPageInfo(PageRequest request) {
        return paginationService.getPageInfo(
            request,
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<ProductListResponse>() {
                @Override
                public List<ProductListResponse> fetch(int offset, int size) {
                    return productDAO.findProductsForPage(size, offset);
                }

                @Override
                public int count() {
                    return productDAO.totalCount();
                }
            }
        );
    }
}
