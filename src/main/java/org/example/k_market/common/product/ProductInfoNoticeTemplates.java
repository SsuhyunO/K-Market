package org.example.k_market.common.product;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ProductInfoNoticeTemplates {

    private static final Map<String, ProductInfoNoticeTemplate> TEMPLATES = createTemplates();

    private ProductInfoNoticeTemplates() {
    }

    public static Map<String, ProductInfoNoticeTemplate> all() {
        return TEMPLATES;
    }

    private static Map<String, ProductInfoNoticeTemplate> createTemplates() {
        Map<String, ProductInfoNoticeTemplate> templates = new LinkedHashMap<>();

        put(templates, template("clothing", "의류",
                        field("material", "제품 소재", "겉감/안감/충전재 등", true),
                        field("color", "색상", "예: 블랙, 화이트", true),
                        field("size", "치수", "예: S/M/L, 95/100/105", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("care", "세탁방법 및 취급시 주의사항", "세탁/건조/다림질 주의사항", true),
                        field("manufacturedAt", "제조연월", "예: 2026.06", false),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("shoes", "구두/신발",
                        field("material", "제품 소재", "갑피/창 소재", true),
                        field("color", "색상", "예: 블랙", true),
                        field("size", "치수", "예: 250mm", true),
                        field("height", "굽 높이", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("care", "취급시 주의사항", "오염 제거/보관 주의사항", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("bag", "가방",
                        field("kind", "종류", "예: 백팩, 숄더백", true),
                        field("material", "소재", "예: 천연가죽, 폴리에스터", true),
                        field("color", "색상", "예: 네이비", true),
                        field("size", "크기", "가로 x 세로 x 폭", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 중국", true),
                        field("care", "취급시 주의사항", "세탁/보관 주의사항", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("digital", "영상/음향/디지털가전",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "예: 220V, 60Hz", true),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/무게", "제품 크기 및 무게", true),
                        field("spec", "주요 사양", "주요 기능/성능", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("food", "식품",
                        field("foodType", "식품의 유형", "예: 과자, 즉석조리식품", true),
                        field("producer", "생산자 및 소재지", "제조원/소재지", true),
                        field("manufacturedAt", "제조연월일", "해당 시 입력", false),
                        field("expiration", "소비기한 또는 품질유지기한", "예: 별도 표기일까지", true),
                        field("capacity", "포장단위별 내용물의 용량/수량", "예: 500g x 2개", true),
                        field("ingredients", "원재료명 및 함량", "주요 원재료와 함량", true),
                        field("nutrition", "영양성분", "해당 시 입력", false),
                        field("geneticallyModified", "유전자변형식품 여부", "해당/해당 없음", false),
                        field("imported", "수입식품 여부", "해당/해당 없음", false),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("healthFood", "건강기능식품",
                        field("productName", "제품명", "제품명", true),
                        field("foodType", "식품의 유형", "건강기능식품", true),
                        field("producer", "제조업소명 및 소재지", "제조원/소재지", true),
                        field("expiration", "소비기한", "예: 별도 표기일까지", true),
                        field("capacity", "포장단위별 내용물의 용량/수량", "예: 60정", true),
                        field("ingredients", "원료명 및 함량", "기능성 원료 포함", true),
                        field("nutrition", "영양정보", "1일 섭취량 기준", true),
                        field("functionality", "기능정보", "인정받은 기능성 내용", true),
                        field("intakeGuide", "섭취량/섭취방법 및 주의사항", "1일 섭취량 및 주의사항", true),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("cosmetics", "화장품",
                        field("capacity", "내용물의 용량 또는 중량", "예: 50ml", true),
                        field("spec", "제품 주요 사양", "피부타입/색상 등", true),
                        field("expiration", "사용기한 또는 개봉 후 사용기간", "예: 개봉 후 12개월", true),
                        field("usage", "사용방법", "사용 순서와 방법", true),
                        field("manufacturer", "화장품제조업자/책임판매업자", "제조업자 및 책임판매업자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("ingredients", "전성분", "전성분 표시", true),
                        field("functional", "기능성 화장품 여부", "해당/해당 없음", true),
                        field("caution", "사용할 때의 주의사항", "주의사항", true),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("furniture", "가구",
                        field("productName", "품명", "상품명", true),
                        field("certification", "KC 인증정보", "해당 시 입력", false),
                        field("color", "색상", "예: 오크", true),
                        field("components", "구성품", "예: 본체, 설명서", true),
                        field("material", "주요 소재", "목재/금속/패브릭 등", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 베트남", true),
                        field("size", "크기", "가로 x 세로 x 높이", true),
                        field("deliveryInstall", "배송/설치비용", "별도 비용 여부", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("kitchen", "주방용품",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("material", "재질", "스테인리스, 유리 등", true),
                        field("components", "구성품", "구성품 및 수량", true),
                        field("size", "크기", "제품 크기", true),
                        field("releasedAt", "출시년월", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("importDeclaration", "수입식품안전관리 특별법 표시", "해당 시 입력", false),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("baby", "영유아용품",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "어린이제품 인증정보", true),
                        field("size", "크기/중량", "제품 크기 및 중량", true),
                        field("color", "색상", "예: 아이보리", false),
                        field("material", "재질", "주요 소재", true),
                        field("recommendedAge", "사용연령 또는 권장사용연령", "예: 3세 이상", true),
                        field("releasedAt", "동일모델의 출시년월", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("caution", "취급방법 및 취급시 주의사항", "안전 주의사항", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("book", "서적",
                        field("title", "도서명", "도서명", true),
                        field("author", "저자/출판사", "저자 및 출판사", true),
                        field("size", "크기", "판형 또는 크기", false),
                        field("pages", "쪽수", "예: 320쪽", false),
                        field("components", "제품 구성", "전권 수, 부록 등", false),
                        field("publishedAt", "발행일", "예: 2026.06.30", true),
                        field("content", "목차 또는 책소개", "요약 정보", false)
        ));
        put(templates, template("etc", "기타 재화",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "법에 의한 인증/허가", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국 또는 원산지", "예: 대한민국", true),
                        field("size", "크기/중량", "해당 시 입력", false),
                        field("spec", "주요 사양", "상품 특성", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));

        return Collections.unmodifiableMap(templates);
    }

    private static ProductInfoNoticeTemplate template(String code, String name, ProductInfoNoticeField... fields) {
        return new ProductInfoNoticeTemplate(code, name, List.of(fields));
    }

    private static void put(Map<String, ProductInfoNoticeTemplate> templates, ProductInfoNoticeTemplate template) {
        templates.put(template.code(), template);
    }

    private static ProductInfoNoticeField field(String key, String label, String placeholder, boolean required) {
        return new ProductInfoNoticeField(key, label, placeholder, required);
    }
}
