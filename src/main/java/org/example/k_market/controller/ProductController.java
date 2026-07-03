package org.example.k_market.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 상품목록 페이지 매핑
 *
 * templates/product/list.html ↔ GET /product/list
 *
 * 쿼리 파라미터:
 *  - category : 1차 또는 2차 카테고리 ID (필수)
 *  - sort     : 정렬 기준 (기본값 sales)
 *               sales(판매많은순) | priceAsc(낮은가격순) | priceDesc(높은가격순)
 *               ratingDesc(평점높은순) | reviewDesc(후기많은순) | latest(최근등록순)
 *  - page     : 페이지 번호 (기본값 1), 10개씩 페이징
 */
@Controller
public class ProductController {

    private static final int PAGE_SIZE = 10;

    @GetMapping("/product/list")
    public String list(
            @RequestParam(required = false) Long category,
            @RequestParam(defaultValue = "sales") String sort,
            @RequestParam(defaultValue = "1") int page,
            Model model
    ) {
        model.addAttribute("categoryId", category);
        model.addAttribute("sort", sort);
        model.addAttribute("page", page);

        // aside의 베스트 상품을 보여주기 위해 필요 (빈 리스트면 placeholder, 실제 데이터면 그 데이터)
        model.addAttribute("bestProducts", java.util.List.of());

        // ── 카테고리명 / 브레드크럼 정보 (실제로는 CategoryService에서 조회) ──
        // model.addAttribute("categoryName", categoryService.findName(category));
        // model.addAttribute("parentCategoryId", categoryService.findParentId(category));
        // model.addAttribute("parentCategoryName", categoryService.findParentName(category));

        // ── 정렬 기준에 따라 상품 목록 조회 (실제로는 ProductService에서 처리) ──
        // Sort sortOption = switch (sort) {
        //     case "priceAsc"    -> Sort.by("price").ascending();
        //     case "priceDesc"   -> Sort.by("price").descending();
        //     case "ratingDesc"  -> Sort.by("rating").descending();
        //     case "reviewDesc"  -> Sort.by("reviewCount").descending();
        //     case "latest"      -> Sort.by("createdAt").descending();
        //     default            -> Sort.by("salesCount").descending(); // sales
        // };
        // Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, sortOption);
        // Page<Product> result = productService.findByCategory(category, pageable);
        // model.addAttribute("productList", result.getContent());
        // model.addAttribute("totalPages", result.getTotalPages());

        return "product/list";
    }

    @GetMapping("/product/view")
    public String view() {

        // 💡 나중에는 여기서 파라미터(id 등)를 받아서 DB에서 상품 정보를 조회한 뒤
        // model.addAttribute("product", productInfo); 처럼 넘겨주는 로직이 추가됩니다.
        // 하지만 지금은 화면만 띄우면 되므로 아주 심플합니다!

        return "product/view"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }

    @GetMapping("/product/cart")
    public String cart() {

        return "product/cart"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }

    @GetMapping("/product/order")
    public String order() {

        return "product/order"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }

    @GetMapping("/product/complete")
    public String complete() {

        return "product/complete"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }

    @GetMapping("/product/search")
    public String search() {

        return "product/search"; // templates/product 폴더 안의 view.html을 화면에 띄워라!
    }
}