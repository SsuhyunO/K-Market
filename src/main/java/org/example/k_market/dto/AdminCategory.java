package org.example.k_market.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
@ToString
public enum AdminCategory {
    MAIN("/admin/main", "관리자 메인", "HOME &rsaquo; 관리자 메인"),
    SITE_SETTINGS("/admin/site-settings", "기본설정", "HOME &rsaquo; 환경설정 &rsaquo; 기본설정"),
    BANNER_MANAGEMENT("/admin/banner-management", "배너관리", "HOME &rsaquo; 환경설정 &rsaquo; 배너관리"),
    TERMS_MANAGEMENT("/admin/terms-management", "약관관리", "HOME &rsaquo; 환경설정 &rsaquo; 약관관리"),
    VERSION_MANAGEMENT("/admin/version-management", "버전관리", "HOME &rsaquo; 환경설정 &rsaquo; 버전관리"),
    CATEGORY("/admin/category-management", "카테고리", "HOME &rsaquo; 환경설정 &rsaquo; 카테고리"),
    SHOP_LIST("/admin/shop-list", "상점목록", "HOME &rsaquo; 상점관리 &rsaquo; 상점목록"),
    SALES_STATUS("/admin/sales-status", "매출현황", "HOME &rsaquo; 상점관리 &rsaquo; 매출현황"),
    MEMBER_LIST("/admin/member-list", "회원목록", "HOME &rsaquo; 회원관리 &rsaquo; 회원목록"),
    MANAGEMENT_POINT("/admin/management-point", "포인트관리", "HOME &rsaquo; 회원관리 &rsaquo; 포인트관리"),
    PRODUCT_LIST("/admin/product/list", "상품목록", "HOME &rsaquo; 상품관리 &rsaquo; 상품목록"),
    PRODUCT_REGISTER("/admin/product/register", "상품등록", "HOME &rsaquo; 상품관리 &rsaquo; 상품등록"),
    PRODUCT_EDIT("/admin/product/edit", "상품수정", "HOME &rsaquo; 상품관리 &rsaquo; 상품수정");

    private final String path;
    private final String title;
    private final String nav;

    public static Optional<AdminCategory> fromPath(String path) {
        if ("/".equals(path)) {
            return Optional.of(MAIN);
        }

        return Arrays.stream(values())
                .filter(category -> category.path.equals(path))
                .findFirst();
    }
}
