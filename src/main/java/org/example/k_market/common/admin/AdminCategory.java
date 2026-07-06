package org.example.k_market.common.admin;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

@Getter
@ToString
public enum AdminCategory {
    MAIN("/admin/main", "관리자 메인", "HOME &rsaquo; 관리자 메인", "/admin"),
    ADMIN_ERROR("/admin/error", "페이지를 찾을 수 없습니다", "HOME &rsaquo; 오류"),
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
    PRODUCT_EDIT("/admin/product/edit", "상품수정", "HOME &rsaquo; 상품관리 &rsaquo; 상품수정"),
    ORDER_STATUS("/admin/order/order-list", "주문현황", "HOME &rsaquo; 주문관리 &rsaquo; 주문현황"),
    DELIVERY_STATUS("/admin/order/delivery-list", "배송현황", "HOME &rsaquo; 주문관리 &rsaquo; 배송현황"),
    COUPON_LIST("/admin/coupon/list", "쿠폰목록", "HOME &rsaquo; 쿠폰관리 &rsaquo; 쿠폰목록"),
    COUPON_ISSUED("/admin/coupon/used", "쿠폰발급현황", "HOME &rsaquo; 쿠폰관리 &rsaquo; 쿠폰발급현황"),
    CS_NOTICE_LIST("/admin/cs/notice/list", "공지사항 목록", "HOME &rsaquo; 고객센터 &rsaquo; 공지사항 목록", "/admin/cs"),
    CS_NOTICE_VIEW("/admin/cs/notice/view", "공지사항 보기", "HOME &rsaquo; 고객센터 &rsaquo; 공지사항 보기"),
    CS_NOTICE_WRITE("/admin/cs/notice/write", "공지사항 작성", "HOME &rsaquo; 고객센터 &rsaquo; 공지사항 작성"),
    CS_NOTICE_MODIFY("/admin/cs/notice/modify", "공지사항 수정", "HOME &rsaquo; 고객센터 &rsaquo; 공지사항 수정"),
    CS_FAQ_LIST("/admin/cs/faq/list", "자주묻는질문 목록", "HOME &rsaquo; 고객센터 &rsaquo; 자주묻는질문 목록"),
    CS_FAQ_VIEW("/admin/cs/faq/view", "자주묻는질문 보기", "HOME &rsaquo; 고객센터 &rsaquo; 자주묻는질문 보기"),
    CS_FAQ_WRITE("/admin/cs/faq/write", "자주묻는질문 작성", "HOME &rsaquo; 고객센터 &rsaquo; 자주묻는질문 작성"),
    CS_FAQ_MODIFY("/admin/cs/faq/modify", "자주묻는질문 수정", "HOME &rsaquo; 고객센터 &rsaquo; 자주묻는질문 수정"),
    CS_QNA_LIST("/admin/cs/qna/list", "문의하기 목록", "HOME &rsaquo; 고객센터 &rsaquo; 문의하기"),
    CS_QNA_VIEW("/admin/cs/qna/view", "문의하기 보기", "HOME &rsaquo; 고객센터 &rsaquo; 문의하기"),
    CS_QNA_REPLY("/admin/cs/qna/reply", "문의하기 답변", "HOME &rsaquo; 고객센터 &rsaquo; 문의하기"),
    CS_RECRUIT("/admin/cs/recruit/list", "채용하기 목록", "HOME &rsaquo; 고객센터 &rsaquo; 채용하기");

    private final String path;
    private final String title;
    private final String nav;
    private final Set<String> aliases;

    AdminCategory(String path, String title, String nav, String... aliases) {
        this.path = path;
        this.title = title;
        this.nav = nav;
        this.aliases = Set.of(aliases);
    }

    public static Optional<AdminCategory> fromPath(String path) {
        if ("/".equals(path)) {
            return Optional.of(MAIN);
        }

        return Arrays.stream(values())
                .filter(category -> category.matches(path))
                .findFirst();
    }

    private boolean matches(String path) {
        return this.path.equals(path) || aliases.contains(path);
    }
}
