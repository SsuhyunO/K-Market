package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.ProductDAO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ProductSearchCommand;
import org.example.k_market.dto.product.request.ProductSearchRequest;
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

    public PageResponse<ProductListResponse> getProductPageInfo(ProductSearchCommand command) {
        ProductSearchRequest request = command.getRequest();
        System.out.println(command);
        String type = request.getType() != null
            ? request.getType().name()
            : null;

        return paginationService.getPageInfo(
            request.getPage(),
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                public List<ProductListResponse> fetch(int offset, int size) {
                    return productDAO.findProductsForPage(
                        size,
                        offset,
                        command.getSellerUid(),
                        command.getRole(),
                        type,
                        request.getKeyword());
                }

                @Override
                public int count() {
                    return productDAO.totalCount(
                        command.getSellerUid(),
                        command.getRole(),
                        type,
                        request.getKeyword());
                }
            }
        );
    }
}
