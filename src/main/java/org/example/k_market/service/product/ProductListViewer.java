package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.ProductDAO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ManagementProductSearchCommand;
import org.example.k_market.dto.product.request.ManagementProductSearchRequest;
import org.example.k_market.dto.product.response.ManagementProductListResponse;
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

    public PageResponse<ManagementProductListResponse> getProductPageInfoForManagement(ManagementProductSearchCommand command) {
        ManagementProductSearchRequest request = command.getRequest();
        System.out.println(command);
        String type = request.getType() != null
            ? request.getType().name()
            : null;

        return paginationService.getPageInfo(
            request.getPage(),
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                public List<ManagementProductListResponse> fetch(int offset, int size) {
                    return productDAO.findProductsForManagement(
                        size,
                        offset,
                        command.getSellerUid(),
                        command.getRole(),
                        type,
                        request.getKeyword());
                }

                @Override
                public int count() {
                    return 0; // 카운트가 필요없는 페이지
                }
            }
        );
    }

    public PageResponse<ProductListResponse> getProductPageInfo() {
        return null;
    }
}
