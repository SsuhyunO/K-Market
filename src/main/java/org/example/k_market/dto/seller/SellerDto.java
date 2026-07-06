package org.example.k_market.dto.seller;

import org.example.k_market.entity.Seller;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class SellerDto {

    // 판매자 신청(등록) 요청
    @Getter
    @NoArgsConstructor
    public static class RegisterRequest {

        @NotBlank(message = "상호명은 필수입니다.")
        private String companyName;

        @NotBlank(message = "사업자등록번호는 필수입니다.")
        private String bizRegNo;

        private String onlineSalesNo;
        private String tel;
        private String fax;

        public Seller toEntity(String uid) {
            return Seller.builder()
                    .uid(uid)
                    .companyName(companyName)
                    .bizRegNo(bizRegNo)
                    .onlineSalesNo(onlineSalesNo)
                    .tel(tel)
                    .fax(fax)
                    .build();
        }
    }

    // 판매자 정보 응답
    @Getter
    @Builder
    public static class Response {
        private String uid;
        private String companyName;
        private String bizRegNo;
        private String onlineSalesNo;
        private String tel;
        private String fax;

        public static Response from(Seller seller) {
            return Response.builder()
                    .uid(seller.getUid())
                    .companyName(seller.getCompanyName())
                    .bizRegNo(seller.getBizRegNo())
                    .onlineSalesNo(seller.getOnlineSalesNo())
                    .tel(seller.getTel())
                    .fax(seller.getFax())
                    .build();
        }
    }
}