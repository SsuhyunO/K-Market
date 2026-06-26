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
    CATEGORY("/admin/category", "카테고리", "HOME &rsaquo; 환경설정 &rsaquo; 카테고리");

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
