package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dao.ProductDAO;
import org.example.k_market.dao.SellerDAO;
import org.example.k_market.dto.pagination.response.PageResponse;
import org.example.k_market.dto.product.command.ManagementProductSearchCommand;
import org.example.k_market.dto.product.request.ManagementProductSearchRequest;
import org.example.k_market.dto.product.request.ProductListRequest;
import org.example.k_market.dto.product.request.ProductSearchRequest;
import org.example.k_market.dto.product.response.ManagementProductListResponse;
import org.example.k_market.dto.product.response.ProductListResponse;
import org.example.k_market.dto.product.response.ProductSearchResponse;
import org.example.k_market.enums.product.MainProductSortType;
import org.example.k_market.dto.seller.response.SellerStatisticsResponse;
import org.example.k_market.service.pagination.PageQuery;
import org.example.k_market.service.pagination.PaginationService;
import org.example.k_market.service.seller.SellerGradeCalculator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductListViewer {
    private static final int PRODUCT_LIST_SIZE = 10;
    private static final int PRODUCT_PAGE_BLOCK_SIZE = 10;

    private final PaginationService paginationService;
    private final ProductDAO productDAO;
    private final SellerDAO sellerDAO;
    private final SellerGradeCalculator sellerGradeCalculator;

    public PageResponse<ManagementProductListResponse> getProductPageInfoForManagement(ManagementProductSearchCommand command) {
        ManagementProductSearchRequest request = command.getRequest();
        String type = request.getType() != null
            ? request.getType().name()
            : null;

        return paginationService.getPageInfo(
            request.getPage(),
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<ManagementProductListResponse> fetch(int offset, int size) {
                    return productDAO.findProductsForManagement(
                        size,
                        offset,
                        command.getSellerUid(),
                        command.getRole(),
                        type,
                        request.getKeyword(),
                        request.isUncategorizedOnly());
                }
                @Override
                public int count() {
                    return productDAO.totalCountForManagement(
                        command.getSellerUid(),
                        command.getRole(),
                        type,
                        request.getKeyword(),
                        request.isUncategorizedOnly());
                }
            }
        );
    }

    public PageResponse<ProductListResponse> getProductPageInfo(ProductListRequest request) {
        return paginationService.getPageInfo(
            request.getPage(),
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<ProductListResponse> fetch(int offset, int size) {
                    List<ProductListResponse> products = productDAO.findProductsForCustomer(
                        size,
                        offset,
                        request.getCateId(),
                        getSortTypeName(request.getSortType())
                    );

                    applySellerGrades(products);

                    return products;
                }

                @Override
                public int count() {
                    return productDAO.totalCountForCustomer(request.getCateId());
                }
            }
        );
    }

    public PageResponse<ProductSearchResponse> getProductSearchPageInfo(ProductSearchRequest request) {
        return paginationService.getPageInfo(
            request.getPage(),
            PRODUCT_LIST_SIZE,
            PRODUCT_PAGE_BLOCK_SIZE,
            new PageQuery<>() {
                @Override
                public List<ProductSearchResponse> fetch(int offset, int size) {
                    List<ProductSearchResponse> products = productDAO.findProductsForSearch(
                        size,
                        offset,
                        getSortTypeName(request.getSortType()),
                        normalizeKeyword(request.getKeyword()),
                        request.isName(),
                        request.isDescription(),
                        request.isPrice(),
                        request.getMinPrice(),
                        request.getMaxPrice()
                    );

                    applySellerGrades(products);

                    return products;
                }

                @Override
                public int count() {
                    return productDAO.totalCountForSearch(
                        normalizeKeyword(request.getKeyword()),
                        request.isName(),
                        request.isDescription(),
                        request.isPrice(),
                        request.getMinPrice(),
                        request.getMaxPrice()
                    );
                }
            }
        );
    }

    public List<ProductListResponse> getMainProducts(MainProductSortType sortType, int size) {
        List<ProductListResponse> products = productDAO.findProductsForMain(
            size,
            sortType.name()
        );

        applySellerGrades(products);

        return products;
    }

    private String getSortTypeName(org.example.k_market.enums.product.ProductSortType sortType) {
        return sortType != null
            ? sortType.name()
            : org.example.k_market.enums.product.ProductSortType.SALES.name();
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }

        return keyword.trim();
    }

    private <T extends ProductListResponse> void applySellerGrades(List<T> products) {
        List<String> sellerUids = products
            .stream()
            .map(ProductListResponse::getSellerUid)
            .filter(Objects::nonNull)
            .distinct()
            .toList();

        List<SellerStatisticsResponse> statistics = sellerUids.isEmpty()
            ? List.of()
            : sellerDAO.findStatisticsBySellerUids(sellerUids);

        Map<String, SellerStatisticsResponse> statisticsMap = statistics.stream()
            .collect(
                Collectors.toMap(SellerStatisticsResponse::getSellerUid,
                    Function.identity(),
                    (existing, replacement) -> existing));

        products.forEach(p -> p
            .setSellerGrade(sellerGradeCalculator.calculate(statisticsMap.get(p.getSellerUid()))));
    }
}
