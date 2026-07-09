package org.example.k_market.dto.product.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateRequest {
    private int prodNo;
    private int cateId;
    private String prodName;
    private String description;
    private String maker;
    private int deliveryFee;
    private int price;
    private int discount;
    private int point;

    private Integer thumb1FileId;
    private Integer thumb2FileId;
    private Integer thumb3FileId;
    private Integer detailInfoFileId;

    @Builder.Default
    private List<OptionGroup> optionGroups = new ArrayList<>();

    @Builder.Default
    private List<Variant> variants = new ArrayList<>();

    @Builder.Default
    private List<NoticeValue> noticeValues = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionGroup {
        private Integer id;  // 기존 옵션그룹이면 id 있음, 신규면 null
        private String name;

        @Builder.Default
        private List<OptionItem> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionItem {
        private Integer id;      // 기존 아이템이면 id 있음, 신규면 null
        private String value;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Variant {
        private Integer id;      // 기존 조합이면 id 있음, 신규면 null
        private int stock;
        private String status;

        @Builder.Default
        private List<OptionItem> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeValue {
        private String key;
        private String value;
    }
}