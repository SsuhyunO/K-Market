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
        put(templates, template("fashionAccessories", "패션잡화(모자/벨트/액세서리)",
                        field("kind", "종류", "예: 모자, 벨트, 액세서리", true),
                        field("material", "소재", "주요 소재", true),
                        field("size", "치수", "제품 크기 또는 착용 치수", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("care", "취급시 주의사항", "착용/보관 주의사항", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("beddingCurtain", "침구류/커튼",
                        field("material", "제품 소재", "겉감/충전재 등", true),
                        field("color", "색상", "예: 화이트", true),
                        field("size", "치수", "가로 x 세로", true),
                        field("components", "제품 구성", "구성품 및 수량", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("care", "세탁방법 및 취급시 주의사항", "세탁/보관 주의사항", true),
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
        put(templates, template("homeAppliance", "가정용 전기제품",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "예: 220V, 60Hz", true),
                        field("energyEfficiency", "에너지소비효율등급", "해당 시 입력", false),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/용량/형태", "제품 크기 및 용량", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("seasonalAppliance", "계절가전",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "예: 220V, 60Hz", true),
                        field("energyEfficiency", "에너지소비효율등급", "해당 시 입력", false),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("area", "냉난방면적", "해당 시 입력", false),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("officeDevice", "사무용기기",
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
        put(templates, template("opticalDevice", "광학기기",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "해당 시 입력", false),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/무게", "제품 크기 및 무게", true),
                        field("spec", "주요 사양", "렌즈/배율/센서 등", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("smallDigital", "소형전자",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "충전/전원 사양", true),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/무게", "제품 크기 및 무게", true),
                        field("spec", "주요 사양", "주요 기능/성능", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("mobilePhone", "휴대폰",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/무게", "제품 크기 및 무게", true),
                        field("telecomSpec", "이동통신 가입조건", "공기계/약정/요금제 등", false),
                        field("spec", "주요 사양", "화면/저장공간/카메라 등", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("navigation", "내비게이션",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "KC 인증정보", "인증번호 또는 해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "전원 사양", true),
                        field("releasedAt", "출시년월", "예: 2026.06", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기/무게", "제품 크기 및 무게", true),
                        field("mapUpdate", "맵 업데이트 비용 및 무상기간", "업데이트 정책", true),
                        field("warranty", "품질보증기준", "보증기간 및 기준", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("carGoods", "자동차용품",
                        field("modelName", "품명 및 모델명", "상품명/모델명", true),
                        field("certification", "동일모델 출시년월/KC 인증정보", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "크기", "제품 크기", true),
                        field("applicableCar", "적용차종", "사용 가능한 차종", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("medicalDevice", "의료기기",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("license", "의료기기 허가/인증/신고번호", "허가/인증/신고번호", true),
                        field("advertisingReview", "광고사전심의필 유무", "해당/해당 없음", true),
                        field("ratedVoltage", "정격전압/소비전력", "전기용품인 경우 입력", false),
                        field("purpose", "제품의 사용목적 및 사용방법", "사용 목적/방법", true),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("caution", "취급시 주의사항", "주의사항", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
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
        put(templates, template("jewelry", "귀금속/보석/시계류",
                        field("material", "소재/순도/밴드재질", "금속/보석/시계 밴드 재질", true),
                        field("weight", "중량", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("size", "치수", "반지 호수/시계 크기 등", true),
                        field("care", "착용시 주의사항", "착용/보관 주의사항", true),
                        field("grade", "주요 사양", "보석 등급/시계 기능 등", true),
                        field("warranty", "보증서 제공 여부 및 품질보증기준", "보증서/보증기간", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
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
        put(templates, template("musicalInstrument", "악기",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("size", "크기", "제품 크기", true),
                        field("color", "색상", "예: 블랙", false),
                        field("material", "재질", "주요 소재", true),
                        field("components", "제품 구성", "구성품 및 수량", true),
                        field("releasedAt", "출시년월", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("sportsGoods", "스포츠용품",
                        field("productName", "품명 및 모델명", "상품명/모델명", true),
                        field("size", "크기/중량", "제품 크기 및 중량", true),
                        field("color", "색상", "예: 블랙", false),
                        field("material", "재질", "주요 소재", true),
                        field("components", "제품 구성", "구성품 및 수량", true),
                        field("releasedAt", "출시년월", "해당 시 입력", false),
                        field("manufacturer", "제조자", "제조자 또는 수입자", true),
                        field("origin", "제조국", "예: 대한민국", true),
                        field("warranty", "품질보증기준", "관련 법 및 소비자분쟁해결기준에 따름", true),
                        field("asContact", "A/S 책임자와 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("hotelPension", "호텔/펜션 예약",
                        field("provider", "서비스 제공 사업자", "업체명", true),
                        field("location", "이용 장소", "주소/지역", true),
                        field("roomType", "객실 종류", "객실명/타입", true),
                        field("checkInOut", "이용 일시", "체크인/체크아웃", true),
                        field("capacity", "이용 정원", "기준/최대 인원", true),
                        field("included", "포함 내역", "조식/부대시설 등", true),
                        field("cancelPolicy", "취소/환불 규정", "취소 마감 및 수수료", true),
                        field("customerCare", "예약/문의 연락처", "고객센터 연락처", true)
        ));
        put(templates, template("travelPackage", "여행패키지",
                        field("provider", "여행사", "여행 주최/판매사", true),
                        field("travelPeriod", "여행기간 및 일정", "출발/도착/상세 일정", true),
                        field("destination", "여행지", "방문 국가/지역", true),
                        field("included", "포함 내역", "항공/숙박/식사/입장권 등", true),
                        field("excluded", "불포함 내역", "개인 경비 등", true),
                        field("minimumPeople", "최소 출발 인원", "예: 10명", true),
                        field("guide", "가이드/인솔자 정보", "동행 여부", true),
                        field("cancelPolicy", "취소/환불 규정", "취소 마감 및 수수료", true),
                        field("customerCare", "예약/문의 연락처", "고객센터 연락처", true)
        ));
        put(templates, template("airlineTicket", "항공권",
                        field("airline", "항공사", "항공사명", true),
                        field("route", "이용 구간", "출발지/도착지", true),
                        field("schedule", "이용 일시", "출발/도착 일시", true),
                        field("ticketType", "항공권 종류", "왕복/편도/좌석 등급", true),
                        field("included", "포함 내역", "유류할증료/세금 등", true),
                        field("validity", "유효기간", "발권/탑승 가능 기간", true),
                        field("cancelPolicy", "취소/환불 규정", "변경/취소 수수료", true),
                        field("customerCare", "예약/문의 연락처", "고객센터 연락처", true)
        ));
        put(templates, template("carRental", "자동차 대여 서비스",
                        field("provider", "서비스 제공 사업자", "업체명", true),
                        field("carModel", "차종", "차량명/등급", true),
                        field("rentalPeriod", "대여 기간", "대여/반납 일시", true),
                        field("rentalLocation", "대여/반납 장소", "지점/주소", true),
                        field("insurance", "보험 포함 여부", "자차/대인/대물 등", true),
                        field("fuelPolicy", "연료 정책", "반납 조건", true),
                        field("driverCondition", "이용 자격", "면허/연령 조건", true),
                        field("cancelPolicy", "취소/환불 규정", "취소 마감 및 수수료", true),
                        field("customerCare", "예약/문의 연락처", "고객센터 연락처", true)
        ));
        put(templates, template("rentalService", "물품 대여 서비스",
                        field("provider", "서비스 제공 사업자", "업체명", true),
                        field("productName", "대여 물품", "상품명/모델명", true),
                        field("rentalPeriod", "대여 기간", "대여/반납 일시", true),
                        field("components", "구성품", "대여 구성품", true),
                        field("deposit", "보증금", "보증금 여부/금액", false),
                        field("deliveryReturn", "배송/반납 방법", "수령/반납 조건", true),
                        field("damagePolicy", "파손/분실 책임", "배상 기준", true),
                        field("cancelPolicy", "취소/환불 규정", "취소 마감 및 수수료", true),
                        field("customerCare", "문의 연락처", "고객센터 연락처", true)
        ));
        put(templates, template("digitalContent", "디지털 콘텐츠",
                        field("producer", "제작자 또는 공급자", "제작/공급 업체", true),
                        field("terms", "이용조건 및 이용기간", "이용 가능 기간/조건", true),
                        field("format", "상품 제공 방식", "다운로드/스트리밍/코드 등", true),
                        field("minimumSpec", "최소 시스템 사양", "운영체제/기기 사양", false),
                        field("cancelPolicy", "청약철회 또는 환불 제한 사유", "환불 가능 조건", true),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("giftCertificate", "상품권/쿠폰",
                        field("issuer", "발행자", "발행 업체", true),
                        field("validity", "유효기간", "사용 가능 기간", true),
                        field("usage", "이용조건", "사용처/사용 조건", true),
                        field("amount", "권면금액 또는 제공 내용", "금액/혜택", true),
                        field("refundPolicy", "환불 조건 및 방법", "잔액 환불 기준", true),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("mobileCoupon", "모바일 쿠폰",
                        field("issuer", "발행자", "발행 업체", true),
                        field("validity", "유효기간", "사용 가능 기간", true),
                        field("usage", "이용조건", "사용처/사용 조건", true),
                        field("exchangeMethod", "교환 방법", "매장 제시/온라인 입력 등", true),
                        field("refundPolicy", "환불 조건 및 방법", "잔액 환불 기준", true),
                        field("customerCare", "소비자상담 관련 전화번호", "고객센터 연락처", true)
        ));
        put(templates, template("movieShowTicket", "영화/공연",
                        field("provider", "주최 또는 제공 사업자", "주최/기획/판매사", true),
                        field("title", "공연/영화명", "상품명", true),
                        field("location", "이용 장소", "상영관/공연장", true),
                        field("schedule", "이용 일시", "관람 일시", true),
                        field("seat", "좌석 정보", "좌석 등급/지정 여부", true),
                        field("ageLimit", "관람 등급", "연령 제한", true),
                        field("cancelPolicy", "취소/환불 규정", "취소 마감 및 수수료", true),
                        field("customerCare", "문의 연락처", "고객센터 연락처", true)
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
