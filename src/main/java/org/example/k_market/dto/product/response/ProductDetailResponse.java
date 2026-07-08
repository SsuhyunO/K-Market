package org.example.k_market.dto.product.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private int prodNo;
    private int cateId;
    private String prodName;
    private String description;
    private String maker;
    private int deliveryFee;
    private int price;
    private int discount;
    private int point;
    private int sold;
    private int stock;
    private Integer rootCategoryId;
    private String rootCategoryName;
    private String subCategoryName;
    private Integer thumb1FileId;
    private Integer thumb2FileId;
    private Integer thumb3FileId;
    private Integer detailInfoFileId;
    private FileInfo thumb1File;
    private FileInfo thumb2File;
    private FileInfo thumb3File;
    private FileInfo detailInfoFile;
    private String sellerUid;
    private String infoNoticeType;
    private String infoNoticeTypeName;
    private String createdAt;

    @Builder.Default
    private List<OptionGroup> optionGroups = new ArrayList<>();

    @Builder.Default
    private List<Variant> variants = new ArrayList<>();

    @Builder.Default
    private List<NoticeValue> noticeValues = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionGroup {
        private int id;
        private String name;

        @Builder.Default
        private List<OptionItem> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionItem {
        private int id;
        private String value;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Variant {
        private int id;
        private int stock;
        private String status;

        @Builder.Default
        private List<OptionItem> items = new ArrayList<>();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeValue {
        private String key;
        private String label;
        private String value;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileInfo {
        private int id;
        private String originalName;
        private String extension;
        private Long fileSize;
    }
}
